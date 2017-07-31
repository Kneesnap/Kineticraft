package net.kineticraft.lostcity.cutscenes.commands;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.cutscenes.Cutscene;
import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.cutscenes.gui.GUICutsceneEditor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Create or edit cutscenes.
 * Created by Kneesnap on 7/28/2017.
 */
public class CommandCutsceneEditor extends StaffCommand {
    public CommandCutsceneEditor() {
        super("<cutscene>", "Edit a cutscene.", "csedit");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (!Cutscenes.getCutscenes().containsKey(args[0]))
            Cutscenes.getCutscenes().put(args[0], new Cutscene());
        new GUICutsceneEditor((Player) sender, Cutscenes.getCutscenes().get(args[0]));
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.GRAY + "Cutscenes: " + ChatColor.GREEN
                + String.join(ChatColor.GRAY + ", " + ChatColor.GREEN, Cutscenes.getCutscenes().keySet()));
    }
}
