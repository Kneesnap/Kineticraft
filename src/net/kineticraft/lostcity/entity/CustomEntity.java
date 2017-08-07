package net.kineticraft.lostcity.entity;

import lombok.Getter;
import net.citizensnpcs.npc.CitizensNPC;
import net.kineticraft.lostcity.entity.attacks.CustomAttack;
import net.kineticraft.lostcity.entity.traits.TraitCustomAttacks;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.citizensnpcs.api.npc.NPC.*;

/**
 * A wrapper around Citizens NPC.
 * Created by Kneesnap on 8/2/2017.
 */
@Getter
public class CustomEntity {
    private CitizensNPC NPC;
    private TraitCustomAttacks trait = new TraitCustomAttacks(this);
    private List<CustomAttack> attacks = new ArrayList<>();
    private static final EquipmentSlot[] ORDER = new EquipmentSlot[] {EquipmentSlot.HAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public CustomEntity(Location loc, EntityType type, String name) {
        this.NPC = (CitizensNPC) Entities.spawnNPC(type, name);
        getNPC().spawn(loc);
        getNPC().data().set(SHOULD_SAVE_METADATA, false); // Don't save this NPC.
        getNPC().data().set(DEFAULT_PROTECTED_METADATA, false); // Don't be invulnerable.
        getNPC().addTrait(trait);
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

    /**
     * Called when this entity is damaged.
     */
    public void onDamage() {

    }

    /**
     * Get this entity as a bukkit LivingEntity.
     * @return livingEntity
     */
    public LivingEntity getLiving() {
        return (LivingEntity) getBukkit();
    }

    /**
     * Set a piece of equipped gear.
     * @param slot
     * @param item
     */
    public void setGear(EquipmentSlot slot, ItemStack item) {
        Utils.setItem((LivingEntity) getBukkit(), slot, item);
    }

    /**
     * Set all equipped gear.
     * Items should be in order: Hand, Helmet, Chestplate, Leggings, Boots
     * @param gear
     */
    public void setGear(ItemStack... gear) {
        for (int i = 0; i < gear.length; i++)
            setGear(ORDER[i], gear[i]);
    }

    /**
     * Set all equipped gear.
     * @param mat
     */
    public void setGear(Material... mat) {
        ItemStack[] gear = new ItemStack[mat.length];
        for (int i = 0; i < mat.length; i++)
            gear[i] = new ItemStack(mat[i]);
        setGear(gear);
    }

    /**
     * Update the attacks of this entity.
     * @param attacks
     */
    protected void setAttacks(CustomAttack... attacks) {
        getTrait().resetAttacks();
        getAttacks().clear();
        Arrays.asList(attacks).forEach(getAttacks()::add);
    }
}
