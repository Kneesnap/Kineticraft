package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;

import java.util.function.Function;

/**
 * Store primitives.
 * Created by Kneesnap on 7/3/2017.
 */
public class PrimitiveStore<T> extends MethodStore<T> {

    private Function<T, Number> convert;

    public PrimitiveStore(Class<T> apply, String typeName, Function<T, Number> convert) {
        super(apply, "setNum", "get" + typeName);
        this.convert = convert;
    }

    @Override
    protected Class<Number> getSaveArgument() {
        return Number.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveField(JsonData data, Object value, String key) throws Exception {
        super.saveField(data, convert.apply((T) value), key);
    }
}
