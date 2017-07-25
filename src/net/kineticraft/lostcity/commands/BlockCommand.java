package net.kineticraft.lostcity.commands;

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
    public boolean canUse(CommandSender sender, boolean showMessage) {
        return sender instanceof BlockCommandSender;
    }
}
