package net.kineticraft.lostcity.guis.donor;

import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lets donors pick their particles.
 * Created by Kneesnap on 6/11/2017.
 */
public class GUIParticles extends GUI {
    private static final List<Particle> BANNED = Arrays.asList(Particle.MOB_APPEARANCE, Particle.EXPLOSION_HUGE, Particle.EXPLOSION_LARGE);

    public GUIParticles(Player player) {
        super(player, "Particle Selector");
    }

    @Override
    public void addItems() {
        Particle effect = getWrapper().getEffect();
        for (Particle p : getParticles())
            if (p != Particle.MOB_APPEARANCE)
                addItem(Material.REDSTONE, ChatColor.GREEN + Utils.capitalize(p.name()),
                        "Click here to activate this particle effect.")
                        .anyClick(e -> {
                            getPlayer().sendMessage(ChatColor.GREEN + "Activated " + Utils.capitalize(p.name()) + ".");
                            getWrapper().setEffect(p);
                            reconstruct();
                        }).setGlowing(p == effect);

        toRight(2);

        if (effect != null)
            addItem(Material.INK_SACK, ChatColor.RED + "Disable Particles", "Click here to disable your active effect.")
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
    private static List<Particle> getParticles() {
        return Stream.of(Particle.values()).filter(p -> !BANNED.contains(p)).collect(Collectors.toList());
    }
}
