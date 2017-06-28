package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.command.CommandSender;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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

    private static final List<String> SELF_ALIAS = Arrays.asList("self", "sender", "player");

    public CommandJS() {
        super(EnumRank.DEV, "<expression>", "Evaluate a JavaScript expression.", "js");
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        initJS();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (!Core.isDev(sender))
            return;

        // Allow the sender to use the 'self' keyword.
        SELF_ALIAS.forEach(a -> engine.put(a, sender));

        try {
            Object result = engine.eval(String.join(" ", args));
            if (result != null)
                sender.sendMessage(result.toString());
        } catch (Exception e) {
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
            engine.eval(new InputStreamReader(Core.getInstance().getResource("boot.js")));
        } catch (ScriptException ex) {
            ex.printStackTrace();
            Core.warn("Failed to initialize JS shortcuts.");
        }
    }
}
