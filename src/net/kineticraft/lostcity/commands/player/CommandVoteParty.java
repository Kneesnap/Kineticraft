package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.config.Configs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * List votes until a vote party.
 * Created by Kneesnap on 7/9/2017.
 */
public class CommandVoteParty extends PlayerCommand {

    public CommandVoteParty() {
        super("", "See how many votes are needed for a vote party.", "vp", "party");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GRAY + "Votes until party: "
                + ChatColor.GREEN + Configs.getVoteData().getVotesUntilParty());
    }
}
