package net.kineticraft.lostcity.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

/**
 * BlockCommand - Designed for use by command blocks.
 * Created by Kneesnap on 5/29/2017.
 */
public abstract class BlockCommand extends Command {

    public BlockCommand(String usage, String help, String... alias) {
        super(CommandType.COMMAND_BLOCK, usage, help, alias);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        onCommand((BlockCommandSender) sender, args);
    }

    protected abstract void onCommand(BlockCommandSender sender, String[] args);

    @Override
    public boolean canUse(CommandSender sender, boolean showMessage) {
        boolean passBlock = sender instanceof BlockCommandSender;
        if (!passBlock && showMessage)
            sender.sendMessage(ChatColor.RED + "This command can only be in a command block.");
        return passBlock;
    }
}
