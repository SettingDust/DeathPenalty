package me.settingdust.deathpenalty;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.izzel.amber.commons.i18n.AmberLocale;
import me.settingdust.deathpenalty.config.model.ItemModel;
import me.settingdust.deathpenalty.ItemModuleService.Impl;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.Text;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
@ImplementedBy(Impl.class)
public interface ItemModuleService {
    ItemModuleService sendMessage(MessageReceiver receiver, Text text);

    boolean isDrop();

    default boolean isDrop(ItemStackSnapshot item) {
        return inBlackList(item) || isDrop();
    }

    int getMinDropCount();

    boolean notInWhiteList(ItemType item);

    boolean inBlackList(ItemType item);

    ItemModuleService addToWhiteList(ItemType item);

    ItemModuleService addToBlackList(ItemType item);

    default boolean notInWhiteList(Item item) {
        return notInWhiteList(item.getItemType());
    }

    default boolean inBlackList(Item item) {
        return inBlackList(item.getItemType());
    }

    default ItemModuleService addToWhiteList(Item item) {
        this.addToWhiteList(item.getItemType());
        return this;
    }

    default ItemModuleService addToBlackList(Item item) {
        this.addToBlackList(item.getItemType());
        return this;
    }

    default boolean notInWhiteList(ItemStack item) {
        return notInWhiteList(item.getType());
    }

    default boolean notInWhiteList(ItemStackSnapshot item) {
        return notInWhiteList(item.getType());
    }

    default boolean inBlackList(ItemStackSnapshot item) {
        return inBlackList(item.getType());
    }

    default ItemModuleService addToWhiteList(ItemStack item) {
        this.addToWhiteList(item.getType());
        return this;
    }

    default ItemModuleService addToBlackList(ItemStack item) {
        this.addToBlackList(item.getType());
        return this;
    }

    boolean isDropEquipment();

    @Singleton
    final class Impl implements ItemModuleService {
        @Inject
        AmberLocale locale;

        @Inject
        ItemModel itemModel;

        @Override
        public ItemModuleService sendMessage(MessageReceiver receiver, Text text) {
            if (itemModel.isSendMessage()) {
                receiver.sendMessage(text);
            }
            return this;
        }

        @Override
        public boolean isDrop() {
            return Math.random() < itemModel.getRatio();
        }

        @Override
        public int getMinDropCount() {
            return itemModel.getMinDrop();
        }

        @Override
        public boolean notInWhiteList(ItemType item) {
            return !itemModel.getWhitelist().contains(item.getName());
        }

        @Override
        public boolean inBlackList(ItemType item) {
            return itemModel.getBlacklist().contains(item.getName());
        }

        @Override
        public ItemModuleService addToWhiteList(ItemType item) {
            itemModel.getWhitelist().add(item.getName());
            return this;
        }

        @Override
        public ItemModuleService addToBlackList(ItemType item) {
            itemModel.getBlacklist().add(item.getName());
            return this;
        }

        @Override
        public boolean isDropEquipment() {
            return itemModel.isDropEquipments();
        }
    }
}
