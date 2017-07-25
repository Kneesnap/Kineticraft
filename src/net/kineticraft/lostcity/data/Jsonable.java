package net.kineticraft.lostcity.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;

/**
 * Represents an object that can be saved or loaded as Json.
 * Created by Kneesnap on 5/29/2017.
 */
public interface Jsonable {

    /**
     * Load this data from a JSON object.
     * @param data
     */
    default void load(JsonElement data) {
        JsonSerializer.deserialize(this, new JsonData(data.getAsJsonObject()));
    }

    /**
     * Save this to a JSON object.
     * @return json
     */
    default JsonElement save() {
        return JsonSerializer.save(this).getAsJsonObject();
    }

    /**
     * Return the json data for this object.
     * @return jsonData.
     */
    default String toJsonString() {
        return save().toString();
    }
}
