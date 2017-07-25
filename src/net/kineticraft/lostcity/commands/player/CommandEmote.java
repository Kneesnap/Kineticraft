package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.command.CommandSender;

/**
 * A custom implementation of /me.
 * Created by Kneesnap on 7/24/2017.
 */
public class CommandEmote extends PlayerCommand {

    public CommandEmote() {
        super("<action>", "Broadcast an action", "me", "emote", "action");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Core.broadcast(" * " + sender.getName() + " " + String.join(" ", args));
    }
}
