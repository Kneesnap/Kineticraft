package net.kineticraft.lostcity.commands.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow players to change their local time.
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandPTime extends PlayerCommand {

    public CommandPTime() {
        super(EnumRank.SIGMA, "<time|day|night|midnight|sunrise|reset>", "Set your local time.", "ptime");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        for (Time t : Time.values()) {
            if (t.name().equalsIgnoreCase(args[0])) {
                p.setPlayerTime(t.getTime(), false);
                p.sendMessage(ChatColor.GOLD + "Time set to " + t.name().toLowerCase() + ".");
                return;
            }
        }

        if(args[0].equalsIgnoreCase("reset")) {
            p.resetPlayerTime();
            sender.sendMessage(ChatColor.GOLD + "Clock synced.");
        } else {
            p.setPlayerTime(Integer.parseInt(args[0]), false);
            sender.sendMessage(ChatColor.GOLD + "Time set to " + ChatColor.RED + args[0]);
        }
    }

    @AllArgsConstructor @Getter
    private enum Time {
        DAY(1000L),
        NIGHT(13000L),
        MIDNIGHT(18000L),
        SUNRISE(22916L);

        private final long time;
    }
}
