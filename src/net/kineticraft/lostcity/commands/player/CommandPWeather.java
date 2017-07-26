package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow players to edit their own weather.
 * Created by Kneesnap on 7/17/2017.
 */
public class CommandPWeather extends PlayerCommand {

    public CommandPWeather() {
        super(EnumRank.GAMMA, "<downfall|clear|reset>", "Set your local weather.", "pweather");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("reset")) {
            ((Player) sender).resetPlayerWeather();
            sender.sendMessage(ChatColor.GOLD + "Weather returned to normal.");
            return;
        }

        ((Player) sender).setPlayerWeather(WeatherType.valueOf(args[0].toUpperCase()));
        sender.sendMessage(ChatColor.GOLD + "Weather set to " + ChatColor.RED + args[0]);
    }
}
