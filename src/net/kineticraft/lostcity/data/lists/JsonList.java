package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonElement;
import lombok.Getter;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.utils.Utils;
/**
 * JsonList - A list of Jsonable values.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public class JsonList<T extends Jsonable> extends SaveableList<T> {

    private Class<T> jsonClass;

    public JsonList() {

    }

    public JsonList(Class<T> clazz) {
        this.jsonClass = clazz;
    }

    @Override
    protected T load(JsonElement e) {
        return Utils.fromJson(this.jsonClass, e.getAsJsonObject());
    }

    @Override
    protected JsonElement save(T val) {
        return val.save().getJsonObject();
    }
}