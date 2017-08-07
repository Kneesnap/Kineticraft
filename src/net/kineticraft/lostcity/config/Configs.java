package net.kineticraft.lostcity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.config.configs.*;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration manager.
 * TODO: When http://openjdk.java.net/jeps/301 is implemented, remove the special case getVoteData() and such in favor of having getConfig() automaticaly casting it using generics.
 * Created by Kneesnap on 6/3/2017.
 */
public class Configs extends Mechanic {

    private static Map<ConfigType, Config> configs = new HashMap<>();

    public Configs() {
        // This isn't in onEnable because it has to run before all other mechanics get registered.
        for (ConfigType type : ConfigType.values())
            configs.put(type, type.createConfig());
    }

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandConfig());
    }

    /**
     * Gets the given config data.
     * @param type
     * @return config
     */
    public static Config getConfig(ConfigType type) {
        return configs.get(type);
    }

    /**
     * Gets the given config data.
     * @param config
     * @return config
     */
    public static Config getConfig(String config) {
        return getConfig(ConfigType.valueOf(config.toUpperCase()));
    }

    /**
     * Get vote data.
     * @return voteConfig
     */
    public static VoteConfig getVoteData() {
        return (VoteConfig) getConfig(ConfigType.VOTES);
    }

    /**
     * Get the main config.
     * @return mainConfig
     */
    public static MainConfig getMainConfig() {
        return (MainConfig) getConfig(ConfigType.MAIN);
    }

    /**
     * Get a raw text config of the specified type.
     * @param type
     * @return rawConfig
     */
    public static RawConfig getRawConfig(ConfigType type) {
        return (RawConfig) getConfig(type);
    }

    /**
     * Get a text message config.
     * @param type
     * @return textConfig
     */
    public static TextConfig getTextConfig(ConfigType type) {
        return (TextConfig) getConfig(type);
    }

    @Override
    public void onDisable() {
        for (ConfigType t : ConfigType.values())
            getConfig(t).saveToDisk();
    }

    @AllArgsConstructor @Getter
    public enum ConfigType {

        MAIN(MainConfig.class),
        VOTES(VoteConfig.class),
        RULES,
        DONATE,
        ANNOUNCER,
        DISCORD,
        RANKS,
        HELP,
        INFO,
        COLORS,
        DUNGEON,
        VOTE,
        IDIOT;

        private final Class<? extends Config> configClass;

        ConfigType() {
            this(TextConfig.class);
        }

        /**
         * Create and load this config.
         * @return config
         */
        public Config createConfig() {
            Config c = ReflectionUtil.construct(getConfigClass());
            c.setType(this); // Sets the type of the config.
            c.loadFromDisk(); // Loads this config from disk.
            c.saveToDisk(); // Saving to disk fixes any formatting issues / makes sure everything loaded properly.
            return c;
        }
    }
}
