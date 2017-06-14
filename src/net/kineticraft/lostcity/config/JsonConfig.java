package net.kineticraft.lostcity.config;

import net.kineticraft.lostcity.config.Configs.ConfigType;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;

/**
 * A Json Configuration base.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public abstract class JsonConfig extends Config implements Jsonable {

    /**
     * Loads Json data from disk.
     */
    @Override
    public void loadFromDisk() {
        load(JsonData.fromFile(getFileName()));
    }

    /**
     * Save this Json data to disk.
     */
    @Override
    public void saveToDisk() {
        save().toFile(getFileName());
    }
}
