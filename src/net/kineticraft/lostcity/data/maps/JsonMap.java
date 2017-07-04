package net.kineticraft.lostcity.data.maps;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.utils.Utils;

/**
 * JsonMap - Used for storing objects by a key value.
 *
 * Created by Kneesnap on 6/1/2017.
 */
@Getter
public class JsonMap<T extends Jsonable> extends SaveableMap<String, T> {

    private transient Class<T> classType;

    public JsonMap() { // When creating new data.

    }

    public JsonMap(JsonData obj) {
        this(obj, null); // Null is ok only when we aren't loading data.
    }

    public JsonMap(JsonData data, Class<T> cls) {
        super(data);
        this.classType = cls;
        load(data);
    }

    @Override
    protected void save(JsonData data, String key, T value) {
        data.setElement(key, value.save());
    }

    @Override
    protected void load(JsonData data, String key) {
        if (getClassType() != null)
            getMap().put(key, Utils.fromJson(getClassType(), data.getObject(key)));
    }
}
