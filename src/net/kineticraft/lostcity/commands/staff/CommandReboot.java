package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.mechanics.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Reboots the server.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandReboot extends StaffCommand {

    public CommandReboot() {
        super(EnumRank.ADMIN, "", "Reboot the server", "reboot", "shutdown");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sendReminder(60);
        sendReminder(30);
        sendReminder(10);

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            Core.announce("Server is rebooting now.");
            DataHandler.saveAllPlayers();
            Core.getMainWorld().setAutoSave(true);
            Core.getMainWorld().save();
            Bukkit.getServer().shutdown();
        }, 20 * 60);
    }

    private void sendReminder(int seconds) {
        Bukkit.getScheduler().runTaskLater(Core.getInstance(),
                () -> Core.announce("Server rebooting in " + Utils.formatTime(seconds * 1000)), (60 - seconds) * 20);
    }
}
