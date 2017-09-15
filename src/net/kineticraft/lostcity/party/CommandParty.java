package net.kineticraft.lostcity.party;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport to a party.
 * Created by Kneesnap on 9/14/2017.
 */
public class CommandParty extends PlayerCommand {
    public CommandParty() {
        super("", "Warp to the active party.", "party");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (Parties.isPartyTime()) {
            Parties.getParty().teleportIn(p);
        } else if (Utils.isStaff(sender)) {
            p.teleport(Parties.getPartyWorld().getSpawnLocation());
            sender.sendMessage("There is no active party, so you have been teleported to the spawn-location.");
        } else {
            sender.sendMessage(ChatColor.RED + "There is no party right now.");
        }
    }
}
