package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonElement;

/**
 * Represents a queue.
 * Cannot be saved or loaded.
 * Created by Kneesnap on 6/28/2017.
 */
public class QueueList<T> extends SaveableList<T> {

    /**
     * Retrieve / remove the first element.
     * @return val
     */
    public T pop() {
        return remove(0);
    }

    /**
     * Retrieve the first element.
     * @return val
     */
    public T peek() {
        return getValueSafe(0);
    }


    @Override
    protected T loadSingle(JsonElement e) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JsonElement save(T val) {
        throw new UnsupportedOperationException();
    }
}
