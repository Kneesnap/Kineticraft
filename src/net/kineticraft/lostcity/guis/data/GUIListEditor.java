package net.kineticraft.lostcity.guis.data;

import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.guis.GUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Edit a JsonList
 * Created by Kneesnap on 7/25/2017.
 */
public class GUIListEditor<T extends Jsonable> extends GUI {

    private JsonList<T> list;

    public GUIListEditor(Player player, JsonList<T> list) {
        super(player, "List Editor", fitSize(list, 1));
        this.list = list;
    }

    @Override
    public void addItems() {
        for (T o : list) {
            addItem(Material.PAPER, ChatColor.YELLOW + "Element " + list.indexOf(o))
                    .leftClick(ce -> new GUIJsonEditor(ce.getPlayer(), o))
                    .rightClick(ce -> {
                        list.remove(o);
                        reconstruct();
                    })
                    .addLoreAction("Left", "Edit Value").addLoreAction("Right", "Remove");
        }

        addBackButton();
    }
}
