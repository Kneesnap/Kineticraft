package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Get entities near the CommandSender.
 * Created by Kneesnap on 6/27/17.
 */
public class CommandNear extends StaffCommand {

    private static final int MAX_RADIUS = 500;

    public CommandNear() {
        super("[radius] [all]", "Get all nearby players/entities.", "near");
    }
    
    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        int radius = Math.min(args.length > 0 ? Integer.parseInt(args[0]) : 100, MAX_RADIUS);
        boolean allEntities = args.length > 1 && Boolean.valueOf(args[1]);
        Location origin = ((Player) sender).getLocation();

        List<Entity> results = Utils.getNearbyEntities(origin, radius).stream()
                .filter(e -> (e instanceof Player || allEntities) && e != sender).collect(Collectors.toList());

        sender.sendMessage(ChatColor.GRAY + "Nearby:");
        if (results.isEmpty())
            sender.sendMessage(" - None");

        for (Entity e : results)
            sender.sendMessage(" - " + ChatColor.GRAY + e.getName() + ": " + ChatColor.YELLOW
                    + Math.round(Math.sqrt(e.getLocation().distanceSquared(origin))));
    }
}
