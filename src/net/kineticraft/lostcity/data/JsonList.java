package net.kineticraft.lostcity.data;

import com.google.gson.JsonArray;
import lombok.Getter;
import net.kineticraft.lostcity.utils.Utils;

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
    private int maxCount;

    public JsonList() {
        this(Integer.MAX_VALUE);
    }

    public JsonList(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * Add a value to the list.
     * @param val
     */
    public void add(T val) {
        if (maxCount <= size()) // If we're past the max value index, delete the first element.
            getValues().remove(0);
        getValues().add(val);
    }

    /**
     * Return the value at the given index.
     * @param index
     * @return
     */
    public T get(int index) {
        return getValues().get(index);
    }

    /**
     * Is this list empty?
     * @return
     */
    public boolean isEmpty() {
        return size() == 0;
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
    public static <T extends Jsonable> JsonList<T> fromJson(JsonArray array, Class<T> type, int max) {
        JsonList<T> list = new JsonList<>(max);
        array.forEach(je -> Utils.fromJson(type, je.getAsJsonObject()));
        return list;
    }
}
