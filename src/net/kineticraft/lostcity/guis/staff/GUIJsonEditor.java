package net.kineticraft.lostcity.guis.staff;

import lombok.Getter;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Allows viewing or editting of JSON data.
 * Created by Kneesnap on 7/20/2017.
 */
@Getter
public class GUIJsonEditor extends GUI {

    private Jsonable data;

    public GUIJsonEditor(Player player, Jsonable data) {
        super(player, "JSON Editor", fitSize(JsonSerializer.getFields(data).size() + 1));
        this.data = data;
    }

    @Override
    public void addItems() {
        JsonSerializer.getFields(getData()).forEach(f -> {
            GUIItem gi = addItem(Material.WOOL, ChatColor.YELLOW + ucFirst(f.getName()));

            try {
                JsonSerializer.getHandler(f).editItem(gi, f, getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!gi.getListeners().containsKey(GUIItem.IClickType.RIGHT)) {
                gi.rightClick(ce -> {
                    try {
                        if (!f.getType().isPrimitive())
                            f.set(getData(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reconstruct();
                }).addLore("Right-Click: Remove Value");
            }

            try {
                Object o = f.get(getData());
                gi.setColor(o != null ? DyeColor.LIME : DyeColor.RED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        addBackButton();
    }

    private static String ucFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
