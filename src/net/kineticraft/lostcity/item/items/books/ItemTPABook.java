package net.kineticraft.lostcity.item.items.books;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allow teleporting to other players.
 *
 * Created by Kneesnap on 6/12/2017.
 */
public class ItemTPABook extends ItemBook {

    public ItemTPABook() {
        super(ItemType.TPA_BOOK);
    }

    public ItemTPABook(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void updateItem() {
        setTitle("Teleport Book");
        setAuthor("Kineticraft Staff");
        addText(TextUtils.centerBook("[Teleport Book]"));
        for (Player p : Core.getOnlinePlayers()) {
            KCPlayer w = KCPlayer.getWrapper(p);
            addLine(w.getRank().getNameColor() + "[" + w.getUsername() + "]").runCommand("/trigger tpa set X");
        }

        super.updateItem();
    }
}
