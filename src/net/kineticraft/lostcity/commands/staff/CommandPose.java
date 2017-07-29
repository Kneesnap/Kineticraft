package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.mechanics.ArmorStands;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

/**
 * Allow saving of an armor stand pose.
 * Created by Kneesnap on 7/28/2017.
 */
public class CommandPose extends StaffCommand {

    public CommandPose() {
        super("<pose>", "Save/Load an ArmorStand pose", "pose");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        boolean load = ArmorStands.getPoses().containsKey(args[0]);
        sender.sendMessage(ChatColor.GREEN + "Please right-click the ArmorStand you'd like to " + (load ? "load" : "save") + ".");
        Callbacks.selectEntity((Player) sender, e -> {
            if (!(e instanceof ArmorStand)) {
                sender.sendMessage(ChatColor.RED + "That is not an ArmorStand.");
                return;
            }

            if (load) {
                ArmorStands.assumePose((ArmorStand) e, args[0]);
            } else {
                ArmorStands.getPoses().put(args[0], new ArmorStands.ArmorPose((ArmorStand) e));
            }
            sender.sendMessage(ChatColor.GREEN + "Pose " + (load ? "load" : "sav") + "ed.");
        });
    }
}
