package net.kineticraft.lostcity.data;

import net.kineticraft.lostcity.data.reflect.JsonSerializer;

/**
 * Represents an object that can be saved or loaded as Json.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public interface Jsonable {

    /**
     * Load this data from a JSON object.
     * @param data
     */
    default void load(JsonData data) {
        JsonSerializer.deserialize(this, data);
    }

    /**
     * Save this to a JSON object.
     * @return json
     */
    default JsonData save() {
        return JsonSerializer.save(this);
    }
}
