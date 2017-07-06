package net.kineticraft.lostcity.item.items.books;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.KCPlayer;
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
        super(ItemType.TPA_BOOK, "Teleport Book");
    }

    public ItemTPABook(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void updateItem() {
        addCenteredText("[Teleport Book]");
        addLine("");
        for (Player p : Core.getOnlinePlayers()) {
            KCPlayer w = KCPlayer.getWrapper(p);
            String cmd = "/trigger tpa set " + w.getAccountId();
            addLine(TextUtils.centerBook(w.getRank().getNameColor() + "[" + w.getUsername() + "]")).runCommand(cmd).showText(cmd);
        }

        super.updateItem();
    }
}
