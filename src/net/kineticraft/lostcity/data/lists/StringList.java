package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.message.StructuredDataMessage;

/**
 * Store string arrays in Json.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class StringList extends SaveableList<String> {

    public StringList() {

    }

    public StringList(String... strings) {
        for (String s : strings)
            getValues().add(s);
    }

    @Override
    protected String load(JsonElement e) {
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
        return stream().filter(s -> s.equalsIgnoreCase(value)).findAny().isPresent();
    }

    /**
     * Removes the specific string, regardless of case.
     * @param value
     * @return wasRemoved
     */
    public boolean removeIgnoreCase(String value) {
        String remove = stream().filter(s ->  s.equalsIgnoreCase(value)).findAny().orElse(null);
        return remove != null ? remove(remove) : false;
    }
}
