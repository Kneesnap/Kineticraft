package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.TimeInterval;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Allows someone to experience tranquility.
 * Created by Kneesnap on 9/14/2017.
 */
public class CommandZen extends StaffCommand {

    public CommandZen() {
        super("<player>", "Calm a player down.", "zen");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (!Utils.isVisible(sender, args[0]))
            return;

        Player p = Bukkit.getPlayer(args[0]);
        KCPlayer.getWrapper(p).setZenMode(new Date().getTime() + TimeUnit.DAYS.toMillis(1));
        p.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD
                + Utils.randElement("Experience Tranquility", "Pass into the Iris") + ".");
    }
}
