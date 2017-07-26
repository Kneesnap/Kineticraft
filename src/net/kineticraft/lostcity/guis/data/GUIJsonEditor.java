package net.kineticraft.lostcity.guis.data;

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
        super(player, "JSON Editor", fitSize(JsonSerializer.getFields(data), 1));
        this.data = data;
    }

    @Override
    public void addItems() {
        JsonSerializer.getFields(getData()).forEach(f -> {
            GUIItem gi = addItem(Material.WOOL, ChatColor.YELLOW + ucFirst(f.getName()));

            // Get the current value.
            Object o = null;
            try {
                o = f.get(getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Display the current value.
            if (o == null || o.toString().length() < 50)
                gi.addLore("Value: " + ChatColor.YELLOW + o, "");

            // Apply the handler specific code.
            JsonSerializer.getHandler(f).editItem(gi, f, getData());

            // If it's using the default icon, set the color based on the data state.
            if (gi.getItem().getType() == Material.WOOL) {
                boolean green = o != null && (!(o instanceof Boolean) || ((Boolean) o));
                gi.setColor(green ? DyeColor.LIME : DyeColor.RED);
            }

            // Update the gui if the value changes.
            gi.anyClick(ce -> {
                if (ce.getGUI() == this)
                    reconstruct();
            });
        });

        addBackButton();
    }

    private static String ucFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
