package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.DungeonBoss;
import net.kineticraft.lostcity.entity.attacks.CustomAttack;
import net.kineticraft.lostcity.entity.attacks.MeleeAttack;
import net.kineticraft.lostcity.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Frilda - The fire witch zombie boss
 * TODO: Maybe have the attack system be based on chance.
 * Created by Kneesnap on 8/2/2017.
 */
public class Frilda extends DungeonBoss {
    public Frilda(Dungeon d) {
        super(new Location(d.getWorld(), -5, 11, 56), EntityType.ZOMBIE, "Frilda", true);
        setGear(Material.STONE_SWORD, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
        addStage(new MeleeAttack(5, 1)); // TODO: Fireball.
        addStage(.3, () -> getDungeon().playCutscene("powerup"), new MeleeAttack(7, 1.5), new AttackFlameWheel());
    }

    private class AttackFlameWheel extends CustomAttack {
        public AttackFlameWheel() {
            super(12, 3.5, 80);
        }

        @Override
        public void attack(LivingEntity target) {
            super.attack(target);
            target.setFireTicks(35);
        }

        @Override
        public void showDisplay(List<LivingEntity> targets) {
            ParticleUtils.makeCircle(Particle.FLAME, getSource().getLocation(), getRadius());
        }
    }
}
