package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.discord.DiscordSender;
import net.kineticraft.lostcity.mechanics.Punishments;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Check when a player was last seen.
 * Created by Kneesnap on 6/17/2017.
 */
public class CommandSeen extends PlayerCommand {

    public CommandSeen() {
        super("<player>", "Check when a player was last seen.", "seen");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

        QueryTools.getData(args[0], p -> {
            long now = System.currentTimeMillis();
            long seenTime = now - (p.isOnline() ?
                    (p.isVanished() ? MetadataManager.getMetadata(p.getPlayer(), Metadata.VANISH_TIME).asLong() : now)
                    : Bukkit.getOfflinePlayer(p.getUuid()).getLastPlayed());

            sender.sendMessage(ChatColor.GRAY + "Showing report of " + ChatColor.GRAY + p.getUsername() + ChatColor.GRAY + ":");
            sendValue(sender, "Last Seen", Utils.formatTime(seenTime));

            // Show extra data to helpers.
            if (Utils.isStaff(sender) && !(sender instanceof DiscordSender))
                sendValue(sender, "IP Address", p.getLastIP());

            // Show punishments.
            sendValue(sender, "Muted", p.isMuted());

            if (!p.getPunishments().isEmpty()) {
                sendValue(sender, "Banned", p.isBanned());
                sender.sendMessage(ChatColor.GRAY + "Punishments:");
                p.getPunishments().stream().map(Punishments.Punishment::toString).map(s -> " - " + s).forEach(sender::sendMessage);
            }

        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
