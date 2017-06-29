package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.mechanics.Chat;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
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
        String message = ChatColor.DARK_GRAY + ": " + ChatColor.WHITE
                + Chat.applyAllFilters(sender, String.join(" ", skipArgs(args, 1)));
        CommandSender receiver = args[0].equalsIgnoreCase("CONSOLE")
                ? Bukkit.getConsoleSender() : Bukkit.getPlayer(args[0]);

        if (!Utils.isVisible(sender, args[0]))
            return;

        // Send display to sender
        String rName = Utils.getSenderName(receiver);
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "TO " + rName + message);
        if (sender instanceof Player)
            MetadataManager.setMetadata((Player) sender, Metadata.LAST_WHISPER, receiver.getName());

        // Send display to receiver.
        if (receiver instanceof Player) {
            Player p = (Player) receiver;
            if (KCPlayer.getWrapper(p).isIgnoring(sender))
                return;

            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, .75F);
            MetadataManager.setMetadata(p, Metadata.LAST_WHISPER, sender.getName());
        }

        receiver.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "FROM " + Utils.getSenderName(sender) + message);
    }
}
