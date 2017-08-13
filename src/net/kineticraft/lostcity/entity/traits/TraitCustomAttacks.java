package net.kineticraft.lostcity.entity.traits;

import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import net.kineticraft.lostcity.entity.CustomEntity;
import net.kineticraft.lostcity.entity.attacks.CustomAttack;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.entity.LivingEntity;

/**
 * Handles custom attacks.
 * Created by Kneesnap on 8/2/2017.
 */
@Getter
public class TraitCustomAttacks extends Trait {
    private CustomEntity entity;
    private int currentAttack;
    private int waitTime;

    public TraitCustomAttacks(CustomEntity ce) {
        super("CustomAttack");
        this.entity = ce;
    }

    @Override
    public void run() {
        if (!getEntity().getLiving().hasAI())
            return; // Pause AI.

        waitTime--;
        if (waitTime <= 0)
            performAttack();
    }

    /**
     * Called when the attacks change.
     */
    public void resetAttacks() {
        waitTime = 0;
        currentAttack = 0;
    }

    private void performAttack() {
        LivingEntity le = Utils.getNearestSurvivalPlayer(getEntity().getBukkit(), 50);
        getNPC().getNavigator().setTarget(le, true); // Force target a player.

        if (entity.getAttacks().isEmpty())
            return; // If there are no attacks, don't try to exec

        getAttack().handleAttack(getEntity().getBukkit());
        currentAttack++;
        if (currentAttack >= getEntity().getAttacks().size())
            currentAttack = 0;
        waitTime = getAttack().getDelay();
    }

    private CustomAttack getAttack() {
        return getEntity().getAttacks().get(currentAttack);
    }
}