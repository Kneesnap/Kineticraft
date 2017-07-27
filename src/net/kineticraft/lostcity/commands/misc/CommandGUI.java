package net.kineticraft.lostcity.commands.misc;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.guis.GUIType;
import net.kineticraft.lostcity.guis.GUIManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Opens a GUI.
 * Created by Kneesnap on 6/8/2017.
 */
public class CommandGUI extends PlayerCommand {

    private GUIType type;

    public CommandGUI(GUIType type,  String help, String... alias) {
        this(EnumRank.MU, type, help, alias);
    }

    public CommandGUI(EnumRank rank, GUIType guiType, String help, String... alias) {
        super(rank, "", help, alias);
        this.type = guiType;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        GUIManager.openGUI((Player) sender, this.type);
    }
}
