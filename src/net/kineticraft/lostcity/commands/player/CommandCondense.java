package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.command.CommandSender;

/**
 * Allow players to condense their items.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandCondense extends PlayerCommand {
    public CommandCondense() {
        super(EnumRank.ALPHA, true, "", "Condense all items in your inventory.", "stack", "condense");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

    }
}
