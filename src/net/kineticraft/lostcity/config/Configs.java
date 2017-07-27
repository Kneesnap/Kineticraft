package net.kineticraft.lostcity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.config.configs.*;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration manager.
 * TODO: When http://openjdk.java.net/jeps/301 is implemented, remove the special case getVoteData() and such in favor of having getConfig() automaticaly casting it using generics.
 * Created by Kneesnap on 6/3/2017.
 */
public class Configs extends Mechanic {

    private static Map<ConfigType, Config> configs = new HashMap<>();

    /**
     * Gets the given config data.
     * @param type
     * @return config
     */
    public static Config getConfig(ConfigType type) {
        return configs.get(type);
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
    public void onEnable() {
        // Loads configs on startup.
        for (ConfigType type : ConfigType.values())
            configs.put(type, type.createConfig());
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
        VOTE;

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
