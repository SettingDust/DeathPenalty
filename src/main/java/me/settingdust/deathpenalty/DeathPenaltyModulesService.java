package me.settingdust.deathpenalty;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import me.settingdust.deathpenalty.config.model.ModulesModel;
import me.settingdust.deathpenalty.handler.DropExpHandler;
import me.settingdust.deathpenalty.handler.DropItemHandler;
import me.settingdust.deathpenalty.handler.FakePlayerHandler;
import me.settingdust.deathpenalty.handler.LoseCurrencyHandler;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

public class DeathPenaltyModulesService {
    @Inject
    ModulesModel modulesModel;

    PluginContainer pluginContainer;

    EventManager eventManager;

    @Inject
    ServiceManager serviceManager;

    @Getter
    @Inject
    ItemModuleService itemModuleService;

    @Getter
    @Inject
    ExpModuleService expModuleService;

    @Getter
    @Inject
    EconomyModuleService economyModuleService;

    @Getter
    @Inject
    FakePlayerModuleService fakePlayerModuleService;

    @Inject
    Injector injector;

    @Inject
    public DeathPenaltyModulesService(EventManager eventManager, PluginContainer pluginContainer) {
        eventManager.registerListeners(pluginContainer, this);
        this.pluginContainer = pluginContainer;
        this.eventManager = eventManager;
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        injector.getInstance(DropItemHandler.class);

        if (modulesModel.isExp()) {
            injector.getInstance(DropExpHandler.class);
        }

        if (modulesModel.isEconomy()) {
            injector.getInstance(LoseCurrencyHandler.class);
        }

        if (modulesModel.isFakePlayer()) {
            injector.getInstance(FakePlayerHandler.class);
        }
    }

    @Listener
    public void onGamePostInitialization(GamePostInitializationEvent event) {
        if (modulesModel.isItem()) {
            serviceManager.setProvider(pluginContainer, ItemModuleService.class, itemModuleService);
        }

        if (modulesModel.isExp()) {
            serviceManager.setProvider(pluginContainer, ExpModuleService.class, expModuleService);
        }

        if (modulesModel.isEconomy()) {
            serviceManager.setProvider(pluginContainer, EconomyModuleService.class, economyModuleService);
        }

        if (modulesModel.isFakePlayer()) {
            serviceManager.setProvider(pluginContainer, FakePlayerModuleService.class, fakePlayerModuleService);
        }
    }
}
