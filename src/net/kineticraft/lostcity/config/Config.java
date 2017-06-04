package net.kineticraft.lostcity.config;

import lombok.Getter;
import net.kineticraft.lostcity.config.Configs.ConfigType;

/**
 * A Configuration base.
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter
public abstract class Config {

    private ConfigType type;

    public Config(ConfigType type) {
        this.type = type;
        loadFromDisk();
        saveToDisk(); // Fixes any invalid configurations.
    }

    /**
     * Loads this configuration from disk.
     */
    public abstract void loadFromDisk();

    /**
     * Save this config to disk.
     */
    public abstract void saveToDisk();

    /**
     * Gets the file name.
     * @return Saved file name.
     */
    protected String getFileName() {
        return getType().name().toLowerCase();
    }
}
