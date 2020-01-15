package me.settingdust.deathpenalty;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.util.UUID;
import me.settingdust.deathpenalty.config.model.FakePlayerModule;
import me.settingdust.deathpenalty.FakePlayerModuleService.Impl;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.Text;

@ImplementedBy(Impl.class)
public interface FakePlayerModuleService {
    FakePlayerModuleService sendMessage(MessageReceiver receiver, Text text);

    int getTimeToLive();

    void addUser(UUID uuid);

    void removeUser(UUID uuid);

    boolean hasUser(UUID uuid);

    final class Impl implements FakePlayerModuleService {
        @Inject
        FakePlayerModule fakePlayerModule;

        @Override
        public FakePlayerModuleService sendMessage(MessageReceiver receiver, Text text) {
            if (fakePlayerModule.isSendMessage()) {
                receiver.sendMessage(text);
            }
            return this;
        }

        @Override
        public int getTimeToLive() {
            return fakePlayerModule.getTimeToLive();
        }

        @Override
        public void addUser(UUID uuid) {
            fakePlayerModule.getPlayerData().add(uuid);
        }

        @Override
        public void removeUser(UUID uuid) {
            fakePlayerModule.getPlayerData().remove(uuid);
        }

        @Override
        public boolean hasUser(UUID uuid) {
            return fakePlayerModule.getPlayerData().contains(uuid);
        }
    }
}
