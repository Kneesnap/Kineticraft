package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Give the viewers a potion effect.
 * Created by Kneesnap on 8/1/2017.
 */
@ActionData(Material.POTION)
public class ActionPotionEffect extends CutsceneAction {
    private PotionEffectType type;
    private int level;
    private int duration;

    @Override
    public void execute() {
        getPlayers().forEach(p -> p.addPotionEffect(new PotionEffect(type, duration, level)));
    }
}
