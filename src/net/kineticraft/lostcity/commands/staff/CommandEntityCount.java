package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Pair;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Local and Global Entity Counter.
 * Created by Egoscio on 8/10/17.
 */

public class CommandEntityCount extends StaffCommand {

    private static final int MAX_RADIUS = 500;

    public CommandEntityCount() {
        super("[radius] [global] [type]", "List nearby entities.", "entitycount", "ec");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player pSender = (Player) sender;
        int radius = args.length > 0 ? Math.min(Integer.parseInt(args[0]), MAX_RADIUS) : 100;
        boolean global = args.length > 1 ? Boolean.valueOf(args[1]) : true;
        EntityType type = args.length > 2 ? stringToType(args[2]) : null;

        sender.sendMessage(ChatColor.GRAY + "Searching in radius: " + ChatColor.GREEN + radius +
                ChatColor.GRAY + " in context: " + ChatColor.GREEN + (global ? "GLOBAL" : "LOCAL") +
                ChatColor.GRAY + " for type: " + ChatColor.GREEN + (type == null ? "ALL" : type) +
                ChatColor.GRAY + ".");

        List<Pair<String, Integer>> results;

        if (global) {
            results = streamToSortedList(
                    Bukkit.getOnlinePlayers().stream()
                            .map(p -> new Pair<>(p.getName(), countNear(p.getLocation(), radius, type).values().stream().reduce(Integer::sum).orElse(null)))
                            .filter(p -> p.snd != null)
            );
        } else {
            Map<EntityType, Integer> near = countNear(pSender.getLocation(), radius, type);
            results = streamToSortedList(
                    near.keySet().stream()
                            .map(p -> new Pair<>(p.getEntityClass().getSimpleName(), near.get(p)))
            );
        }

        if (results.isEmpty())
            sender.sendMessage(" - None");

        for (Pair<String, Integer> p : results)
            sender.sendMessage(" - " + ChatColor.GRAY + p.fst + ": " +
            ChatColor.YELLOW + p.snd);

    }

    private static EntityType stringToType(String type) {
        try {
            return EntityType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static List<Pair<String, Integer>> streamToSortedList(Stream<Pair<String, Integer>> stream) {
        return stream
                .sorted(Comparator.comparing(p -> -p.snd))
                .collect(Collectors.toList());
    }

    private static Map<EntityType, Integer> countNear(Location loc, int radius, EntityType type) {
        Map<EntityType, Integer> entityMap = new HashMap<>();

        Utils.getNearbyEntities(loc, radius).stream()
                .filter(e -> (type == null || type == e.getType()) && e instanceof LivingEntity)
                .forEach(e -> {
                    entityMap.put(e.getType(), entityMap.getOrDefault(e.getType(), 0) + 1);
                });

        return entityMap;
    }
}