package net.kineticraft.lostcity.config;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs.ConfigType;
import net.kineticraft.lostcity.utils.GeneralException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * A Configuration base.
 * Created by Kneesnap on 6/3/2017.
 */
@Setter @Getter
public abstract class Config {

    private transient ConfigType type;

    /**
     * Loads this configuration from disk.
     */
    public void loadFromDisk() {
        try {
            // Create a new ArrayList<> because the List that Files.readAllLines returns may not be edittable.
            File file = getFile();
            if (!file.exists())
                file.createNewFile();

            load(new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new GeneralException("Failed to load config " + type + ".", e);
        }
    }

    /**
     * Load from the lines of a file.
     * @param lines
     */
    protected abstract void load(List<String> lines);

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

    /**
     * Get the file that is routed to this config.
     * @return file
     */
    public File getFile() {
        return Core.getFile(getFileName());
    }
}
