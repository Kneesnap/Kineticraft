package net.kineticraft.lostcity.commands.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.guis.data.GUIJsonEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

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
        Edittable e = Edittable.valueOf(args[0].toUpperCase());
        Jsonable j = e.getSupplier().apply(args[1]);

        if (j == null) {
            sender.sendMessage(ChatColor.RED + "Unknown target data '" + args[1] + "'.");
            return;
        }

        new GUIJsonEditor((Player) sender, j);
    }

    @AllArgsConstructor @Getter
    private enum Edittable {
        PLAYER(p -> KCPlayer.getWrapper(Bukkit.getPlayer(p))),
        CONFIG(j -> (JsonConfig) Configs.getConfig(Configs.ConfigType.valueOf(j.toUpperCase())));

        private final Function<String, Jsonable> supplier;
    }
}
