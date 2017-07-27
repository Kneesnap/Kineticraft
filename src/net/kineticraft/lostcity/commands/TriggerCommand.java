package net.kineticraft.lostcity.commands;

import net.kineticraft.lostcity.utils.TextUtils;
import net.kineticraft.lostcity.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a command that wraps around a /trigger command.
 * Created by Kneesnap on 6/14/2017.
 */
public abstract class TriggerCommand extends Command {

    public TriggerCommand(String objective) {
        super(CommandType.TRIGGER, "<set|add> <value>", null, objective);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        String mode = args[0];
        if (!mode.equals("add") && !mode.equals("set")) {
            TextUtils.sendLocalized(sender, "commands.trigger.invalidMode", mode);
            return;
        }

        int num;
        try {
            num = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            TextUtils.sendLocalized(sender, "commands.generic.num.invalid", Utils.getInput(nfe));
            return;
        }

        onCommand((Player) sender, num);
    }

    @Override
    public boolean canUse(CommandSender sender, boolean showMessage) {
        boolean isPlayer = sender instanceof Player;
        if (!isPlayer && showMessage)
            TextUtils.sendLocalized(sender, "commands.trigger.invalidPlayer");
        return isPlayer;
    }

    @Override
    protected void showUsage(CommandSender sender) {
        TranslatableComponent args = new TranslatableComponent("commands.trigger.usage");
        args.setColor(ChatColor.RED);

        TranslatableComponent usage = new TranslatableComponent("commands.generic.usage", args);
        usage.setColor(ChatColor.RED);
        sender.sendMessage(usage);
    }

    /**
     * Handles command logic.
     * @param sender
     * @param value
     */
    protected abstract void onCommand(Player sender, int value);
}
