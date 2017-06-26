package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.mechanics.Punishments;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Check when a player was last seen.
 *
 * Created by Kneesnap on 6/17/2017.
 */
public class CommandSeen extends PlayerCommand {

    public CommandSeen() {
        super(EnumRank.MU, false, "<player>", "Check when a player was last seen.", "seen");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (QueryTools.isBusy(sender))
            return;

        QueryTools.getData(args[0], p -> {
            long seenTime = p.isOnline() ? 0 : System.currentTimeMillis() - Bukkit.getOfflinePlayer(p.getUuid()).getLastPlayed();

            sender.sendMessage(ChatColor.GRAY + "Showing report of " + ChatColor.GRAY + p.getUsername() + ChatColor.GRAY + ":");
            sender.sendMessage(" - " + ChatColor.GRAY + "Last Seen: " + ChatColor.WHITE + Utils.formatTime(seenTime));

            if (Utils.getRank(sender).isAtLeast(EnumRank.HELPER)) {
                sender.sendMessage(" - " + ChatColor.GRAY + "IP Address: " + ChatColor.WHITE + p.getLastIP());

                if (!p.getPunishments().isEmpty()) {
                    sender.sendMessage(" - "  + Utils.formatToggle("Banned", p.isBanned()));
                    sender.sendMessage(ChatColor.GRAY + "Punishments:");
                    p.getPunishments().stream().map(Punishments.Punishment::toString).forEach(sender::sendMessage);
                }
            }
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
