package net.kineticraft.lostcity.item.items.books;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.item.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Record information relevant to staff for a player.
 * Created by Kneesnap on 7/24/2017.
 */
@Setter @Getter
public class ItemBookNotes extends ItemInputBook {

    private String player;

    public ItemBookNotes(KCPlayer target) {
        super(ItemType.PLAYER_NOTEBOOK, target.getNotes());
        setPlayer(target.getUsername());
    }

    public ItemBookNotes(ItemStack item) {
        super(item);
        setPlayer(getTagString("player"));
    }

    @Override
    public void updateItem() {
        setTagString("player", getPlayer());
        super.updateItem();
    }

    @Override
    protected void onUpdate(Player player) {
        QueryTools.getData(getPlayer(), p -> {
            p.setNotes(getLines());
            p.writeData();
            player.sendMessage(ChatColor.GREEN + "Notes updated.");
        });
    }
}
