package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CommandReport extends PlayerCommand {

    public CommandReport() {
        super("<player|location|glitch|other>", "Submit a report.","report");
    }

    private static List<String> types = Arrays.asList("player", "location", "glitch", "other");
    private static String prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DOG" + ChatColor.GREEN + " "
            + "Report" + ChatColor.GRAY + ": " + ChatColor.WHITE;
    private static List<String> greetingMessages = Arrays.asList(
            "Hi there!",
            "Hey!",
            "Hello there!",
            "Hi :)",
            "Bonjour!",
            "Sup dawg."
    );
    private static List<String> exitMessages = Arrays.asList(
            "Thanks for reporting, have a pleasant day!",
            "Your report has been processed, big brother is watching!",
            "Thanks report, have happy joy-joy day.",
            "Your Pokemon are now healed, we hope to see you again soon!",
            "Compiling tattle...transfluxing signal...report sent! Thanks!",
            "Report sent, don't forget to cookie your Mod.",
            "Thanks for looking out homie, we got your report!"
    );

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        String type = args[0].toLowerCase();
        if (types.contains(type)) {
            sender.sendMessage(prefix +
                    Utils.randElement(greetingMessages) +
                    " Everything said from now until the end of the report remains between us.");
            if (type.equals("player")) {
                sender.sendMessage(prefix + "Who are you reporting?");
                Callbacks.listenForChat(player, subject -> {
                    sender.sendMessage(prefix + "What did " + subject + " do, exactly?");
                    Callbacks.listenForChat(player, description -> submitReport(player, type, subject, description));
                });
            } else {
                sender.sendMessage(prefix + "Please describe the situation in as much detail as possible.");
                Callbacks.listenForChat(player, description -> submitReport(player, type, null, description));
            }
        } else {
            showUsage(sender);
        }
    }

    private static void submitReport(Player reporter, String type, String subject, String description) {
        Location loc = reporter.getLocation();
        String message = "" +
                "New **" + type + "** report from `" + reporter.getName() + "`\n" +
                "Time: `" + new Date().toString() + "`\n" +
                (subject != null ? "Subject: `" + subject + "`\n" : "") +
                "Teleport: `/tl " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + (int) loc.getYaw() + " " + (int) loc.getPitch() + " " + loc.getWorld().getName() + "`\n" +
                "Description:\n" +
                "```\n" +
                description + "\n" +
                "```";
        DiscordAPI.sendMessage(DiscordChannel.REPORTS, message);
        reporter.sendMessage(prefix + Utils.randElement(exitMessages));
    }

}