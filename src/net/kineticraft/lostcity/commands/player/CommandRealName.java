package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by egoscio on 6/25/17.
 */
public class CommandRealName extends PlayerCommand {

    public CommandRealName() {
        super("<nickname>", "Displays a player's real name.", "realname", "rn");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Set<Player> matchSet = Bukkit.getOnlinePlayers().stream().filter(player -> {
            KCPlayer kcPlayer = KCPlayer.getWrapper(player);
            String nickname = kcPlayer.getNickname();
            return nickname.replaceAll("§(?:\\d|[a-f]|[k-o]|r)", "").toLowerCase().contains(args[0].toLowerCase());
        }).collect(Collectors.toSet());
        if (matchSet.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Not matches have been found.");
        } else {
            String matchString = Utils.join(ChatColor.GRAY + ", ", matchSet, m -> ChatColor.GREEN + m.getName());
            sender.sendMessage(ChatColor.GRAY + "Matches: " + matchString);
        }
    }

}