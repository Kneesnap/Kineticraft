package net.kineticraft.lostcity.config;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config that has no file structure, just raw text.
 * Created by Kneesnap on 6/3/2017.
 */
@Getter
public class RawConfig extends Config {

    private List<String> lines = new ArrayList<>();

    @Override
    protected void load(List<String> lines) {
        this.lines = lines;
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

    @Override
    public String getFileName() {
        return "messages/" + super.getFileName() + ".txt";
    }
}
