package net.kineticraft.lostcity.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonList - A list of Jsonable values.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public class JsonList<T extends Jsonable> {

    private List<T> values = new ArrayList<>();

    /**
     * Add a value to the list.
     * @param val
     */
    public void add(T val) {
        getValues().add(val);
    }

    /**
     * Remove a value from the list.
     * Returns whether or not the element was removed successfully.
     *
     * @param val
     */
    public boolean remove(T val) {
        return getValues().remove(val);
    }

    /**
     * Returns the size of the list.
     */
    public int size() {
        return getValues().size();
    }

    /**
     * Save the values of this into a JsonArray.
     * @return
     */
    public JsonArray toJson() {
        JsonArray array = new JsonArray();
        getValues().stream().map(Jsonable::save).map(JsonData::getJsonObject).forEach(array::add);
        return array;
    }

    /**
     * Convert a Json array into a JsonList
     * @param array
     * @param type
     * @return
     */
    public static <T extends Jsonable> JsonList<T> fromJson(JsonArray array, Class<T> type) {
        JsonList<T> list = new JsonList<>();
        array.forEach(je -> JsonUtil.fromJson(type, je.getAsJsonObject()));
        return list;
    }
}
