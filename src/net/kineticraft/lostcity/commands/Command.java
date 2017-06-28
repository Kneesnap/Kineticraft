package net.kineticraft.lostcity.commands;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command - A KC command base.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public abstract class Command {

    private CommandType type;
    private String rawUsage;
    private String help;
    private List<String> alias;

    @Setter private String lastAlias; // A hacky method to allow us to throw the player the usage with the alias they used.

    private static final List<String> SENDERS = Arrays.asList("CommandSender", "Player", "ConsoleCommandSender");

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
            showUsage(sender);
            return;
        }

        try {
            onCommand(sender, args);
        } catch (NumberFormatException nfe) {
            // Couldn't get a number from input, such as from Integer.parseInt
            sender.sendMessage(ChatColor.RED + "Invalid number '" + Utils.getInput(nfe) + "'.");
        } catch (IllegalArgumentException iae) {
            // Couldn't find an enum value, comes from methods such as ChatColor.valueOf
            Matcher mClassPath = Pattern.compile("No enum constant (.+)").matcher(iae.getLocalizedMessage());
            mClassPath.find();
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
}
