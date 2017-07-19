package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Show who you're ignoring.
 * Created by Kneesnap on 7/19/2017.
 */
public class CommandIgnoreList extends PlayerCommand {

    public CommandIgnoreList() {
        super("", "Show a list of players you're ignoring.", "ignorelist");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        String ignoring = String.join(ChatColor.GRAY + ", " + ChatColor.WHITE, KCPlayer.getWrapper(sender).getIgnored());
        sender.sendMessage(ChatColor.GRAY + "Ignoring: "
                + ChatColor.WHITE + (ignoring.length() > 0 ? ignoring : ChatColor.GREEN + "None"));
    }
}
