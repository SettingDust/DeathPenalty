package me.settingdust.deathpenalty.config;

import com.google.inject.ProvidedBy;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.Getter;
import me.settingdust.deathpenalty.config.model.EconomyModel;
import me.settingdust.deathpenalty.config.model.ExpModel;
import me.settingdust.deathpenalty.config.model.FakePlayerModule;
import me.settingdust.deathpenalty.config.model.ItemModel;
import me.settingdust.deathpenalty.config.model.ModulesModel;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.Setting;

@Getter
@Singleton
@ConfigSerializable
@Accessors(fluent = true)
@ProvidedBy(MainConfigProvider.class)
@SuppressWarnings("InvalidProvidedBy")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainConfigModel {
    @Setting
    ModulesModel modules = new ModulesModel();

    @Setting
    ItemModel item = new ItemModel();

    @Setting
    ExpModel exp = new ExpModel();

    @Setting
    EconomyModel economy = new EconomyModel();

    @Setting(comment = "Avoid user from logout to avoid death. Create a fake player at the location.")
    FakePlayerModule fakePlayer = new FakePlayerModule();
}
