package me.settingdust.deathpenalty.config.model;

import com.google.common.collect.Sets;
import com.google.inject.ProvidedBy;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.settingdust.deathpenalty.config.provider.FakePlayerModuleProvider;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.Setting;

@Data
@ConfigSerializable
@ProvidedBy(FakePlayerModuleProvider.class)
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakePlayerModule {
    @Setting
    boolean sendMessage = true;

    @Setting(comment = "The ticks of the fake player to keep")
    int timeToLive = 100;

    @Setting
    Set<UUID> playerData = Sets.newHashSet();
}
