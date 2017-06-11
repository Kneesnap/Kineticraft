package net.kineticraft.lostcity.config;

import lombok.Getter;
import net.kineticraft.lostcity.config.Configs.ConfigType;

/**
 * A Configuration base.
 * TODO: Maybe use annotations?
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter
public abstract class Config {

    private String fileName;

    public Config(String fileName) {
        this.fileName = fileName;
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
}
