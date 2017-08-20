package net.kineticraft.lostcity.entity.attacks;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A custom attack base.
 * Created by Kneesnap on 8/2/2017.
 */
@Getter
public abstract class CustomAttack {
    private double damage;
    private double radius;
    private int delay;
    private Entity source;

    public CustomAttack(double damage, double radius, int delay) {
        this.damage = damage;
        this.radius = radius;
        this.delay = delay;
    }

    /**
     * Execute this attack as a given entity.
     * @param attacker
     */
    public void handleAttack(Entity attacker) {
        this.source = attacker;
        List<LivingEntity> targets = attacker.getNearbyEntities(getRadius(), getRadius(), getRadius()).stream()
                .filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e).filter(this::canAttack).collect(Collectors.toList());
        showDisplay(targets);
        targets.forEach(this::attack);
    }

    /**
     * Attack the given target.
     * @param target
     */
    public void attack(LivingEntity target) {
        target.damage(getDamage(), getSource());
    }

    /**
     * Show the attack display effect.
     * @param targets
     */
    public abstract void showDisplay(List<LivingEntity> targets);

    /**
     * Is the player in range of this attack?
     * @param target
     * @return isInRange
     */
    public boolean inRange(LivingEntity target) {
        return source.getLocation().distance(target.getLocation()) <= getRadius();
    }

    /**
     * Can this entity attack another one?
     * @param target
     * @return canAttack
     */
    public boolean canAttack(LivingEntity target) {
        return inRange(target) && target instanceof Player;
    }
}
