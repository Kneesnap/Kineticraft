package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonElement;
import lombok.Getter;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;

/**
 * JsonList - A list of Jsonable values.
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public class JsonList<T> extends SaveableList<T> {

    private Class<T> jsonClass;

    public JsonList() {

    }

    public JsonList(Iterable<T> values) {
        super(values);
    }

    public JsonList(Class<T> clazz) {
        this.jsonClass = clazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T loadSingle(JsonElement e) {
        return JsonSerializer.fromJson(getJsonClass(), e);
    }

    @Override
    protected JsonElement save(T val) {
        return JsonSerializer.addClass(val, getJsonClass(), JsonSerializer.save(val));
    }
}