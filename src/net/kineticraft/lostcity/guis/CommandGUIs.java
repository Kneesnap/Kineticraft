package net.kineticraft.lostcity.guis;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Open any GUI.
 * Created by Kneesnap on 6/9/2017.
 */
public class CommandGUIs extends StaffCommand {

    public CommandGUIs() {
        super(EnumRank.ADMIN, "<gui>", "Open any menu.", "menu", "gui");
        autocomplete(GUIType.values());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        GUIManager.openGUI((Player) sender, GUIType.valueOf(args[0].toUpperCase()));
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.RED + "Menus: " + ChatColor.YELLOW + Arrays.stream(GUIType.values())
                .map(GUIType::name).collect(Collectors.joining(ChatColor.RED + ", " + ChatColor.YELLOW)));
    }
}
