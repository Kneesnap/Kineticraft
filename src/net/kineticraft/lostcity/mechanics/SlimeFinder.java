package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * SlimeFinder
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class SlimeFinder extends Mechanic {

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), this::bounceSlimes, 0L, 100L);
    }

    /**
     * Make all loaded slime balls bounce if they're in a slime chunk.
     */
    private void bounceSlimes() {
        Core.getMainWorld().getEntities().stream().filter(entity -> entity instanceof Item)
                .map(e -> (Item)e).filter(i -> isSlimeChunk(i.getLocation().getChunk())).filter(Entity::isValid)
                .filter(i -> i.getItemStack().getType() == Material.SLIME_BALL).forEach(slime -> {
                    slime.getWorld().playSound(slime.getLocation(), Sound.ENTITY_SLIME_JUMP, .5F, 1F);
                    slime.setVelocity(new Vector(0, .33F, 0));
                });
    }

    /**
     * Is the supplied chunk a slime chunk? Since there is no bukkit API for this, we have to use the formula ourselves.
     * @param chunk
     * @return Is the specified chunk a slime chunk?
     */
    private static boolean isSlimeChunk(Chunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();
        return new Random(chunk.getWorld().getSeed() +
                x * x * 4987142 +
                x * 5947611 +
                z * z * 4392871L +
                z * 389711 ^ 0x3AD8025F).nextInt(10) == 0;
    }
}
