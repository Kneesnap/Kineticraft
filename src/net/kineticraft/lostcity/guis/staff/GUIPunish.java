package net.kineticraft.lostcity.guis.staff;

import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.mechanics.Punishments;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Punish a player with style.
 * Created by Kneesnap on 6/17/2017.
 */
public class GUIPunish extends GUI {

    private KCPlayer target;

    public GUIPunish(Player player, KCPlayer punish) {
        super(player, "Punish " + punish.getUsername());
        target = punish;
    }

    @Override
    public void addItems() {

        Arrays.stream(Punishments.PunishmentType.values()).forEach(pt ->
            addItem(pt.getIcon(), ChatColor.YELLOW + Utils.capitalize(pt.name()),
                    "Click here to punish this", "player for " + ChatColor.YELLOW + pt.getDisplay() + ChatColor.GRAY + ".").anyClick(e -> {
                        target.punish(pt, getPlayer());
                        close();
            }));

        if (!target.getPunishments().isEmpty()) {
            nextRow();
            fillGlass(DyeColor.LIME);
            target.getPunishments().forEach(p ->
                addItem(p.getItem()).anyClick(e ->
                    Callbacks.promptConfirm(getPlayer(), () -> {
                        p.setValid(!p.isValid());
                        getPlayer().sendMessage(ChatColor.GRAY + "Punishment toggled.");
                        target.writeData();
                        reconstruct();
                    }, null)));
        }
    }
}
