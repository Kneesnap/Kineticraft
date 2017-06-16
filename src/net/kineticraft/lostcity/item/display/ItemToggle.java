package net.kineticraft.lostcity.item.display;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * An item that holds onto whether or not its toggled.
 *
 * Created by Kneesnap on 6/9/2017.
 */
@Getter
public class ItemToggle extends GUIItem {

    private boolean toggled;
    private DyeColor trueColor;
    private String toggleName;
    private String description;

    private static final ChatColor ENABLED = ChatColor.GREEN;
    private static final ChatColor DISABLED = ChatColor.RED;

    public ItemToggle(String name) {
        this(name, name.toLowerCase());
    }

    public ItemToggle(String name, String description) {
        this(name, description, false);
    }

    public ItemToggle(String name, String description, boolean state) {
        this(name, description, state, DyeColor.LIME);
    }

    public ItemToggle(String name, String desc, boolean enabled, DyeColor enabledColor) {
        super(new ItemStack(Material.INK_SACK));
        this.toggled = enabled;
        this.trueColor = enabledColor;
        this.toggleName = name;
        this.description = desc;
        anyClick(e -> {
            toggle();
            e.getGUI().reconstruct();
        });
    }

    @Override
    public void updateItem() {
        setColor(isToggled() ? getTrueColor() : DyeColor.GRAY);

        ChatColor color = isToggled() ? ENABLED : DISABLED;
        setDisplayName(color + getToggleName());

        if (getDescription() != null)
            addLore("Click here to toggle " + getDescription() + ".");

        addLore("Currently " + color + (isToggled() ? "enabled" : "disabled") + ChatColor.GRAY + ".");

        super.updateItem();
    }

    /**
     * Toggle the current state.
     * @return this
     */
    public ItemToggle toggle() {
        this.toggled = !this.toggled;
        return this;
    }
}
