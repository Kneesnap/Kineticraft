package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Allows developers to execute javascript code in-game.
 * Enforces a hard-coded list of developers for maximum security.
 *
 * //TODO: Dynamically alias all classes in the default Bukkit package, or all enums.
 *
 * Created by egoscio on 6/20/17.
 */

public class CommandJS extends StaffCommand {

    private ScriptEngine engine;

    public CommandJS() {
        super(EnumRank.DEV, "[expression]", "Evaluate a JavaScript expression.", "js");
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        initJS();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (!Core.isDev(sender))
            return;

        engine.put("self", sender); // Allow the sender to use the 'self' keyword.

        try {
            sender.sendMessage(engine.eval(String.join(" ", args)).toString());
        } catch (ScriptException e) {
            sender.sendMessage(e.getMessage());
        }
    }

    /**
     * Setup javascript shortcuts such as 'server'.
     */
    private void initJS() {
        try {
            // Make the 'eval' command behave exactly as the eval used here does.
            engine.put("engine", engine);
            engine.eval("var eval = function(code) {return engine.eval(code);}");

            // Setup basic global server shortcuts.
            engine.eval("var Kineticraft = net.kineticraft.lostcity");
            engine.eval("var plugin = Kineticraft.Core.instance");
            engine.eval("var server = org.bukkit.Bukkit"); // Setup 'server' keyword
            engine.eval("var Bukkit = server"); // Setup 'Bukkit' keyword.

            // Basic IO.
            engine.eval("var console = server.logger");
            engine.eval("console.log = console.info");
            engine.eval("var print = function(msg) {server.logger.info(msg); return msg;}"); // Create print()

            engine.eval("var ChatColor = org.bukkit.ChatColor"); // Setup ChatColor keyword.
            engine.eval("var Sound = org.bukkit.Sound");

            // Setup schedulers
            engine.eval("var toTicks = function (millis) { return Math.ceil(millis / 50); }");
            engine.eval("var setTimeout = function (callback, delayMS) {"
                    + "return server.scheduler.runTaskLater(plugin, callback, toTicks(delayMS)); }");
            engine.eval("var setInterval = function (callback, intervalMS) {"
                    + "return server.scheduler.runTaskTimer(plugin, callback, toTicks(intervalMS), toTicks(intervalMS)); }");
        } catch (ScriptException ex) {
            ex.printStackTrace();
            Core.warn("Failed to initialize JS shortcuts.");
        }
    }
}
