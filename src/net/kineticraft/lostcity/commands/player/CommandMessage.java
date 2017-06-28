package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.mechanics.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Send a private message.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandMessage extends PlayerCommand {

    public CommandMessage() {
        super( "<player> <message>", "Send a private message to a player.", "msg", "m", "w", "tell", "whisper", "t");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        String message = String.join(" ", skipArgs(args, 1));
        if (Utils.getRank(sender).isAtLeast(EnumRank.OMEGA)) // Color the message if the sender is at least Omega.
            message = ChatColor.translateAlternateColorCodes('&', message);
        message = ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + message; // Add prefix.

        CommandSender receiver = args[0].equalsIgnoreCase("CONSOLE")
                ? Bukkit.getConsoleSender() : Bukkit.getPlayer(args[0]);

        if (receiver == null || (receiver instanceof Player && KCPlayer.getWrapper((Player) receiver).isVanished(sender))) {
            sender.sendMessage(ChatColor.RED + "Player is offline.");
            return;
        }

        // Send display to sender
        String rName = Utils.getSenderName(receiver);
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "TO " + rName + message);
        if (sender instanceof Player)
            MetadataManager.setMetadata((Player) sender, MetadataManager.Metadata.LAST_WHISPER, receiver.getName());

        // Send display to receiver.
        if (receiver instanceof Player) {
            Player p = (Player) receiver;
            if (KCPlayer.getWrapper(p).isIgnoring(sender))
                return;

            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, .75F);
            MetadataManager.setMetadata(p, MetadataManager.Metadata.LAST_WHISPER, sender.getName());
        }

        receiver.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "FROM " + Utils.getSenderName(sender) + message);
    }
}
