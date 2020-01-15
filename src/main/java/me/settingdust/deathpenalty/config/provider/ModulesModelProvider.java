package me.settingdust.deathpenalty.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.settingdust.deathpenalty.config.MainConfigModel;
import me.settingdust.deathpenalty.config.model.ModulesModel;
import me.settingdust.deathpenalty.ExpModuleService;
import me.settingdust.deathpenalty.ItemModuleService;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModulesModelProvider implements Provider<ModulesModel> {
    @Inject
    MainConfigModel configModel;

    @Override
    public ModulesModel get() {
        return configModel.modules();
    }
}
