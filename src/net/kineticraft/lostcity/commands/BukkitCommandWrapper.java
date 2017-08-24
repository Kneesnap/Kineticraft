package net.kineticraft.lostcity.commands;

import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

/**
 * Wraps around a custom command in a way that the built-in command system will understand.
 * Created by Kneesnap on 7/30/2017.
 */
public class BukkitCommandWrapper extends org.bukkit.command.Command implements CommandExecutor {
    private Command cmd;
    private static CommandMap map;

    protected BukkitCommandWrapper(Command cmd) {
        super(cmd.getName());
        this.cmd = cmd;
        setAliases(cmd.getAlias());
        register();
    }

    @Override // This won't get called, since we don't call it ourselves.
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        return Commands.handleCommand(sender, cmd.getType(), cmd.getCommandPrefix() + s + " " + String.join(" ", args));
    }

    public void register() {
        if (map == null)
            map = (CommandMap) ReflectionUtil.exec(Bukkit.getServer(), "getCommandMap");
        map.register("kineticraft", this);
    }
}