package net.kineticraft.lostcity.commands.misc;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.config.Configs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Command that will display all lines of a config file.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandInfo extends PlayerCommand {

    private Configs.ConfigType type;

    public CommandInfo(Configs.ConfigType type, String help, String... alias) {
        super("", help, alias);
        this.type = type;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        for (String s : Configs.getRawConfig(this.type).getLines()) {
            s = ChatColor.translateAlternateColorCodes('&', s);
            if (s.startsWith("{") || s.startsWith("[")) {
                // It's some mojangson.
                sender.sendMessage();
            } else {
                // It's a raw text line.
                sender.sendMessage(s);
            }
        }
    }
}
