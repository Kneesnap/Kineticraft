package net.kineticraft.lostcity.data;

/**
 * Represents an object that can be saved or loaded as Json.
 *
 * Created by Kneesnap on 5/29/2017.
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
