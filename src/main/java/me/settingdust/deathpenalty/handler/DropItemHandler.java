package me.settingdust.deathpenalty.handler;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.izzel.amber.commons.i18n.AmberLocale;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import me.settingdust.deathpenalty.Constants;
import me.settingdust.deathpenalty.ItemModuleService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent.Death;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.filter.cause.After;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent.Destruct;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.Text;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DropItemHandler {
    @Inject
    ServiceManager serviceManager;

    @Inject
    AmberLocale locale;

    EventManager eventManager;

    @Inject
    public DropItemHandler(EventManager eventManager, PluginContainer pluginContainer) {
        eventManager.registerListeners(pluginContainer, this);
        this.eventManager = eventManager;
    }

    @Listener
    @SneakyThrows
    public void onDestructEntityDeath(Death event, @After(DamageSource.class) Player player) {
        event.setKeepInventory(true);
        if (player.hasPermission(Constants.ID + ".disable.item")) {
            return;
        }

        val playerInventory = (PlayerInventory) player.getInventory();
        val droppedItems = Lists.<ItemStackSnapshot>newArrayList();
        val isModuleEnable = serviceManager.isRegistered(ItemModuleService.class);
        if (isModuleEnable) {
            val itemModuleService = serviceManager.provideUnchecked(ItemModuleService.class);
            droppedItems.addAll(getDroppedItems(playerInventory.getMainGrid()));
            droppedItems.addAll(getDroppedItems(playerInventory.getHotbar()));
            if (itemModuleService.isDropEquipment()) {
                droppedItems.addAll(getDroppedItems(playerInventory.getEquipment()));
                droppedItems.addAll(getDroppedItems(playerInventory.getOffhand()));
            }
        } else {
            var stackOptional = playerInventory.poll();
            while (stackOptional.isPresent()) {
                droppedItems.add(stackOptional.get().createSnapshot());
                stackOptional = playerInventory.poll();
            }
        }

        val location = player.getLocation();

        val dropItemEvent = SpongeEventFactory.createDropItemEventDestruct(
            Sponge.getCauseStackManager().getCurrentCause(),
            droppedItems
                .stream()
                .map(
                    item -> {
                        val entity = location
                            .getExtent()
                            .createEntity(
                                EntityTypes.ITEM,
                                location.add(Math.random() * 1.5, Math.random(), Math.random() * 1.5).getPosition()
                            );
                        entity.offer(Keys.REPRESENTED_ITEM, item);
                        entity.offer(Keys.PICKUP_DELAY, 15);
                        return entity;
                    }
                )
                .collect(Collectors.toList())
        );

        eventManager.post(dropItemEvent);
        if (!dropItemEvent.isCancelled()) {
            location.getExtent().spawnEntities(dropItemEvent.getEntities());
            if (isModuleEnable) {
                locale
                    .get("message.item")
                    .ifPresent(
                        text -> serviceManager
                            .provideUnchecked(ItemModuleService.class)
                            .sendMessage(
                                player,
                                Text
                                    .builder(text, text.toPlain())
                                    .onHover(
                                        TextActions.showText(
                                            Text.joinWith(
                                                Text.NEW_LINE,
                                                droppedItems
                                                    .parallelStream()
                                                    .map(
                                                        item -> item
                                                            .get(Keys.DISPLAY_NAME)
                                                            .orElse(Text.of(item.getTranslation()))
                                                            .concat(Text.of(" x" + item.getQuantity()))
                                                    )
                                                    .collect(Collectors.toList())
                                            )
                                        )
                                    )
                                    .build()
                            )
                    );
            }
        }
    }

    private List<ItemStackSnapshot> getDroppedItems(Inventory inventory) {
        val droppedItems = Lists.<ItemStackSnapshot>newArrayList();
        serviceManager
            .provide(ItemModuleService.class)
            .ifPresent(
                itemModuleService -> {
                    val slots = inventory.slots();
                    slots.forEach(
                        slot -> slot
                            .peek()
                            .map(ItemStack::createSnapshot)
                            // 过滤绑定诅咒物品
                            .filter(
                                item -> !item.get(Keys.ITEM_ENCHANTMENTS).isPresent() ||
                                item
                                    .get(Keys.ITEM_ENCHANTMENTS)
                                    .filter(
                                        enchantments -> enchantments
                                            .parallelStream()
                                            .map(Enchantment::getType)
                                            .noneMatch(type -> type.equals(EnchantmentTypes.BINDING_CURSE))
                                    )
                                    .isPresent()
                            )
                            .ifPresent(
                                item -> {
                                    if (itemModuleService.isDrop(item)) {
                                        droppedItems.add(item);
                                        slot.set(ItemStack.empty());
                                    }
                                }
                            )
                    );

                    int toDrop = itemModuleService.getMinDropCount() - droppedItems.size();
                    if (toDrop > 0) {
                        StreamSupport
                            .stream(slots.spliterator(), false)
                            .sorted(Comparator.comparingInt(Object::hashCode))
                            .filter(slot -> slot.peek().isPresent())
                            .filter(slot -> itemModuleService.notInWhiteList(slot.peek().get()))
                            .limit(toDrop)
                            .filter(slot -> droppedItems.add(slot.peek().get().createSnapshot()))
                            .forEach(slot -> slot.set(ItemStack.empty()));
                    }
                }
            );
        return droppedItems;
    }
}
