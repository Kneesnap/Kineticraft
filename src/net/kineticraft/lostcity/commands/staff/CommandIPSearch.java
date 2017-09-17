package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.data.QueryTools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

/**
 * Find ban evaders.
 * Created by Kneesnap on 8/19/2017.
 */
public class CommandIPSearch extends StaffCommand {
    public CommandIPSearch() {
        super("<ip>", "Search for an IP.", "seenip");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        QueryTools.queryData(data -> {
            sender.sendMessage(ChatColor.RED + "Accounts with IPs that start with '" + args[0] + "': ");
            sender.sendMessage(data.filter(k -> k.getLastIP() != null &&k.getLastIP().startsWith(args[0]))
                    .map(KCPlayer::getUsername).collect(Collectors.joining(", ")));
        });
    }
}
