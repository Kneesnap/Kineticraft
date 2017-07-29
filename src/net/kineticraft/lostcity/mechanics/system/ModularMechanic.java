package net.kineticraft.lostcity.mechanics.system;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * A mechanic that loads and saves modules into a directory.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter
public class ModularMechanic<T extends Jsonable> extends Mechanic {

    private String directory;
    private Class<T> moduleClass;
    private JsonMap<T> map = new JsonMap<>();

    public ModularMechanic(String dir, Class<T> clazz) {
        this.directory = dir;
        this.moduleClass = clazz;
    }

    @Override // Loads all files.
    public void onEnable() {
        load();
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), this::save, 0L, 6000L); // Auto-save every 5min.
    }

    @Override // Save all files on disable.
    public void onDisable() {
        save();
    }

    /**
     * Load all module data from disk.
     */
    @SuppressWarnings("ConstantConditions")
    public void load() {
        Core.makeFolder(getDirectory());

        for (File f : Core.getFile(getDirectory()).listFiles()) {
            String fileName = f.getName().substring(f.getName().lastIndexOf(File.separator) + 1).split("\\.")[0];
            getMap().put(fileName, JsonSerializer.fromFile(getModuleClass(), f));
        }
    }

    /**
     * Save all module data to disk.
     */
    public void save() {
        Core.makeFolder(getDirectory());
        getMap().keySet().forEach(f -> new JsonData(getModule(f)).toFile(getDirectory() + "/" + Utils.sanitizeFileName(f)));
    }

    /**
     * Get the module with the given name.
     * @param name
     * @return module
     */
    public T getModule(String name) {
        return getMap().get(name);
    }
}