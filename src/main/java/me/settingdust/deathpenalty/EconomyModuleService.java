package me.settingdust.deathpenalty;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.math.BigDecimal;
import lombok.var;
import me.settingdust.deathpenalty.config.model.EconomyModel;
import me.settingdust.deathpenalty.EconomyModuleService.Impl;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.Text;

@ImplementedBy(Impl.class)
public interface EconomyModuleService {
    EconomyModuleService sendMessage(MessageReceiver receiver, Text text);

    BigDecimal getCurrencyToDrop(BigDecimal currency);

    final class Impl implements EconomyModuleService {
        @Inject
        EconomyModel economyModule;

        @Override
        public EconomyModuleService sendMessage(MessageReceiver receiver, Text text) {
            if (economyModule.isSendMessage()) {
                receiver.sendMessage(text);
            }
            return this;
        }

        @SuppressWarnings("DuplicatedCode")
        @Override
        public BigDecimal getCurrencyToDrop(BigDecimal currency) {
            var finalCurrency = currency.multiply(BigDecimal.valueOf(economyModule.getRatio()));
            if (economyModule.getMinDrop() > 0) {
                finalCurrency = finalCurrency.max(BigDecimal.valueOf(economyModule.getMinDrop()));
            }
            if (economyModule.getMaxDrop() > 0) {
                finalCurrency = finalCurrency.min(BigDecimal.valueOf(economyModule.getMaxDrop()));
            }
            return finalCurrency;
        }
    }
}
