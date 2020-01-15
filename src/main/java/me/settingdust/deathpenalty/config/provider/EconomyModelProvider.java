package me.settingdust.deathpenalty.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.settingdust.deathpenalty.config.MainConfigModel;
import me.settingdust.deathpenalty.config.model.EconomyModel;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EconomyModelProvider implements Provider<EconomyModel> {
    @Inject
    MainConfigModel configModel;

    @Override
    public EconomyModel get() {
        return configModel.economy();
    }
}
