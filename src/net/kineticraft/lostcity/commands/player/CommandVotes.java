package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.persistence.Query;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Show top voters.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandVotes extends PlayerCommand {

    private static final int ENTRIES = 10;

    public CommandVotes() {
        super("", "Shows how many times you have voted.", "");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (QueryTools.isBusy(sender))
            return;

        QueryTools.queryData(stream -> {
            List<KCPlayer> list = stream.sorted(Comparator.comparing(KCPlayer::getMonthlyVotes)).collect(Collectors.toList());
            KCPlayer p = KCPlayer.getWrapper((Player) sender);

            int show = Math.min(ENTRIES, list.size());
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.UNDERLINE
                    + "----------" + ChatColor.AQUA + "Top " + show + " Monthly Voters" + ChatColor.GRAY + ChatColor.UNDERLINE + "----------");
            for (int i = 0; i < show; i++) {
                KCPlayer player = list.get(i);
                sender.sendMessage((player.getUsername().equals(p.getUsername()) ? ChatColor.GREEN : ChatColor.AQUA)
                        + ChatColor.GRAY.toString() + ": " + player.getMonthlyVotes());
            }

            sender.sendMessage(ChatColor.GRAY + "Monthly Votes: " + ChatColor.AQUA + p.getMonthlyVotes());
            sender.sendMessage(ChatColor.GRAY + "Total Votes: " + ChatColor.AQUA + p.getTotalVotes());
        });
    }
}
