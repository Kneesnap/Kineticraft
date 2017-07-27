package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * CommandBright - Toggle Fullbright.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandBright extends StaffCommand {
    public CommandBright() {
        super("", "Toggle fullbright", "bright");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(Utils.formatToggle("Fullbright",
                Utils.togglePotion((Player) sender, PotionEffectType.NIGHT_VISION)));
    }
}
