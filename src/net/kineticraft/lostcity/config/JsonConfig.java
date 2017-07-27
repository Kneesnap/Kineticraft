package net.kineticraft.lostcity.config;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;

import java.util.List;

/**
 * A Json Configuration base.
 * Created by Kneesnap on 6/3/2017.
 */
public abstract class JsonConfig extends Config implements Jsonable {

    /**
     * Loads Json data from disk.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void loadFromDisk() {
        load(JsonData.fromFile(getFileName()).getJsonObject());
    }

    /**
     * Save this Json data to disk.
     */
    @Override
    public void saveToDisk() {
        new JsonData(this).toFile(getFileName());
    }

    @Override
    protected void load(List<String> lines) {
        // Does not call since we override loadFromDisk.
    }
}
