package net.kineticraft.lostcity.commands;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.misc.*;
import net.kineticraft.lostcity.commands.player.*;
import net.kineticraft.lostcity.commands.staff.*;
import net.kineticraft.lostcity.commands.trigger.*;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.Configs.ConfigType;
import net.kineticraft.lostcity.discord.DiscordSender;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.guis.CommandGUIs;
import net.kineticraft.lostcity.guis.GUIType;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.mechanics.Chat;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles command usage.
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
        addCommand(new CommandEmote());
        addCommand(new CommandExtinguish());
        addCommand(new CommandHelp());
        addCommand(new CommandHat());
        addCommand(new CommandHome());
        addCommand(new CommandIgnore());
        addCommand(new CommandIgnoreList());
        addCommand(new CommandKittyCannon());
        addCommand(new CommandList());
        addCommand(new CommandMessage());
        addCommand(new CommandMail());
        addCommand(new CommandMailbox());
        addCommand(new CommandNick());
        addCommand(new CommandPTime());
        addCommand(new CommandPWeather());
        addCommand(new CommandReply());
        addCommand(new CommandRanks());
        addCommand(new CommandRankup());
        addCommand(new CommandReport());
        addCommand(new CommandRTP());
        addCommand(new CommandSeen());
        addCommand(new CommandSetHome());
        addCommand(new CommandShovel());
        addCommand(new CommandSkull());
        addCommand(new CommandSpawn());
        addCommand(new CommandStats());
        addCommand(new CommandUnignore());
        addCommand(new CommandTPA());
        addCommand(new CommandTPBook());
        addCommand(new CommandVotes());
        addCommand(new CommandRealName());
        addCommand(new CommandWhyLag());
        addCommand(new CommandVerify());

        // Register staff commands
        addCommand(new CommandBack());
        addCommand(new CommandBackup());
        addCommand(new CommandBright());
        addCommand(new CommandBroadcast());
        addCommand(new CommandDeathTeleport());
        addCommand(new CommandEdit());
        addCommand(new CommandEntityCount());
        addCommand(new CommandFly());
        addCommand(new CommandGUIs());
        addCommand(new CommandIPSearch());
        addCommand(new CommandKick());
        addCommand(new CommandMined());
        addCommand(new CommandMute());
        addCommand(new CommandNBS());
        addCommand(new CommandNear());
        addCommand(new CommandNotes());
        addCommand(new CommandPose());
        addCommand(new CommandPunish());
        addCommand(new CommandPurchase());
        addCommand(new CommandReboot());
        addCommand(new CommandRescue());
        addCommand(new CommandSay());
        addCommand(new CommandSetRank());
        addCommand(new CommandSpectator());
        addCommand(new CommandTeleport());
        addCommand(new CommandToLocation());
        addCommand(new CommandTestVote());
        addCommand(new CommandUnmute());
        addCommand(new CommandVanish());
        addCommand(new CommandVoteParty());
        addCommand(new CommandJS());
        addCommand(new CommandReloadData());
        addCommand(new CommandZen());
        addCommand(new CommandCheck());

        // Register trigger commands
        addCommand(new CommandTPATrigger());
        addCommand(new CommandTPAHereTrigger());
        addCommand(new CommandTriggerAccept());
        addCommand(new CommandTriggerDecline());

        Bukkit.getPluginManager().callEvent(new CommandRegisterEvent()); // Broadcast its time to register commands.
        getCommands().sort(Comparator.comparing(Command::getName)); // Sort commands alphabetically
        getCommands().stream().filter(Command::registerBukkit).forEach(BukkitCommandWrapper::new); // For commands that need bukkit registering.
    }

    /**
     * Register a command, if it's allowed on this build.
     * @param command
     */
    public static void addCommand(Command command) {
        if (Core.isApplicableBuild(command))
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
        String[] args = input.split(" ");
        String cmd = args[0];
        Command command = getCommand(type, cmd);
        if (command == null)
            return false; // Not a command.

        if (sender instanceof DiscordSender) // Log all discord sent commands.
            Core.alertStaff(ChatColor.GREEN + sender.getName() + ": " + ChatColor.GRAY + type.getPrefix() + input);

        if (Bukkit.isPrimaryThread()) {
            runCommand(command, sender, cmd, Utils.shift(args));
        } else {
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> runCommand(command, sender, cmd, Utils.shift(args)));
        }
        return true;
    }

    private static void runCommand(Command cmd, CommandSender sender, String label, String[] args) {
        try {
            cmd.handle(sender, label, args); // Handle command logic.
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Error executing " + cmd.getName() + " as '" + sender.getName() + "'");
            sender.sendMessage(ChatColor.RED + "There was an internal error while running this command.");
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
                .filter(c -> !c.getCommandPrefix().contains(" ") && label.length() >= c.getCommandPrefix().length()) // Don't count /trigger
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent evt) {
        Player p = evt.getPlayer();
        String input = evt.getMessage();

        if (input.startsWith("/minecraft:") && !Utils.isStaff(p))
            evt.setCancelled(true); // Prevent /minecraft: prefixed commands.

        if (input.startsWith("/ ")) {
            sendStaffChat(p, input.substring(2));
            evt.setCancelled(true);
            return;
        }

        if (!input.startsWith("/trigger ")) // Alert staff of commands used, if the command isn't /trigger.
            Core.alertStaff(p.getName() + ": " + ChatColor.GRAY + input);

        evt.setCancelled(handleCommand(p, CommandType.SLASH, input) || handleCommand(p, CommandType.TRIGGER, input)); // Don't show 'unknown command....'
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent evt) {
        evt.setCancelled(handleCommand(evt.getSender(), CommandType.SLASH, CommandType.SLASH.getPrefix() + evt.getCommand()) // Handle console commands.
                || handleCommand(evt.getSender(), CommandType.COMMAND_BLOCK, evt.getCommand())); // Command Block commands.

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
}
