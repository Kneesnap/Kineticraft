package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import net.kineticraft.lostcity.data.Jsonable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A base for a saveable list.
 * Created by Kneesnap on 6/10/2017.
 */
@Getter
public abstract class SaveableList<T> implements Iterable<T>, Jsonable {

    private List<T> values = new ArrayList<>();

    public SaveableList() {

    }

    public SaveableList(Iterable<T> values) {
        values.forEach(getValues()::add);
    }

    /**
     * Attempts to replace an old value with a new value.
     * @param oldValue
     * @param newValue
     * @return replacedValue
     */
    public T replace(T oldValue, T newValue) {
        return replace(oldValue, newValue, false);
    }

    /**
     * Replace an old value with a new value.
     * @param oldValue The value to replace.
     * @param newValue - The value to replace it with.
     * @param add - Add if not found
     * @return replacedValue
     */
    public T replace(T oldValue, T newValue, boolean add) {
        int oldIndex = indexOf(oldValue);
        if (oldIndex != -1)
            return set(oldIndex, newValue);
        if (add)
            add(newValue);
        return null;
    }

    /**
     * Set a value at the given index.
     * Index must be in range of list.
     * @param index - Index to set the value at.
     * @param value - Value to set.
     * @return replaced - Old Value.
     */
    public T set(int index, T value) {
        return getValues().set(index, value);
    }

    /**
     * Get the index of a given value.
     * Returns -1 if not found.
     * @param val
     * @return index
     */
    public int indexOf(T val) {
        return getValues().indexOf(val);
    }

    /**
     * Add a value to the list.
     * @param val
     */
    public void add(T val) {
        getValues().add(val);
    }

    /**
     * Add a value, then trim to the max length.
     * @param val
     * @param maxSize
     */
    public void add(T val, int maxSize) {
        add(val);
        trim(maxSize);
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
            remove(0);
    }

    /**
     * Remove a value from the list by its index.
     * Returns the removed value, or null.
     *
     * @param index
     * @return removed
     */
    public T remove(int index) {
        return hasIndex(index) ? getValues().remove(index) : null;
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
     * Does this list contain the given value?
     * @param value
     * @return contains
     */
    public boolean contains(T value) {
        return getValues().contains(value);
    }

    /**
     * Return the last element of this list, if possible.
     * @return lastElement
     */
    public T last() {
        return getValueSafe(size() - 1);
    }

    /**
     * Load this array from json.
     * @param array
     */
    @Override
    public void load(JsonElement array) {
        for (JsonElement je : array.getAsJsonArray())
            if (je != null && !je.isJsonNull())
                getValues().add(loadSingle(je));
    }

    /**
     * Save the values of this into a JsonArray.
     * @return JsonArray
     */
    @Override
    public JsonElement save() {
        JsonArray array = new JsonArray();
        getValues().stream().filter(Objects::nonNull).map(this::save).filter(Objects::nonNull).forEach(array::add);
        return array;
    }

    /**
     * Does this list contain a value for the given index?
     * @param index
     * @return hasIndex
     */
    public boolean hasIndex(int index) {
        return index >= 0 && size() > index;
    }

    /**
     * Returns the value at the given index if we have it, otherwise null.
     *
     * @param index
     * @return value
     */
    public T getValueSafe(int index) {
        return hasIndex(index) ? get(index) : null;
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
    protected abstract T loadSingle(JsonElement e);

    /**
     * Save a value to json
     * @param val
     * @return json
     */
    protected abstract JsonElement save(T val);
}
