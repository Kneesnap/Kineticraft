package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.AFK;
import net.kineticraft.lostcity.mechanics.Chat;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Send a private message.
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandMessage extends PlayerCommand {

    public CommandMessage() {
        super( "<player> <message>", "Send a private message to a player.", "msg", "m", "w", "tell", "whisper", "t");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        String message = ChatColor.DARK_GRAY + ": " + ChatColor.WHITE
                + Chat.applyAllFilters(sender, String.join(" ", skipArgs(args, 1)));
        CommandSender receiver = args[0].equalsIgnoreCase("CONSOLE")
                ? Bukkit.getConsoleSender() : Bukkit.getPlayer(args[0]);

        if (!Utils.isVisible(sender, receiver))
            return;

        if (receiver instanceof Player && AFK.isAFK((Player) receiver))
            sender.sendMessage(ChatColor.GRAY + "This player is AFK, and may not receive your message.");

        // Send display to sender
        String rName = Utils.getSenderName(receiver);
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "TO " + rName + message);
        if (sender instanceof Player)
            MetadataManager.setMetadata((Player) sender, "lastWhisper", receiver.getName());

        // Send display to receiver.
        if (receiver instanceof Player) {
            Player p = (Player) receiver;
            if (KCPlayer.getWrapper(p).isIgnoring(sender))
                return;

            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, .75F);
            MetadataManager.setMetadata(p, "lastWhisper", sender.getName());
        }

        receiver.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "FROM " + Utils.getSenderName(sender) + message);
    }
}
