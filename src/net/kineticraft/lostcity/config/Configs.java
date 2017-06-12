package net.kineticraft.lostcity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.config.configs.MainConfig;
import net.kineticraft.lostcity.config.configs.PunishmentConfig;
import net.kineticraft.lostcity.config.configs.VoteConfig;
import net.kineticraft.lostcity.mechanics.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration manager.
 *
 * TODO: When http://openjdk.java.net/jeps/301 is implemented, remove the special case getVoteData() and such in favor of having getConfig() automaticaly casting it using generics.
 *
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
     * Gets the punishment data.
     * @return punishmentData
     */
    public static PunishmentConfig getPunishmentData() {
        return (PunishmentConfig) getConfig(ConfigType.BANS);
    }

    /**
     * Get a raw text config of the specified type.
     * @param type
     * @return rawConfig
     */
    public static RawConfig getRawConfig(ConfigType type) {
        return (RawConfig) getConfig(type);
    }

    @Override
    public void onEnable() {
        // Loads configs on startup.
        for (ConfigType type : ConfigType.values())
            configs.put(type, type.createConfig());
    }

    @Getter
    public enum ConfigType {

        MAIN(MainConfig.class),
        VOTES(VoteConfig.class),
        BANS(PunishmentConfig.class),
        RULES(RawConfig.class, "rules"),
        DONATE(RawConfig.class, "donate"),
        ANNOUNCER(RawConfig.class, "announcer"),
        DISCORD(RawConfig.class, "discord"),
        RANKS(RawConfig.class, "ranks"),
        INFO(RawConfig.class, "info");

        private final Class<? extends Config> configClass;
        private final Object[] args;

        ConfigType(Class<? extends Config> clazz, Object... args) {
            this.configClass = clazz;
            this.args = args;
        }

        /**
         * Construct this config.
         * @return config
         */
        public Config createConfig() {
            return ReflectionUtil.construct(getConfigClass(), getArgs());
        }
    }
}
