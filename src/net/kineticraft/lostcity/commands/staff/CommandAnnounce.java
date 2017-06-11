package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Adds an an announcement to the file.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandAnnounce extends StaffCommand {

    public CommandAnnounce() {
        super(EnumRank.ADMIN, false, "<message>", "Add a server announcement", "announce");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Configs.getRawConfig(Configs.ConfigType.ANNOUNCER).getLines()
                .add(TextUtils.toString(TextUtils.fromLegacy(String.join(" ", args))));
        Configs.getRawConfig(Configs.ConfigType.ANNOUNCER).saveToDisk();
        sender.sendMessage(ChatColor.GREEN + "Announce added.");
    }
}
