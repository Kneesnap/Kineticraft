package net.kineticraft.lostcity.commands;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.discord.*;
import net.kineticraft.lostcity.commands.misc.*;
import net.kineticraft.lostcity.commands.player.*;
import net.kineticraft.lostcity.commands.staff.*;
import net.kineticraft.lostcity.commands.trigger.*;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.Configs.ConfigType;
import net.kineticraft.lostcity.guis.GUIType;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.mechanics.Chat;
import net.kineticraft.lostcity.mechanics.Mechanic;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles command usage.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class Commands extends Mechanic {

    @Getter
    private static List<Command> commands = new ArrayList<>();

    /**
     * Register all commands.
     */
    private static void registerCommands() {

        // Register books
        addCommand(new CommandBook(ItemType.PATCHNOTES_BOOK, "patchnotes", "patch", "build", "changelog"));

        // Register GUI commands
        addCommand(new CommandGUI(EnumRank.THETA, GUIType.DONOR, "Access donor perks.", "donor"));

        // Register config commands
        addCommand(new CommandInfo(Configs.ConfigType.COLORS, "List chat color codes", "color", "colors", "colour", "colours"));
        addCommand(new CommandInfo(ConfigType.DISCORD, "List discord information", "discord"));
        addCommand(new CommandInfo(ConfigType.DUNGEON, "List dungeon information", "dungeon", "dungeons"));
        addCommand(new CommandInfo(ConfigType.DONATE, "How to donate / donor perks.", "donate", "shop"));
        addCommand(new CommandInfo(ConfigType.INFO, "General server information.", "info", "einfo"));
        addCommand(new CommandInfo(ConfigType.RULES, "Server rules.", "rules", "info"));
        addCommand(new CommandInfo(ConfigType.VOTE, "Information on voting.", "vote"));

        // Register player commands
        addCommand(new CommandCondense());
        addCommand(new CommandDelHome());
        addCommand(new CommandHelp());
        addCommand(new CommandHat());
        addCommand(new CommandHome());
        addCommand(new CommandIgnore());
        addCommand(new CommandMessage());
        addCommand(new CommandMail());
        addCommand(new CommandNick());
        addCommand(new CommandPTime());
        addCommand(new CommandReply());
        addCommand(new CommandRanks());
        addCommand(new CommandRankup());
        addCommand(new CommandRTP());
        addCommand(new CommandSeen());
        addCommand(new CommandSetHome());
        addCommand(new CommandShovel());
        addCommand(new CommandSkull());
        addCommand(new CommandSpawn());
        addCommand(new CommandUnignore());
        addCommand(new CommandTPA());
        addCommand(new CommandTPBook());
        addCommand(new CommandVotes());
        addCommand(new CommandRealName());
        addCommand(new CommandWhyLag());
        addCommand(new CommandVerify());

        // Register staff commands
        addCommand(new CommandBackup());
        addCommand(new CommandBright());
        addCommand(new CommandBroadcast());
        addCommand(new CommandConfig());
        addCommand(new CommandDeathTeleport());
        addCommand(new CommandFly());
        addCommand(new CommandGUIs());
        addCommand(new CommandKick());
        addCommand(new CommandMined());
        addCommand(new CommandMute());
        addCommand(new CommandNear());
        addCommand(new CommandPunish());
        addCommand(new CommandReboot());
        addCommand(new CommandRescue());
        addCommand(new CommandSetRank());
        addCommand(new CommandSpectator());
        addCommand(new CommandTeleport());
        addCommand(new CommandTestVote());
        addCommand(new CommandUnmute());
        addCommand(new CommandVanish());
        addCommand(new CommandVoteParty());
        addCommand(new CommandJS());
        addCommand(new CommandSee());
        addCommand(new CommandReloadData());

        // Register trigger commands
        addCommand(new CommandTPATrigger());
        addCommand(new CommandTriggerAccept());
        addCommand(new CommandTriggerDecline());

        // Register discord commands
        addCommand(new CommandDiscordVerify());
        addCommand(new CommandServerVote());

        // Sort commands alphabetically:
        getCommands().sort(Comparator.comparing(Command::getName));
    }

    /**
     * Register a command.
     * @param command
     */
    public static void addCommand(Command command) {
        getCommands().add(command);
    }

    @Override
    public void onEnable() {
        registerCommands();
    }

    /**
     * Gets a list of commands by their specified type.
     * @param type
     * @return commands
     */
    public static List<Command> getCommands(CommandType type) {
        return getCommands().stream().filter(c -> c.getType() == type || type == null).collect(Collectors.toList());
    }

    /**
     * Get a list of commands the sender is allowed to use.
     * @param sender
     * @return
     */
    public static List<Command> getUsable(CommandSender sender) {
        return getCommands().stream().filter(c -> c.canUse(sender, false)).collect(Collectors.toList());
    }

    /**
     * Gets a command by its alias.
     * @param alias
     * @return cmd
     */
    public static Command getCommand(CommandType type,  String alias) {
        return getCommands(type).stream().filter(c -> c.getAlias().contains(alias.toLowerCase()))
                .findAny().orElse(null);
    }

    /**
     * Get a command label from a chat input line.
     * @param input
     * @return label
     */
    private static String getLabel(CommandType type,  String input) {
        return input.substring(type.getPrefix().length()).split(" ")[0];
    }

    /**
     * Tries to run a command. Returns true if the command was found / executed.
     * @param sender
     * @param type
     * @return commandSuccess
     */
    public static boolean handleCommand(CommandSender sender, CommandType type, String input) {
        if (!input.startsWith(type.getPrefix()))
            return false; // Not this command type.

        input = input.substring(type.getPrefix().length()); // Remove the prefix.
        input = Chat.filterMessage(input); // Apply filter.
        List<String> split = new ArrayList<>(Arrays.asList(input.split(" "))); // remove() won't work with just asList
        String cmd = split.get(0);
        Command command = getCommand(type, cmd);
        if (command == null)
            return false; // Not a command.

        if (sender instanceof DiscordSender) // Log all discord sent commands.
            Core.alertStaff(ChatColor.GREEN + sender.getName() + ": " + ChatColor.GRAY + type.getPrefix() + input);

        split.remove(0); // Remove the command from args.
        String[] args = split.toArray(new String[split.size()]);

        // Don't run this async. Can happen with chat commands.
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> runCommand(command, sender, cmd, args));
        return true;
    }

    private static void runCommand(Command cmd, CommandSender sender, String label, String[] args) {
        try {
            cmd.handle(sender, label, args); // Handle command logic.
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Error executing " + cmd.getName() + " as '" + sender.getName() + "'");
            sender.sendMessage(ChatColor.RED + "There was an error while running this command.");
        }
    }

    @EventHandler // Handles populating the command list for all commands.
    public void onTabComplete(TabCompleteEvent evt) {
        boolean console = evt.getSender() instanceof ConsoleCommandSender;
        for (Command c : getUsable(evt.getSender()))
            c.getAlias().stream().map(a -> (console ? "" : c.getCommandPrefix()) + a).filter(l -> l.startsWith(evt.getBuffer()))
                    .filter(l -> Utils.getCount(l, " ") == Utils.getCount(evt.getBuffer(), " "))
                    .map(l -> l.substring(l.lastIndexOf(" ") + 1)).forEach(evt.getCompletions()::add);
    }

    @EventHandler // Handles command-specific tab-completes.
    public void onArgsComplete(TabCompleteEvent evt) {
        String input = evt.getBuffer();
        String label = input.split(" ")[0];
        boolean console = evt.getSender() instanceof ConsoleCommandSender;

        Command cmd = getUsable(evt.getSender()).stream()
                .filter(c -> !c.getCommandPrefix().contains(" ")) // Don't count /trigger
                .filter(c -> c.getAlias().contains(console ? label : label.substring(c.getCommandPrefix().length())))
                .findAny().orElse(null); // Get the command for the input supplied.

        if (!input.contains(" ") || cmd == null)
            return; // No command was found.

        String[] args = Utils.shift(input.split(" "));
        String lastArg = (args.length > 0 ? args[args.length - 1] : "").toLowerCase();
        boolean space = input.endsWith(" ");

        List<String> possible = cmd.getCompletions(evt.getSender(), args, args.length + (space ? 1 : 0) - 1);
        evt.setCompletions(possible.stream().filter(ac -> ac.toLowerCase().startsWith(lastArg) || space).collect(Collectors.toList()));
    }

    // Remove duplicate entries.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSendTabCompletes(TabCompleteEvent evt) {
        Utils.removeDuplicates(evt.getCompletions()); // Remove duplicate entries, if any.
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent evt) {
        Player p = evt.getPlayer();
        String input = evt.getMessage();

        if (handleCommand(p, CommandType.SLASH, input) || handleCommand(p, CommandType.TRIGGER, input))
            evt.setCancelled(true); // Don't show 'unknown command....'

        if (input.startsWith("/ ")) {
            sendStaffChat(p, input.substring(2));
            evt.setCancelled(true);
            return;
        }

        if (!input.startsWith("/trigger ")) // Alert staff of commands used, if the command isn't /trigger.
            Core.alertStaff(p.getName() + ": " + ChatColor.GRAY + input);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerCommand(ServerCommandEvent evt) {
        if (handleCommand(evt.getSender(), CommandType.SLASH, CommandType.SLASH.getPrefix() + evt.getCommand()))
            evt.setCancelled(true); // Handle console commands.

        if (evt.getCommand().startsWith("/ ")) {
            sendStaffChat(evt.getSender(), evt.getCommand().substring(2));
            evt.setCancelled(true);
        }
    }

    /**
     * Send a message in staff-chat.
     * @param sender
     * @param message
     */
    private static void sendStaffChat(CommandSender sender, String message) {
        Core.alertStaff("[AC] " + sender.getName() + ": " + ChatColor.GREEN + Chat.applyAllFilters(sender, message));
    }

    //TODO: Command block commands.
}
