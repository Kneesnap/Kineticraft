package net.kineticraft.lostcity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.config.configs.MainConfig;
import net.kineticraft.lostcity.config.configs.PunishmentConfig;
import net.kineticraft.lostcity.config.configs.VoteConfig;
import net.kineticraft.lostcity.data.JsonMap;
import net.kineticraft.lostcity.mechanics.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.Bukkit;

/**
 * Configuration manager.
 *
 * TODO: When http://openjdk.java.net/jeps/301 is implemented, remove the special case getVoteData() and such in favor of having getConfig() automaticaly casting it using generics.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class Configs extends Mechanic {

    private static JsonMap<Config> configs = new JsonMap<>();

    /**
     * Gets the given config data.
     * @param type
     * @return
     */
    public static Config getConfig(ConfigType type) {
        return configs.get(type.name());
    }

    /**
     * Get vote data.
     * @return
     */
    public static VoteConfig getVoteData() {
        return (VoteConfig) getConfig(ConfigType.VOTES);
    }

    /**
     * Get the main config.
     * @return
     */
    public static MainConfig getMainConfig() {
        return (MainConfig) getConfig(ConfigType.MAIN);
    }

    /**
     * Gets the punishment data.
     * @return
     */
    public static PunishmentConfig getPunishmentData() {
        return (PunishmentConfig) getConfig(ConfigType.BANS);
    }

    @Override
    public void onEnable() {
        // Loads configs on startup.
        for (ConfigType type : ConfigType.values())
            configs.put(type.name(), type.createConfig());
    }

    @AllArgsConstructor @Getter
    public enum ConfigType {

        MAIN(MainConfig.class),
        VOTES(VoteConfig.class),
        BANS(PunishmentConfig.class);

        private final Class<? extends Config> configClass;

        ConfigType() {
            this(RawConfig.class);
        }

        /**
         * Construct this config.
         * @return
         */
        public Config createConfig() {
            Object[] args = getConfigClass().equals(RawConfig.class) ? new Object[] {this} : null;
            return ReflectionUtil.construct(getConfigClass(), args);
        }
    }
}
