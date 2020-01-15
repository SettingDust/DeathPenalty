package me.settingdust.deathpenalty.config;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import java.nio.file.Paths;
import me.settingdust.deathpenalty.ConfigProvider;

public class MainConfigProvider extends ConfigProvider<MainConfigModel> implements Provider<MainConfigModel> {

    @SuppressWarnings("UnstableApiUsage")
    public MainConfigProvider() {
        super(Paths.get("config.conf"), new MainConfigModel(), TypeToken.of(MainConfigModel.class));
    }
}
