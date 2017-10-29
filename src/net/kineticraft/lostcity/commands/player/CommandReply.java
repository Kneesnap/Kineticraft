package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.CommandType;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Reply to the last person you private messaged.
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandReply extends PlayerCommand {

    public CommandReply() {
        super("<message>", "Reply to your last private message.", "r", "reply");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String lastMessage = MetadataManager.getValue(player, "lastWhisper");

        if (lastMessage == null) {
            player.sendMessage(ChatColor.RED + "You have nobody whom you can reply to.");
            return;
        }

        // Send the message.
        Commands.handleCommand(sender, CommandType.SLASH, "/msg " + lastMessage + " " + String.join(" ", args));
    }
}
