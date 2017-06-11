package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A base for a saveable list.
 *
 * Created by Kneesnap on 6/10/2017.
 */
@Getter
public abstract class SaveableList<T> implements Iterable<T> {

    private List<T> values = new ArrayList<>();

    public SaveableList() {

    }

    /**
     * Add a value to the list.
     * @param val
     */
    public void add(T val) {
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
     * @return empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Remove the earliest entries, up to a given index.
     * @param index
     */
    public void trim(int index) {
        while (size() > index)
            getValues().remove(0);
    }

    /**
     * Remove a value from the list.
     * Returns whether or not the element was removed successfully.
     *
     * @param val
     * @return wasRemoved
     */
    public boolean remove(T val) {
        return getValues().remove(val);
    }

    /**
     * Returns the size of the list.
     * @return size
     */
    public int size() {
        return getValues().size();
    }

    /**
     * Load this array from json.
     * @param array
     */
    public void load(JsonArray array) {
        array.forEach(this::load);
    }

    /**
     * Save the values of this into a JsonArray.
     * @return JsonArray
     */
    public JsonArray save() {
        JsonArray array = new JsonArray();
        getValues().stream().map(this::save).forEach(array::add);
        return array;
    }

    /**
     * Clear the values.
     */
    public void clear() {
        getValues().clear();
    }

    /**
     * Get a java stream of the values.
     * @return stream
     */
    public Stream<T> stream() {
        return getValues().stream();
    }

    @Override
    public Iterator<T> iterator() {
        return getValues().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getValues().forEach(action::accept);
    }

    /**
     * Load a value from json
     * @param e
     * @return value
     */
    protected abstract T load(JsonElement e);

    /**
     * Save a value to json
     * @param val
     * @return json
     */
    protected abstract JsonElement save(T val);
}
