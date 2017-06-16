package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.command.CommandSender;

/**
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandNick extends PlayerCommand {

    public CommandNick() {
        super(EnumRank.OMEGA, true, "<nick>", "Change your nickname.", "nick");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

    }
}
