package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
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
    public void editItem(GUIItem item, Field f, Jsonable data) throws IllegalAccessException {
        item.leftClick(ce -> {

        }).rightClick(ce -> {

        }).addLore("Value: " + ChatColor.YELLOW + f.get(data), "", "Left-Click: Set Value");
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
