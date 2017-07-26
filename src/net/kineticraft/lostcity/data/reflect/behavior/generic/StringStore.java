package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.MethodStore;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;

/**
 * Save and load strings.
 * Created by Kneesnap on 7/25/2017.
 */
public class StringStore extends MethodStore<String> {

    public StringStore() {
        super(String.class, "String");
    }

    @Override
    public void editItem(GUIItem item, Field f, Jsonable data) {
        item.leftClick(ce -> {
            ce.getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new value.");
            Callbacks.listenForChat(ce.getPlayer(), m -> {
                set(f, data, m);
                ce.getPlayer().sendMessage(ChatColor.GREEN + "Value updated.");
            }, null);
        }).addLoreAction("Left", "Set Value");
        setNull(f, data, item);
    }
}
