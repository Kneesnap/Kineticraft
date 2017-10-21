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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Allows viewing or editting of JSON data.
 * Created by Kneesnap on 7/20/2017.
 */
@Getter
public class GUIJsonEditor extends GUI {

    private Jsonable data;
    private BiConsumer<Player, Jsonable> onFinish;

    protected GUIJsonEditor(Player player) {
        super(player, "JSON Editor");
    }

    public GUIJsonEditor(Player player, Jsonable data) {
        this(player, data, null);
    }

    public GUIJsonEditor(Player player, Jsonable data, BiConsumer<Player, Jsonable> onFinish) {
        this(player);
        this.data = data;
        this.onFinish = onFinish;
    }

    @Override
    public void addItems() {
        addElements();
        addBackButton();
    }

    /**
     * Add all edittable elements to the gui.
     */
    protected void addElements() {
        JsonSerializer.getFields(getData()).forEach(f -> {
            Object o = null;
            try {
                o = f.get(getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Consumer<Object> setter = val -> {
                try {
                    f.set(getData(), val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            addItem(ucFirst(f.getName()), f.getType(), o, setter);
        });
    }

    @SuppressWarnings("unchecked")
    protected GUIItem addItem(String itemName, Class<?> type, Object value, Consumer<Object> setter) {
        GUIItem gi = addItem(Material.WOOL, ChatColor.YELLOW + itemName);

        // Display the current value.
        String str = value != null ? value.toString() : "null";
        gi.addLore("Value: " + ChatColor.YELLOW + (str.length() > 35 ? str.substring(0, 35) + "..." : str), "");

        // Apply the handler specific code.
        JsonSerializer.getHandler(type).editItem(gi, value, setter, type);

        // If it's using the default icon, set the color based on the data state.
        if (gi.getItem().getType() == Material.WOOL) {
            boolean green = value != null && (!(value instanceof Boolean) || ((Boolean) value));
            gi.setColor(green ? DyeColor.LIME : DyeColor.RED);
        }

        // Update the gui if the value changes.
        gi.anyClick(ce -> {
            if (ce.getGUI() == this)
                reconstruct();
        });

        return gi;
    }

    @Override
    public void onClose() {
        if (this.onFinish != null && getData() != null)
            this.onFinish.accept(getPlayer(), getData());
    }

    private static String ucFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
