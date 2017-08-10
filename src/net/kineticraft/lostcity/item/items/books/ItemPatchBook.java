package net.kineticraft.lostcity.item.items.books;

import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * PatchBook - Show changelog.
 * Created by Kneesnap on 6/30/2017.
 */
public class ItemPatchBook extends ItemBookFile {

    public ItemPatchBook() {
        super(ItemType.PATCHNOTES_BOOK, "patchnotes");
    }

    public ItemPatchBook(ItemStack item) {
        super(item);
    }

    @Override
    public void updateItem() {
        addCenteredText(ChatColor.BOLD + "Build #" + Configs.getMainConfig().getBuild());
        addLine("");

        super.updateItem();
    }

    @Override
    public void open(Player player) {
        super.open(player);
        KCPlayer.getWrapper(player).setLastBuild(Configs.getMainConfig().getBuild());
    }
}
