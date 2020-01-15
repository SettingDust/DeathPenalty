package me.settingdust.deathpenalty.config.model;

import com.google.common.collect.Sets;
import com.google.inject.ProvidedBy;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.settingdust.deathpenalty.config.provider.ItemModelProvider;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

@Data
@ConfigSerializable
@ProvidedBy(ItemModelProvider.class)
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemModel {
    @Setting
    boolean sendMessage = true;

    @Setting(comment = "The percentage to drop (< 1)")
    double ratio = 0.15;

    @Setting(comment = "The count that drop randomly at least")
    int minDrop = 4;

    @Setting(comment = "Whether drop the equipments")
    boolean dropEquipments = true;

    @Setting(comment = "The items that NOT be dropped")
    Set<String> whitelist = Sets
        .newHashSet(
            ItemTypes.DIAMOND,
            ItemTypes.DIAMOND_AXE,
            ItemTypes.DIAMOND,
            ItemTypes.DIAMOND_AXE,
            ItemTypes.DIAMOND_BLOCK,
            ItemTypes.DIAMOND_BOOTS,
            ItemTypes.DIAMOND_CHESTPLATE,
            ItemTypes.DIAMOND_HELMET,
            ItemTypes.DIAMOND_HOE,
            ItemTypes.DIAMOND_HORSE_ARMOR,
            ItemTypes.DIAMOND_LEGGINGS,
            ItemTypes.DIAMOND_ORE,
            ItemTypes.DIAMOND_PICKAXE,
            ItemTypes.DIAMOND_SHOVEL,
            ItemTypes.DIAMOND_SWORD
        )
        .parallelStream()
        .map(ItemType::getName)
        .collect(Collectors.toSet());

    @Setting(comment = "The items that have to be dropped")
    Set<String> blacklist = Sets
        .newHashSet(ItemTypes.COMPASS)
        .parallelStream()
        .map(ItemType::getName)
        .collect(Collectors.toSet());
}
