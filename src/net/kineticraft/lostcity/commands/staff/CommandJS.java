package net.kineticraft.lostcity.commands.staff;

import lombok.SneakyThrows;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.system.MechanicManager;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import net.kineticraft.lostcity.utils.Utils;
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
 * Created by Egoscio on 6/20/17.
 */
public class CommandJS extends StaffCommand {

    private ScriptEngine engine;

    private static final List<String> SELF_ALIAS = Arrays.asList("self", "sender", "player");

    public CommandJS() {
        super(EnumRank.DEV, "<expression>", "Evaluate a JavaScript expression.", "js");
        setDangerous(true);
        initJS();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
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

        // Allow JS to load java classes.
        Thread currentThread = Thread.currentThread();
        ClassLoader previousClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(Core.getInstance().getClass().getClassLoader());

        try {
            this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
            engine.put("engine", engine); // Allow JS to do consistent eval.
            engine.eval(new InputStreamReader(Core.getInstance().getResource("boot.js"))); // Run JS startup.
            MechanicManager.getMechanics().forEach(this::bindObject); // Create shortcuts for all mechanics.
            bindClass(Utils.class);
            bindClass(KCPlayer.class);
            bindClass(ReflectionUtil.class);
        } catch (ScriptException ex) {
            ex.printStackTrace();
            Core.warn("Failed to initialize JS shortcuts.");
        } finally {
            // Set back the previous class loader.
            currentThread.setContextClassLoader(previousClassLoader);
        }
    }

    /**
     * Evaluate JS.
     * @param cmd
     * @param args
     * @return result
     * @throws ScriptException
     */
    private Object eval(String cmd, Object... args) throws ScriptException{
        return engine.eval(String.format(cmd, args));
    }

    /**
     * Create a binding for a given object's class.
     * @param o
     */
    private void bindObject(Object o) {
        bindClass(o.getClass());
    }

    /**
     * Create a shortcut for the given class.
     * @param clazz
     */
    @SneakyThrows
    private void bindClass(Class<?> clazz) { // We don't use put because JS gets a special object that allows for calling of static methods.
        eval("var %s = %s", clazz.getSimpleName(), clazz.getName());
    }
}