package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Store string arrays in Json.
 * Created by Kneesnap on 6/10/2017.
 */
public class StringList extends SaveableList<String> {

    public StringList() {

    }

    public StringList(Iterable<String> val) {
        super(val);
    }

    @Override
    protected String loadSingle(JsonElement e) {
        return e.getAsString();
    }

    @Override
    protected JsonElement save(String val) {
        return new JsonPrimitive(val);
    }

    /**
     * Does this contain the specific string, regardless of case.
     * @param value
     * @return hasValue
     */
    public boolean containsIgnoreCase(String value) {
        return stream().anyMatch(s -> s.equalsIgnoreCase(value));
    }

    /**
     * Removes the specific string, regardless of case.
     * @param value
     * @return wasRemoved
     */
    public boolean removeIgnoreCase(String value) {
        String remove = stream().filter(s ->  s.equalsIgnoreCase(value)).findAny().orElse(null);
        return remove != null && remove(remove);
    }

    /**
     * Join this StringList together into a string.
     * @param delimeter
     * @return string
     */
    public String join(String delimeter) {
        return String.join(delimeter, getValues());
    }
}