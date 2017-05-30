package net.kineticraft.lostcity.commands;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Command - A KC command base.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public abstract class Command {

    private CommandType type;
    private String usage;
    private String help;
    private List<String> alias;

    public Command(CommandType type, String usage, String help, String... alias) {
        this.type = type;
        this.usage = usage;
        this.help = help;
        this.alias = Arrays.asList(alias);
    }

    /**
     * Gets the command usage.
     * @return
     */
    public String getUsage() {
        return getUsage(getName());
    }

    /**
     * Gets the command usage with a specified alias.
     * @return
     */
    public String getUsage(String alias) {
        return ChatColor.RED + "Usage: " + getType().getPrefix() + alias + " " + this.usage;
    }

    /**
     * Get the minimum amount of required arguments for this command.
     * @return
     */
    public int getMinArgs() {
        return (int) Arrays.stream(this.usage.split(" ")).filter(s -> s.startsWith("<") && s.endsWith(">")).count();
    }

    /**
     * Returns the command display name.
     */
    public String getName() {
        return getAlias().get(0);
    }

    /**
     * Send the proper usage message for the given label to the command sender.
     * @param sender
     * @param label
     */
    protected void showUsage(CommandSender sender, String label) {
        sender.sendMessage(getUsage(label));
    }

    /**
     * Handles this command logic.
     * @param sender
     * @param label
     * @param args
     */
    public void handle(CommandSender sender, String label, String[] args) {
        if (args.length < getMinArgs()) {
            showUsage(sender, label);
            return;
        }

        onCommand(sender, args);
    }

    /**
     * The code specific to each command.
     * @param sender
     * @param args
     */
    protected abstract void onCommand(CommandSender sender,  String[] args);
}
