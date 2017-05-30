package net.kineticraft.lostcity.data;

import com.google.gson.JsonObject;

/**
 * Created by Drew on 5/29/2017.
 */
public interface Jsonable {

    /**
     * Load data from a JSON object.
     * @param data
     */
    void load(JsonData data);

    /**
     * Save to a JSON object.
     */
    JsonData save();
}
