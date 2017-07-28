package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.reflect.behavior.MethodStore;
import net.kineticraft.lostcity.item.display.GUIItem;

import java.util.function.Consumer;

/**
 * Store and load boolean values.
 * Created by Kneesnap on 7/25/2017.
 */
public class BooleanStore extends MethodStore<Boolean> {

    public BooleanStore() {
        super(Boolean.TYPE, "Boolean");
    }

    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        item.leftClick(ce -> setter.accept(!((Boolean) value))).addLoreAction("Left", "Toggle");
    }
}
