package net.kineticraft.lostcity.data.lists;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.kineticraft.lostcity.utils.Utils;

/**
 * Store enums in Json
 * Created by Kneesnap on 6/10/2017.
 */
public class EnumList<E extends Enum<E>> extends SaveableList<E> {

    private Class<E> enumClass;

    public EnumList() {

    }

    public EnumList(Class<E> clazz) {
        this.enumClass = clazz;
    }

    @Override
    protected E loadSingle(JsonElement e) {
        return Utils.getEnum(e.getAsString(), this.enumClass);
    }

    @Override
    protected JsonElement save(E val) {
        return new JsonPrimitive(val.name());
    }
}