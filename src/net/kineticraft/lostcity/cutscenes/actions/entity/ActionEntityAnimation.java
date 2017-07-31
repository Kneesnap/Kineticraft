package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import org.bukkit.EntityEffect;
import org.bukkit.Material;

/**
 * Execute an entity animation.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.TOTEM)
public class ActionEntityAnimation extends ActionEntity {
    private EntityEffect effect = EntityEffect.HURT;

    @Override
    public void execute() {
        getEntity().playEffect(effect);
    }

    @Override
    public String toString() {
        return effect + super.toString();
    }
}
