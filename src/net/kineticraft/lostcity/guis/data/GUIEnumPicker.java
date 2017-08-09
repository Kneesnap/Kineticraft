package net.kineticraft.lostcity.guis.data;

import net.kineticraft.lostcity.guis.PagedGUI;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Pick an enum
 * Created by Kneesnap on 7/25/2017.
 */
public class GUIEnumPicker<E extends Enum<E>> extends PagedGUI {

    private E[] values;
    private Consumer<E> onPick;

    public GUIEnumPicker(Player player, E[] values, Consumer<E> onPick) {
        super(player, "Please Select:");
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

        super.addItems();
    }

    @Override
    protected void addCustomOverlay() {
        center(1);
        addItem(Material.PAPER, ChatColor.GREEN + "Search", "", "Click here to enter a value.").leftClick(ce -> {
            ce.getPlayer().sendMessage(ChatColor.GREEN + "Please enter the option you'd like to select.");
            Callbacks.listenForChat(ce.getPlayer(), input -> {
                String name = input.replaceAll(" ", "_");
                E val = Stream.of(values).filter(e -> e.name().equalsIgnoreCase(name)).findAny().orElse(null);
                if (val == null) {
                    ce.getPlayer().sendMessage(ChatColor.RED + "Unknown value.");
                    return;
                }

                onPick.accept(val);
                openPrevious();
            });
        });
    }
}
