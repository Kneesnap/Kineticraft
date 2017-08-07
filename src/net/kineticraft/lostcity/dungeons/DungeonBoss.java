package net.kineticraft.lostcity.dungeons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.data.lists.QueueList;
import net.kineticraft.lostcity.entity.CustomEntity;
import net.kineticraft.lostcity.entity.attacks.CustomAttack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * A base for a DungeonBoss.
 * Created by Kneesnap on 7/11/2017.
 */
@Getter
public class DungeonBoss extends CustomEntity {
    private boolean finalBoss;
    private QueueList<BossStage> stages = new QueueList<>();

    public DungeonBoss(Location loc, EntityType type, String name, boolean finalBoss) {
        super(loc, type, name);
        this.finalBoss = finalBoss;
        getBukkit().setCustomName(ChatColor.RED + name);
        getBukkit().setCustomNameVisible(true);
    }

    @Override
    public void onDeath() {
        getDungeon().playCutscene("complete");
    }

    @Override
    public void onDamage() {
        BossStage stage = getStages().getValueSafe(1);
        LivingEntity le = getLiving();
        if (stage == null || stage.getHealthPercent() * le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() >= le.getHealth())
            return; // If there isn't a stage queued after this.
        getStages().pop(); // Remove the current stage.
        updateStage(); // Update stage.
    }

    protected void updateStage() {
        BossStage newStage = getStages().peek();
        setAttacks(newStage.getAttacks());
        if (newStage.getOnActivate() != null)
            newStage.getOnActivate().run();
    }

    /**
     * Get the dungeon this boss belongs to.
     * @return dungeon.
     */
    public Dungeon getDungeon() {
        return Dungeons.getDungeon(getBukkit());
    }

    /**
     * Add the default attack stage.
     * @param attacks
     */
    protected void addStage(CustomAttack... attacks) {
        addStage(1, null, attacks);
        updateStage();
    }

    /**
     * Add an attack stage.
     * @param healthPercent - The health percentage
     * @param attacks
     */
    protected void addStage(double healthPercent, Runnable onChance, CustomAttack... attacks) {
        getStages().add(new BossStage(healthPercent, attacks, onChance));
    }

    @AllArgsConstructor @Getter
    private class BossStage {
        private double healthPercent;
        private CustomAttack[] attacks;
        private Runnable onActivate;
    }
}
