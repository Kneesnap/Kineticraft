package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.mechanics.Voting;
import org.bukkit.command.CommandSender;

/**
 * Allows staff to add votes.
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandTestVote extends StaffCommand {

    public CommandTestVote() {
        super(EnumRank.MOD, "<player>", "Simulate a vote.", "testvote", "fakevote");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Voting.handleVote(args[0]);
    }
}
