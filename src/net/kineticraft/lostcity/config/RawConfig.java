package net.kineticraft.lostcity.config;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config that has no file structure, just raw text.
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter
public class RawConfig extends Config {

    private List<String> lines = new ArrayList<>();

    public RawConfig(String name) {
        super(name + ".txt");
    }

    @Override
    public void loadFromDisk() {
        try {
            // Create a new ArrayList<> because the List that Files.readAllLines returns may not be edittable.
            File file = getFile();
            if (!file.exists())
                file.createNewFile();

            this.lines = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to load " + getFileName());
        }
    }

    @Override
    public void saveToDisk() {
        try {
            Files.write(getFile().toPath(), getLines(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to save " + getFileName());
        }
    }

    private File getFile() {
        return Core.getFile(getFileName());
    }
}
