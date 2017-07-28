package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.reflect.behavior.MethodStore;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

/**
 * Save and load strings.
 * Created by Kneesnap on 7/25/2017.
 */
public class StringStore extends MethodStore<String> {

    public StringStore() {
        super(String.class, "String");
    }

    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        item.leftClick(ce -> {
            ce.getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new value.");
            Callbacks.listenForChat(ce.getPlayer(), m -> {
                setter.accept(ChatColor.translateAlternateColorCodes('&', m));
                ce.getPlayer().sendMessage(ChatColor.GREEN + "Value updated.");
            }, null);
        }).addLoreAction("Left", "Set Value");
        setNull(item, value, setter);
    }
}
