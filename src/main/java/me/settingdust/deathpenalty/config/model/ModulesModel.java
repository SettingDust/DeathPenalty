package me.settingdust.deathpenalty.config.model;

import com.google.inject.ProvidedBy;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.settingdust.deathpenalty.config.provider.ModulesModelProvider;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.Setting;

@Data
@Singleton
@ConfigSerializable
@Accessors(chain = true)
@ProvidedBy(ModulesModelProvider.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModulesModel {
    @Setting
    boolean item = true;

    @Setting
    boolean exp = true;

    @Setting
    boolean economy = true;

    @Setting
    boolean fakePlayer = true;
}
