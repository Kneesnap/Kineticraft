package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allow players to condense their items.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandCondense extends PlayerCommand {
    public CommandCondense() {
        super(EnumRank.ALPHA, "", "Condense all items in your inventory.", "stack", "condense");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
            if (item != null)
                player.getInventory().addItem(item);
        }
    }
}
