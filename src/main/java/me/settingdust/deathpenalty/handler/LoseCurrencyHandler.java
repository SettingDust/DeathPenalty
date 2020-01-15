package me.settingdust.deathpenalty.handler;

import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.AmberLocale;
import lombok.SneakyThrows;
import lombok.val;
import me.settingdust.deathpenalty.Constants;
import me.settingdust.deathpenalty.EconomyModuleService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent.Death;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.filter.cause.After;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.Sponge;

public class LoseCurrencyHandler {
    @Inject
    ServiceManager serviceManager;

    @Inject
    AmberLocale locale;

    @Inject
    public LoseCurrencyHandler(EventManager eventManager, PluginContainer pluginContainer) {
        eventManager.registerListeners(pluginContainer, this);
    }

    @Listener
    @SneakyThrows
    public void onDestructEntityDeath(Death event, @After(DamageSource.class) Player player) {
        if (player.hasPermission(Constants.ID + ".disable.economy")) {
            return;
        }

        serviceManager
            .provide(EconomyService.class)
            .ifPresent(
                economyService -> serviceManager
                    .provide(EconomyModuleService.class)
                    .ifPresent(
                        economyModuleService -> economyService
                            .getOrCreateAccount(player.getUniqueId())
                            .ifPresent(
                                uniqueAccount -> {
                                    val defaultCurrency = economyService.getDefaultCurrency();
                                    val currency = economyModuleService.getCurrencyToDrop(
                                        uniqueAccount.getBalance(defaultCurrency)
                                    );
                                    uniqueAccount.withdraw(
                                        defaultCurrency,
                                        currency,
                                        Sponge.getCauseStackManager().getCurrentCause()
                                    );
                                    locale
                                        .get(
                                            "message.economy",
                                            currency.intValue() + defaultCurrency.getDisplayName().toPlain()
                                        )
                                        .ifPresent(text -> economyModuleService.sendMessage(player, text));
                                }
                            )
                    )
            );
    }
}
