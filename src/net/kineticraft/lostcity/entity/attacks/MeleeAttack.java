package net.kineticraft.lostcity.entity.attacks;

import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * A basic melee attack
 * Created by Kneesnap on 8/6/2017.
 */
public class MeleeAttack extends CustomAttack {

    public MeleeAttack(double damage, double radius) {
        super(damage, radius, 5);
    }

    @Override
    public void showDisplay(List<LivingEntity> targets) {

    }
}
