package net.kineticraft.lostcity.guis.guis.donor;

import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.guis.GUIType;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Lets donors pick their particles.
 * TODO: Each particle should look different in the GUI.
 *
 * Created by Kneesnap on 6/11/2017.
 */
public class GUIParticles extends GUI {
    public GUIParticles(Player player) {
        super(player, "Particle Selector", fitSize(getParticles().length + 2));
    }

    @Override
    public void addItems() {
        Particle effect = getWrapper().getEffect();

        for (Particle p : getParticles()) {
            addItem(Material.REDSTONE, ChatColor.GREEN + Utils.capitalize(p.name()),
                    "Click here to activate this particle effect.")
                    .anyClick(e -> {
                        getPlayer().sendMessage(ChatColor.GREEN + "Activated " + Utils.capitalize(p.name()) + ".");
                        getWrapper().setEffect(p);
                        reconstruct();
                    }).setGlowing(p == effect);
        }

        toRight(2);

        if (effect != null)
            addItem(Material.INK_SACK, ChatColor.RED + "Disable Particles", "Click here to disable your active effects.")
                    .anyClick(e -> {
                        getWrapper().setEffect(null);
                        getPlayer().sendMessage(ChatColor.RED + "Particles disabled.");
                        reconstruct();
                    });

        addBackButton();
    }

    /**
     * Gets an array of all allowed particles.
     * @return particles
     */
    private static Particle[] getParticles() {
        return Particle.values();
    }
}
