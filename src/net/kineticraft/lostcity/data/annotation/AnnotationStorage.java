package net.kineticraft.lostcity.data.annotation;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;

import java.lang.reflect.Field;

/**
 * Our future way of storing data as json.
 * Do not use for the time being.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class AnnotationStorage {

    /**
     * Completely load a Jsonable object
     * @param j
     * @param data
     */
    public static void load(Jsonable j, JsonData data) {

    }

    /**
     * Completely save a Jsonable object.
     * @param jsonable
     * @return jsonData
     */
    public static JsonData save(Jsonable jsonable) {
        return null;
    }

    /**
     * Loads the data from json for the given field
     * @param data
     * @param field
     * @return object
     */
    private static Object loadField(JsonData data, Field field) {
        return null;
    }

    /**
     * Saves the data from a field to a JsonObject.
     * @param data
     * @param field
     */
    private static void saveField(JsonData data,  Field field) {

    }
}
