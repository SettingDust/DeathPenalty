package me.settingdust.deathpenalty;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import lombok.var;
import me.settingdust.deathpenalty.config.model.ExpModel;
import me.settingdust.deathpenalty.ExpModuleService.Impl;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.Text;

@ImplementedBy(Impl.class)
public interface ExpModuleService {
    ExpModuleService sendMessage(MessageReceiver receiver, Text text);

    int getExpToDrop(int totalExp);

    double getLoseRatio();

    final class Impl implements ExpModuleService {
        @Inject
        ExpModel expModel;

        @Override
        public ExpModuleService sendMessage(MessageReceiver receiver, Text text) {
            if (expModel.isSendMessage()) {
                receiver.sendMessage(text);
            }
            return this;
        }

        @SuppressWarnings("DuplicatedCode")
        @Override
        public int getExpToDrop(int totalExp) {
            var exp = expModel.getRatio() * totalExp;
            if (expModel.getMinDrop() > 0) {
                exp = Math.max(expModel.getMinDrop(), exp);
            }
            if (expModel.getMaxDrop() > 0) {
                exp = Math.min(expModel.getMaxDrop(), exp);
            }
            return (int) exp;
        }

        @Override
        public double getLoseRatio() {
            return expModel.getLoseRatio();
        }
    }
}
