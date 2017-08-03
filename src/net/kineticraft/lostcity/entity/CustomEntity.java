package net.kineticraft.lostcity.entity;

import lombok.Getter;
import net.citizensnpcs.npc.CitizensNPC;
import net.kineticraft.lostcity.entity.attacks.CustomAttack;
import net.kineticraft.lostcity.entity.traits.TraitCustomAttacks;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

import static net.citizensnpcs.api.npc.NPC.*;

/**
 * A wrapper around Citizens NPC.
 * Created by Kneesnap on 8/2/2017.
 */
@Getter
public class CustomEntity {
    private CitizensNPC NPC;
    private List<CustomAttack> attacks = new ArrayList<>();

    public CustomEntity(Location loc, EntityType type, String name) {
        this.NPC = (CitizensNPC) Entities.spawnNPC(type, name);
        getNPC().spawn(loc);
        getNPC().data().set(SHOULD_SAVE_METADATA, false); // Don't save this NPC.
        getNPC().addTrait(new TraitCustomAttacks(this));
    }

    /**
     * Navigate to a given location.
     * @param loc
     */
    public void navigate(Location loc) {
        getNPC().getNavigator().setTarget(loc);
    }

    /**
     * Return the bukkit entity for this NPC.
     * @return bukkit
     */
    public Entity getBukkit() {
        return getNPC().getEntity();
    }

    /**
     * Called when this entity dies.
     */
    public void onDeath() {

    }
}
