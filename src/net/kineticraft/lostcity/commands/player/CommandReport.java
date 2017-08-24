package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class CommandReport extends PlayerCommand {

    public CommandReport() {
        super("<location|player|chat> <description>", "Report a location, player, or moment in chat.", "report");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        String type = args[0].toLowerCase();
        String name = player.getName();
        String loc = Utils.toString(player.getLocation());
        String time = Utils.formatDate(new Date());
        String desc = String.join(" ", skipArgs(args, 1));

        DiscordAPI.sendMessage(DiscordChannel.REPORTS, "\n" +
                "New **" + type + "** report from `" + name + "`:\n" +
                "\tLocation: `" + loc + "`\n" +
                "\tTimestamp: `" + time + "`\n" +
                "\tDescription:\n" +
                "```\n" +
                desc + "\n" +
                "```"
        );

    }

}