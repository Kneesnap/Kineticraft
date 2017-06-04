package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.Configs.ConfigType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows staff to reload configs.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class CommandConfig extends StaffCommand {

    public CommandConfig() {
        super(EnumRank.ADMIN, false,"[config]", "Reload configs.", "config");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ConfigType type = ConfigType.valueOf(args[0].toUpperCase());
            reloadConfig(type);
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded " + type.name().toLowerCase() + ".json");
        } else {
            for (ConfigType type : ConfigType.values())
                reloadConfig(type);
            sender.sendMessage(ChatColor.GREEN + "Reloaded all configs.");
        }
    }

    private void reloadConfig(ConfigType type) {
        Configs.getConfig(type).loadFromDisk();
    }
}
