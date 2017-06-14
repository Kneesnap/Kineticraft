package net.kineticraft.lostcity.config;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.config.Configs.ConfigType;

/**
 * A Configuration base.
 * TODO: Use annotations
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Setter @Getter
public abstract class Config {

    private ConfigType type;

    /**
     * Loads this configuration from disk.
     */
    public abstract void loadFromDisk();

    /**
     * Save this config to disk.
     */
    public abstract void saveToDisk();

    /**
     * Get the file name for this config.
     * @return fileName
     */
    public String getFileName() {
        return getType().name().toLowerCase();
    }
}
