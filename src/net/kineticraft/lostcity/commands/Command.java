package net.kineticraft.lostcity.commands;

import lombok.Getter;
import lombok.Setter;
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

    @Setter  private String lastAlias; // A hacky method to allow us to thow the player the usage with the alias they used.

    public Command(CommandType type, String usage, String help, String... alias) {
        this.type = type;
        this.rawUsage = usage;
        this.help = help;
        this.alias = Arrays.asList(alias);
    }

    /**
     * Gets the command usage.
     * @return
     */
    public String getUsage() {
        return ChatColor.RED + "Usage: " + getCommandPrefix() + getLastAlias() + " " + getRawUsage();
    }

    /**
     * Get the minimum amount of required arguments for this command.
     * @return
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
     * @return
     */
    public String getCommandPrefix() {
        return getType().getPrefix();
    }

    /**
     * Show the failed usage message to the player
     * @param sender
     */
    protected void showUsage(CommandSender sender) {
        sender.sendMessage(getUsage());
    }

    /**
     * Returns the alias that should be shown to the player.
     * @return
     */
    private String getLastAlias() {
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

        if (args.length < getMinArgs()) {
            showUsage(sender);
            return;
        }

        try {
            onCommand(sender, args);
        } catch (NumberFormatException nfe) {
            // Couldn't get a number from input, such as from Integer.parseInt
            String input = nfe.getLocalizedMessage().split(": ")[1];
            sender.sendMessage(ChatColor.RED + "Invalid number " + input + ".");
        } catch (IllegalArgumentException iae) {
            //Couldn't find an enum value, comes from methods such as ChatColor.valueOf
            Matcher mClassPath = Pattern.compile("No enum constant (.+) in").matcher(iae.getLocalizedMessage());
            mClassPath.find();
            String classPath = mClassPath.group(1);
            String[] split = classPath.split("\\.");

            sender.sendMessage(ChatColor.RED + split[split.length - 1] + " is not a valid " + split[split.length - 2] + ".");
        }
    }

    /**
     * The code specific to each command.
     * @param sender
     * @param args
     */
    protected abstract void onCommand(CommandSender sender,  String[] args);
}
