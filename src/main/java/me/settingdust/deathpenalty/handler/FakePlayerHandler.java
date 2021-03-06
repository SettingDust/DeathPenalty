package me.settingdust.deathpenalty.handler;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.AmberLocale;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.SneakyThrows;
import lombok.val;
import me.settingdust.deathpenalty.Constants;
import me.settingdust.deathpenalty.FakePlayerModuleService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent.Death;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.filter.cause.After;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageEvent.MessageFormatter;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.event.network.ClientConnectionEvent.Login;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakePlayerHandler {
    PluginContainer pluginContainer;

    @Inject
    AmberLocale locale;

    @Inject
    ServiceManager serviceManager;

    Set<UUID> humanSet = Sets.newHashSet();

    @Inject
    public FakePlayerHandler(EventManager eventManager, PluginContainer pluginContainer) {
        eventManager.registerListeners(pluginContainer, this);
        this.pluginContainer = pluginContainer;
    }

    @Listener
    public void onDestructEntityDeath(
        Death event,
        @First DamageSource damageSource,
        @After(DamageSource.class) Human human
    ) {
        serviceManager
            .provide(FakePlayerModuleService.class)
            .ifPresent(
                fakePlayerModuleService -> human
                    .getCreator()
                    .ifPresent(
                        creator -> {
                            if (humanSet.contains(human.getUniqueId())) {
                                fakePlayerModuleService.addUser(creator);
                            }
                        }
                    )
            );
    }

    @Listener
    public void onClientConnectionLogin(
        Login event,
        @Getter("getTargetUser") User user,
        @Getter("getToTransform") Transform<World> toTransform
    ) {
        humanSet.removeIf(
            uuid -> toTransform
                .getExtent()
                .getEntity(uuid)
                .filter(entity -> entity.getCreator().filter(Predicate.isEqual(user.getUniqueId())).isPresent())
                .filter(
                    entity -> {
                        entity.remove();
                        return true;
                    }
                )
                .isPresent()
        );
    }

    @Listener
    public void onSpawnPlayer(Join event, @Getter("getTargetEntity") Player player) {
        if (player.hasPermission(Constants.ID + ".disable.fakeplayer")) {
            return;
        }

        serviceManager
            .provide(FakePlayerModuleService.class)
            .filter(fakePlayerModuleService -> fakePlayerModuleService.hasUser(player.getUniqueId()))
            .ifPresent(
                fakePlayerModuleService -> {
                    fakePlayerModuleService.removeUser(player.getUniqueId());

                    val eventDeath = SpongeEventFactory.createDestructEntityEventDeath(
                        Sponge
                            .getCauseStackManager()
                            .pushCause(player)
                            .pushCause(DamageSource.builder().type(DamageTypes.CUSTOM).bypassesArmor().build())
                            .getCurrentCause(),
                        player.getMessageChannel(),
                        Optional.of(player.getMessageChannel()),
                        new MessageFormatter(),
                        player,
                        false,
                        false
                    );
                    Sponge.getEventManager().post(eventDeath);
                    if (!eventDeath.isCancelled()) {
                        player.offer(Keys.HEALTH, 0D);
                        locale
                            .get("message.fakePlayer")
                            .ifPresent(message -> fakePlayerModuleService.sendMessage(player, message));
                    }
                }
            );
    }

    @Listener
    public void onClientConnectionDisconnect(Disconnect event, @Getter("getTargetEntity") Player player) {
        if (player.hasPermission(Constants.ID + ".disable.fakeplayer")) {
            return;
        }

        serviceManager
            .provide(FakePlayerModuleService.class)
            .ifPresent(
                fakePlayerModuleService -> {
                    val human = (Human) player.getLocation().createEntity(EntityTypes.HUMAN);
                    humanSet.add(human.getUniqueId());
                    human.offer(Keys.DISPLAY_NAME, player.getDisplayNameData().displayName().get());
                    human.offer(Keys.HEALTH, player.getHealthData().health().get());
                    human.offer(Keys.MAX_HEALTH, player.getHealthData().maxHealth().get());
                    human.offer(Keys.AI_ENABLED, true);
                    human.setCreator(player.getUniqueId());
                    player.getLocation().spawnEntity(human);
                    human
                        .getNearbyEntities(8)
                        .stream()
                        .filter(entity -> entity instanceof Agent)
                        .map(entity -> (Agent) entity)
                        .forEach(agent -> agent.setTarget(human));
                    Task.builder().delay(fakePlayerModuleService.getTimeToLive(), TimeUnit.SECONDS).execute((Runnable) human::remove).submit(pluginContainer);
                }
            );
    }
}
