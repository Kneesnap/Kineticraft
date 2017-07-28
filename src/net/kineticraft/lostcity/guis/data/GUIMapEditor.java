package net.kineticraft.lostcity.guis.data;

import net.kineticraft.lostcity.data.maps.SaveableMap;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Allows editting of JsonMaps.
 * Created by Kneesnap on 7/25/2017.
 */
public class GUIMapEditor<K, V> extends GUIJsonEditor {

    private SaveableMap<K, V> map;

    public GUIMapEditor(Player player, SaveableMap<K, V> map) {
        super(player, fitSize(map.keySet(), 1));
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addElements() {
        map.forEach((k, v) -> addItem(k.toString(), v.getClass(), v, val -> map.put(k, (V) val), k));
    }

    @SuppressWarnings("unchecked")
    protected GUIItem addItem(String itemName, Class<?> type, Object value, Consumer<Object> setter, K key) {
        GUIItem gi = addItem(itemName, type, value, setter);
        gi.clear(GUIItem.IClickType.RIGHT).rightClick(ce -> map.remove(key)).addLoreAction("Right", "Remove Value");

        if (key instanceof String) {
            gi.middleClick(ce -> {
                ce.getPlayer().sendMessage(ChatColor.GREEN + "What should the key '" + key.toString() + "' be changed to?");
                Callbacks.listenForChat(ce.getPlayer(), m -> map.put((K) m, map.remove(key)));
            }).addLoreAction("Middle", "Change Key");
        }
        return gi;
    }
}
