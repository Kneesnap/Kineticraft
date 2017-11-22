package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows media+ to vanish.
 * Created by Kneesnap on 6/11/2017.
 */
public class CommandVanish extends StaffCommand {

    public CommandVanish() {
        super(EnumRank.MEDIA, "[message]", "Vanish from the game.", "vanish", "unvanish");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        KCPlayer kc = KCPlayer.getWrapper(p);
        kc.vanish(!kc.isVanished());
        sender.sendMessage(Utils.formatToggle("Vanished", kc.isVanished()));
        Core.alertStaff(kc.getColoredName() + ChatColor.GRAY + " has " + (kc.isVanished() ? "" : "un") + "vanished.");
        if (isArg(args, 0, "message"))
            Core.broadcast(ChatColor.YELLOW.toString() + ChatColor.BOLD + " > " + kc.getTemporaryRank().getNameColor()
                    + kc.getUsername() + ChatColor.YELLOW + " has " + (kc.isVanished() ? "left" : "joined") + ".");
    }
}
