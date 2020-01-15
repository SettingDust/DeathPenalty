package me.settingdust.deathpenalty.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.izzel.amber.commons.i18n.AmberLocale;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import me.settingdust.deathpenalty.Constants;
import me.settingdust.deathpenalty.ExpModuleService;
import me.settingdust.deathpenalty.ItemModuleService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent.Death;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.filter.cause.After;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DropExpHandler {
    @Inject
    AmberLocale locale;

    @Inject
    ServiceManager serviceManager;

    @Inject
    public DropExpHandler(EventManager eventManager, PluginContainer pluginContainer) {
        eventManager.registerListeners(pluginContainer, this);
    }

    @SuppressWarnings({ "WrapperTypeMayBePrimitive", "OptionalGetWithoutIsPresent" })
    @SneakyThrows
    @Listener(order = Order.LATE)
    public void onDestructEntityDeath(Death event, @After(DamageSource.class) Player player) {
        if (player.hasPermission(Constants.ID + ".disable.exp")) {
            return;
        }

        val isModuleEnable = serviceManager.isRegistered(ExpModuleService.class);
        val totalExp = player.get(Keys.TOTAL_EXPERIENCE).get();
        var expToDrop = totalExp;
        var expToLose = 0;

        if (isModuleEnable) {
            val expModuleService = serviceManager.provideUnchecked(ExpModuleService.class);
            expToDrop = expModuleService.getExpToDrop(totalExp);
            expToLose = (int) (expToDrop * expModuleService.getLoseRatio());
        }

        player.offer(Keys.TOTAL_EXPERIENCE, player.get(Keys.TOTAL_EXPERIENCE).get() - expToDrop);
        expToDrop -= expToLose;

        val location = player.getLocation();
        val entity = location
            .getExtent()
            .createEntity(
                EntityTypes.EXPERIENCE_ORB,
                location.add(Math.random() * 1.5, Math.random(), Math.random() * 1.5).getPosition()
            );
        entity.offer(Keys.CONTAINED_EXPERIENCE, expToDrop);
        location.getExtent().spawnEntity(entity);

        if (isModuleEnable) {
            locale
                .get("message.exp", expToDrop, expToLose)
                .ifPresent(
                    message -> serviceManager.provideUnchecked(ExpModuleService.class).sendMessage(player, message)
                );
        }
    }
}
