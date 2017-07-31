package net.kineticraft.lostcity.config;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.config.Configs.ConfigType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Allows staff to reload configs.
 * Created by Kneesnap on 6/3/2017.
 */
public class CommandConfig extends StaffCommand {

    public CommandConfig() {
        super(EnumRank.ADMIN,"<config|all>", "Reload a config.", "config");
        autocomplete(ConfigType.values());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("all")) {
            Arrays.stream(ConfigType.values()).forEach(this::reloadConfig);
            sender.sendMessage(ChatColor.GREEN + "Reloaded all configs.");
            return;
        }

        ConfigType type = ConfigType.valueOf(args[0].toUpperCase());
        reloadConfig(type);
        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded " + type.name().toLowerCase() + ".");
    }

    private void reloadConfig(ConfigType type) {
        Configs.getConfig(type).loadFromDisk();
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(Arrays.stream(ConfigType.values()).map(t -> t.name().toLowerCase()).
                collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.GREEN,
                        ChatColor.GRAY + "Configs: " + ChatColor.GREEN,
                        ChatColor.GRAY + ", " + ChatColor.GREEN + "all")));
    }
}
