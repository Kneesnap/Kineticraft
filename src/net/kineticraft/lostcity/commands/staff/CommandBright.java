package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * CommandBright - Toggle Fullbright.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandBright extends StaffCommand {
    public CommandBright() {
        super("", "Toggle fullbright.", "bright");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        boolean newState = !player.hasPotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        if (newState)
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));

        player.sendMessage("Fullbright " + (newState ? "enabled" : "disabled"));
    }
}
