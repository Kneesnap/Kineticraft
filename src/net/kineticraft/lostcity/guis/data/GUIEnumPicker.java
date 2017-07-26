package net.kineticraft.lostcity.guis.data;

import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Pick an enum
 * Created by Kneesnap on 7/25/2017.
 */
public class GUIEnumPicker<E extends Enum<E>> extends GUI {

    private E[] values;
    private Consumer<E> onPick;

    public GUIEnumPicker(Player player, E[] values, Consumer<E> onPick) {
        super(player, "Please Select:", fitSize(values, 1));
        this.values = values;
        this.onPick = onPick;
    }

    @Override
    public void addItems() {
        for (E value : values) {
            String name = Utils.capitalize(value.name());
            addItem(Material.LAPIS_BLOCK, ChatColor.YELLOW + name, "Click here to select " + name + ".")
                    .anyClick(c -> {
                        onPick.accept(value);
                        openPrevious();
                    });
        }

        addBackButton();
    }
}
