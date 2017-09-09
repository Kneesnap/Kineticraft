package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/**
 * Teleport to a location.
 * Created by Egoscio on 8/31/2017.
 */

public class CommandToLocation extends StaffCommand {

    public CommandToLocation() {
        super("<x> <y> <z> [yaw] [pitch] [world]", "Teleport to a location.", "tolocation", "toloc", "tl");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String x, y, z, yaw, pitch, world;

        x = args[0];
        y = args[1];
        z = args[2];
        yaw = args.length > 3 ? args[3] : Float.toString(player.getLocation().getYaw());
        pitch = args.length > 4 ? args[4] : Float.toString(player.getLocation().getPitch());
        world = args.length > 5 ? args[5] : player.getWorld().getName();

        World worldParsed = Bukkit.getWorld(world);
        if (worldParsed == null) {
            sender.sendMessage(ChatColor.RED + "Invalid world: " + world);
            showUsage(player);
            return;
        }

        try {
            player.teleport(new Location(
                    worldParsed,
                    Double.parseDouble(x),
                    Double.parseDouble(y),
                    Double.parseDouble(z),
                    Float.parseFloat(yaw),
                    Float.parseFloat(pitch)
            ));
            player.sendMessage(ChatColor.GREEN + "Teleported to location.");
        } catch (NumberFormatException e) {
            showUsage(player);
        }
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.RED + "Worlds: " + ChatColor.YELLOW + Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.joining(", ")));
    }

}
