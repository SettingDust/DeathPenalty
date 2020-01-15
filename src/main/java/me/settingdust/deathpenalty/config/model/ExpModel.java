package me.settingdust.deathpenalty.config.model;

import com.google.inject.ProvidedBy;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import me.settingdust.deathpenalty.config.provider.ExpModelProvider;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.Setting;

@Data
@ConfigSerializable
@ProvidedBy(ExpModelProvider.class)
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpModel {
    @Setting
    boolean sendMessage = true;

    @Setting(comment = "The percentage of exp to drop (< 1)")
    double ratio = 0.2;

    @Setting(comment = "Exp to drop minimum")
    int minDrop = 64;

    @Setting(comment = "Exp to drop maximum")
    int maxDrop = 0;

    @Setting(comment = "The percentage of dropped exp to be lost (can't be got back)")
    double loseRatio = 0.4;
}
