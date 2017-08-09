package net.kineticraft.lostcity.guis.data;

import net.kineticraft.lostcity.data.lists.SaveableList;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Edit a JsonList
 * Created by Kneesnap on 7/25/2017.
 */
public class GUIListEditor<T> extends GUIJsonEditor {

    private SaveableList<T> list;

    public GUIListEditor(Player player, SaveableList<T> list) {
        super(player);
        this.list = list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addElements() {
        for (int i = 0; i < list.size(); i++) {
            final int index = i;
            Object o = list.get(i);
            addItem("Element " + i, o.getClass(), o, val -> list.set(index, (T) val));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GUIItem addItem(String itemName, Class<?> type, Object value, Consumer<Object> setter) {
        GUIItem gi = super.addItem(itemName, type, value, setter);
        gi.clear(GUIItem.IClickType.RIGHT).rightClick(ce -> list.remove((T) value)).addLoreAction("Right", "Remove Value");
        return gi;
    }
}
