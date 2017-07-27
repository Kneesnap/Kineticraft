package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Reload playerdata.
 * Created by Kneesnap on 6/30/2017.
 */
public class CommandReloadData extends StaffCommand {
    public CommandReloadData() {
        super(EnumRank.ADMIN, "<player>", "Reload a player's playerdata.", "reload", "data");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        QueryTools.getData(args[0], d -> {
            KCPlayer.getPlayerMap().put(d.getUuid(), KCPlayer.loadWrapper(d.getUuid()));
            sender.sendMessage(ChatColor.GREEN + d.getUsername() + "'s data has been reloaded.");
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
