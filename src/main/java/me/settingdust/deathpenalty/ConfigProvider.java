package me.settingdust.deathpenalty;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.SneakyThrows;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.Sponge;

/**
 * {@link GamePreInitializationEvent}
 */
@SuppressWarnings("UnstableApiUsage")
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class ConfigProvider<T> implements Provider<T> {
    @Inject
    @ConfigDir(sharedRoot = false)
    Path configDir;

    @Inject
    PluginContainer pluginContainer;

    ConfigurationLoader<CommentedConfigurationNode> configLoader;

    Path configPath;

    T defaultValue;

    T value;

    TypeToken<T> typeToken;

    ConfigurationNode rootNode = null;

    public ConfigProvider(Path configPath, T defaultValue, TypeToken<T> typeToken) {
        this.configPath = configPath;
        this.defaultValue = defaultValue;
        this.typeToken = typeToken;
    }

    protected void init() {
        if (configLoader == null) {
            this.configPath = configDir.resolve(configPath);
            this.configLoader = HoconConfigurationLoader.builder().setPath(configPath).build();
            Sponge.getEventManager().registerListeners(pluginContainer, this);
            load();
        }
    }

    @SneakyThrows
    protected void load() {
        this.rootNode = configLoader.load();
        this.rootNode.mergeValuesFrom(configLoader.createEmptyNode().setValue(typeToken, defaultValue));
        this.value = this.rootNode.getValue(typeToken);
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void save() {
        configPath.getParent().toFile().mkdirs();
        configLoader.save(rootNode);
    }

    @Listener
    public void onSaveWorld(SaveWorldEvent event) {
        save();
    }

    @Listener
    public void onGameReload(GameReloadEvent event) {
        load();
    }

    /**
     * {@link GameInitializationEvent }
     *
     * @return data class
     */
    @Override
    public T get() {
        init();
        return value;
    }
}
