package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow players to spawn in skulls of a certain player.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandSkull extends PlayerCommand {

    public CommandSkull() {
        super(EnumRank.GAMMA, "<name>", "Spawn a skull-item.", "skull");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Utils.giveItem((Player) sender, ItemManager.makeSkull(args[0]));
    }
}
