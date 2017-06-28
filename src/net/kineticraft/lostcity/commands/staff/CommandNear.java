package net.kineticraft.lostcity.commands.staff;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by egoscio on 6/26/17.
 */
public class CommandNear extends StaffCommand {

    public CommandNear() {
        super("[radius] [all?]", "Get all nearby players, or entities as well if all is true.", "near");
    }
    
    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        final int maxRadius = 500;
        Player player = (Player) sender;
        Integer radius = 100;
        Boolean allEntities = false;
        if (args.length > 0) {
            try {
                int parsed = Integer.parseUnsignedInt(args[0]);
                radius = parsed <= maxRadius ? parsed : maxRadius;
            } catch (NumberFormatException error) {
                radius = 100;
            }
            if (args.length > 1) {
                allEntities = Boolean.valueOf(args[1]);
            }
        }
        List<Entity> matches = player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player || allEntities).collect(Collectors.toList());
        if (matches.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No matches have been found.");
        } else {
            String matchString = Utils.join("\n", matches, m -> ChatColor.GRAY + " - " + ChatColor.GREEN + m.getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + Math.sqrt(m.getLocation().distanceSquared(player.getLocation())));
        }
    }
}
