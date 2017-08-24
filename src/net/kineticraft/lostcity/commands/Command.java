package net.kineticraft.lostcity.commands;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Command - A command base.
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public abstract class Command {

    private CommandType type;
    private String rawUsage;
    private String help;
    private List<String> alias;
    private Map<Integer, Function<CommandSender, Iterable<String>>> autoCompletes = new HashMap<>();

    @Setter private String lastAlias; // A hacky method to allow us to throw the player the usage with the alias they used.

    private static final List<String> SENDERS = Arrays.asList("CommandSender", "Player", "ConsoleCommandSender",
            "DiscordSender", "TerminalConsoleCommandSender");

    public Command(CommandType type, String usage, String help, String... alias) {
        this.type = type;
        this.rawUsage = usage;
        this.help = help;
        this.alias = Arrays.asList(alias);
    }

    /**
     * Gets the command usage.
     * @return usage
     */
    public String getUsage() {
        return "Usage: " + getCommandPrefix() + getLastAlias() + " " + getRawUsage();
    }

    /**
     * Get the minimum amount of required arguments for this command.
     * @return minArgs
     */
    public int getMinArgs() {
        return (int) Arrays.stream(getRawUsage().split(" ")).filter(s -> s.startsWith("<") && s.endsWith(">")).count();
    }

    /**
     * Returns the command display name.
     */
    public String getName() {
        return getAlias().get(0);
    }

    /**
     * Returns the string prefix that preceeds this command.
     * @return prefix
     */
    public String getCommandPrefix() {
        return getType().getPrefix();
    }

    /**
     * Show the failed usage message to the player
     * @param sender
     */
    protected void showUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + getUsage());
    }

    /**
     * Returns the alias that should be shown to the player.
     * @return lastAlias
     */
    protected String getLastAlias() {
        return lastAlias != null ? lastAlias : getName();
    }

    /**
     * Handle command-related logic listening for potential errors.
     * @param runnable
     */
    protected void execute(CommandSender sender,  String[] args, Runnable runnable) {
        try {
            runnable.run();
        } catch (NumberFormatException nfe) {
            // Couldn't get a number from input, such as from Integer.parseInt
            sender.sendMessage(ChatColor.RED + "Invalid number '" + Utils.getInput(nfe) + "'.");
        } catch (IllegalArgumentException iae) {
            // Couldn't find an enum value, comes from methods such as ChatColor.valueOf
            Matcher mClassPath = Pattern.compile("No enum constant (.+)").matcher(iae.getLocalizedMessage());

            if (!mClassPath.find())
                throw iae; // If we can't find it, print the message.

            String classPath = mClassPath.group(1);
            String[] split = classPath.split("\\.");

            String input = split[split.length - 1];
            for (String s : args)
                if (s.equalsIgnoreCase(input))
                    input = s; //Many enums use toUpperCase(), we'd rather display the input the user put in.

            sender.sendMessage(ChatColor.RED + input + " is not a valid " + split[split.length - 2] + ".");
        } catch (ClassCastException cce) {
            // This is not an eligible command sender.
            Matcher mCast = Pattern.compile("(.+) cannot be cast to (.+)").matcher(cce.getLocalizedMessage());
            if (!mCast.find())
                throw cce;

            String castFrom = mCast.group(1).split(";")[0];
            String castTo = mCast.group(2).split(";")[0];
            castTo = castTo.substring(castTo.lastIndexOf(".") + 1); // Remove the path.

            if (SENDERS.stream().anyMatch(castFrom::endsWith)) { // Only handle if the class casted was the command executor.
                sender.sendMessage(ChatColor.RED + "You must be a " + castTo.toLowerCase() + " to run this command.");
            } else {
                throw cce;
            }
        }
    }

    /**
     * Handles this command logic.
     * @param sender
     * @param label
     * @param args
     */
    public void handle(CommandSender sender, String label, String[] args) {
        setLastAlias(label); // So this will show the alias last used in any usage messages to the player.

        if (!canUse(sender, true))
            return;

        if (args.length < getMinArgs()) {
            execute(sender, args, () -> showUsage(sender));
            return;
        }

        execute(sender, args, () -> onCommand(sender, args));
    }

    /**
     * Get tab autocompletes specific to this command from input.
     * @param sender
     * @param args
     * @return completions
     */
    public List<String> getCompletions(CommandSender sender, String[] args, int argCheck) {
        List<String> completions = new ArrayList<>();

        execute(sender, args, () -> {
            if (getAutoCompletes().containsKey(argCheck)) {
                completions.addAll(Lists.newArrayList(getAutoCompletes().get(argCheck).apply(sender)));
            } else {
                String[] rawArgs = getRawUsage().split(" ");
                if (rawArgs.length > argCheck) {
                    String options = rawArgs[argCheck];
                    if (options.contains("|"))
                        completions.addAll(Arrays.asList(options.substring(1, options.length() - 1).split("\\|")));
                }
            }
        });

        return completions;
    }

    /**
     * Check if the given index of an argument list matches a given string.
     * @param args
     * @param arg
     * @param check
     * @return matches
     */
    protected static boolean isArg(String[] args, int arg, String check) {
        return args.length > arg && args[arg].equalsIgnoreCase(check);
    }

    /**
     * Remove the first few arguments from an array.
     * @param args
     * @param toSkip - Number of args to skip
     * @return skipped
     */
    protected static String[] skipArgs(String[] args, int toSkip) {
        return Utils.shift(args, toSkip);
    }

    /**
     * The code specific to each command.
     * @param sender
     * @param args
     */
    protected abstract void onCommand(CommandSender sender,  String[] args);

    /**
     * Can the given sender perform this command?
     * @param sender
     * @param showMessage
     * @return canUse
     */
    public abstract boolean canUse(CommandSender sender, boolean showMessage);

    /**
     * Set static values to auto-complete from an enum value array.
     * @param e
     */
    protected void autocomplete(Enum<?>[] e) {
        autocomplete(-1, e);
    }

    /**
     * Set static values to auto-complete from an enum value array.
     * @param arg
     * @param e
     */
    protected void autocomplete(int arg, Enum<?>[] e) {
        autocomplete(arg, Arrays.stream(e).map(Enum::name).collect(Collectors.toList()));
    }

    /**
     * Set static values to add to the tab-complete the next argument.
     * @param results
     */
    protected void autocomplete(String... results) {
        autocomplete(-1, results);
    }

    /**
     * Set static values to add to the tab-complete a certain argument.
     * @param arg
     * @param results
     */
    protected void autocomplete(int arg, String... results) {
        autocomplete(arg, Arrays.asList(results));
    }

    /**
     * Set static values to add to the tab-complete the next argument.
     * @param results
     */
    protected void autocomplete(Iterable<String> results) {
        autocomplete(-1, results);
    }

    /**
     * Set static values to add to the tab-complete a certain argument.
     * @param arg
     * @param results
     */
    protected void autocomplete(int arg, Iterable<String> results) {
        autocomplete(arg, p -> results);
    }

    /**
     * Set the next dynamic autocomplete result.
     * @param supplier
     */
    protected void autocomplete(Supplier<Iterable<String>> supplier) {
        autocomplete(-1, supplier);
    }

    /**
     * Dynamically get autocomplete results without parameters.
     * @param arg
     * @param supplier
     */
    protected void autocomplete(int arg, Supplier<Iterable<String>> supplier) {
        autocomplete(arg, cs -> supplier.get());
    }

    /**
     * Set the next dynamic autocomplete result.
     * @param options
     */
    protected void autocomplete(Function<CommandSender, Iterable<String>> options) {
        autocomplete(-1, options);
    }

    /**
     * Set a dynamic autocomplete result.
     * @param arg
     * @param options
     */
    protected void autocomplete(int arg, Function<CommandSender, Iterable<String>> options) {
        // Allow -1 to automatically pick the highest argument
        arg = arg >= 0 ? arg : getAutoCompletes().keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
        getAutoCompletes().put(arg, options);
    }

    /**
     * Add an auto-complete for all online players.
     */
    protected void autocompleteOnline() {
        autocompleteOnline(-1);
    }

    /**
     * Set the next auto-complete to be of all online players.
     * @param arg
     */
    protected void autocompleteOnline(int arg) {
        autocomplete(arg, p -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }

    /**
     * Returns whether or not this command should be registered into the bukkit command map, for whatever reason.
     */
    public boolean registerBukkit() {
        return false;
    }
}
