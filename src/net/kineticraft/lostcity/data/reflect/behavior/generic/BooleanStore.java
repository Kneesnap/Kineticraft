package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.MethodStore;
import net.kineticraft.lostcity.item.display.GUIItem;

import java.lang.reflect.Field;

/**
 * Store and load boolean values.
 * Created by Kneesnap on 7/25/2017.
 */
public class BooleanStore extends MethodStore<Boolean> {

    public BooleanStore() {
        super(Boolean.TYPE, "Boolean");
    }

    @Override
    public void editItem(GUIItem item, Field f, Jsonable data) {
        item.leftClick(ce -> set(f, data, !((Boolean) get(f, data)))).addLoreAction("Left", "Toggle");
    }
}
