package net.kineticraft.lostcity.cutscenes.commands;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.cutscenes.Cutscene;
import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Start a cutscene.
 * Created by Kneesnap on 7/22/2017.
 */
public class CommandCutscene extends StaffCommand {

    public CommandCutscene() {
        super("<cutscene> [player]", "Play a cutscene", "cutscene", "play");
        autocomplete(Cutscenes.getCutscenes()::keySet);
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Cutscene cs = Cutscenes.getCutscenes().get(args[0]);

        if (cs == null) {
            sender.sendMessage(ChatColor.RED + "Cutscene not found.");
            return;
        }

        if (args.length > 1 && !Utils.isVisible(sender, args[1]))
            return;

        cs.play(args.length > 1 ? Bukkit.getPlayer(args[1]) : (Player) sender);
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.GRAY + "Cutscenes: " + ChatColor.GREEN
                + String.join(ChatColor.GRAY + ", " + ChatColor.GREEN, Cutscenes.getCutscenes().keySet()));
    }
}
