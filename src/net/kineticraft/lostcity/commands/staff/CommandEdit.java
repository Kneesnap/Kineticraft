package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.guis.staff.GUIJsonEditor;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow editting of json data.
 * Created by Kneesnap on 7/20/2017.
 */
public class CommandEdit extends StaffCommand {

    public CommandEdit() {
        super(EnumRank.MOD, "<player|config> <data>", "Edit json data.", "edit");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Jsonable j = null;

        if (args[0].equals("player")) {
            if (!Utils.isVisible(sender, args[1]))
                return;
            j = KCPlayer.getWrapper(Bukkit.getPlayer(args[1]));
        } else if (args[0].equals("config")) {
            j = (JsonConfig) Configs.getConfig(Configs.ConfigType.valueOf(args[1].toUpperCase()));
        }

        if (j == null) {
            showUsage(sender);
            return;
        }

        new GUIJsonEditor((Player) sender, j);
    }
}
