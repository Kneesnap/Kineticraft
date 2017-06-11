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
     */
    public static void load(Jsonable j) {

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
     * @param f
     * @return object
     */
    private static Object loadObject(JsonData data, Field f) {
        return null;
    }

    /**
     * Saves the data from a field to a JsonObject.
     * @param data
     * @param field
     */
    private static void saveObject(JsonData data,  Field field) {

    }
}
