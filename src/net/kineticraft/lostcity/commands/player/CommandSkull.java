package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allow players to spawn in skulls of a certain player.
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandSkull extends PlayerCommand {

    public CommandSkull() {
        super(EnumRank.PHI, "<name> [amount]", "Spawn a skull-item.", "skull");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (MetadataManager.updateCooldown((Player) sender, "skull", 200))
            return;

        ItemStack skull = ItemManager.makeSkull(args[0]);
        if (args.length > 1)
            skull.setAmount(Math.max(1, Math.min(Integer.parseInt(args[1]), 10)));
        Utils.giveItem((Player) sender, skull);
    }
}
