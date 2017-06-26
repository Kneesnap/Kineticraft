package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by egoscio on 6/20/17.
 */

public class CommandJS extends StaffCommand {

    private ScriptEngine engine;

    public CommandJS() {
        super(EnumRank.DEV, false, "[expression]", "Run a JavaScript expression.", "js");
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (Core.isDev(sender)) {
            Object result;
            try {
                result = engine.eval(String.join(" ", args));
                sender.sendMessage(result.toString());
            } catch (ScriptException e) {
                sender.sendMessage(e.getMessage());
            }
        }
    }
}
