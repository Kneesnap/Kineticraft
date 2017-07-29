package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.MechanicManager;
import net.kineticraft.lostcity.mechanics.system.ModularMechanic;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.Map;

/**
 * Handles custom armor stands and their animations.
 * Created by Kneesnap on 7/28/2017.
 */
public class ArmorStands extends ModularMechanic<ArmorStands.ArmorPose> {

    public ArmorStands() {
        super("poses", ArmorPose.class);
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStand(PlayerArmorStandManipulateEvent evt) {
        evt.setCancelled(MetadataManager.hasMetadata(evt.getRightClicked(), Metadata.NO_MODIFY));
    }

    @EventHandler(ignoreCancelled = true) // Prevent these armorstands from dropping dear
    public void onEntityDeath(EntityDeathEvent evt) {
        if (evt.getEntity() instanceof ArmorStand && MetadataManager.hasMetadata(evt.getEntity(), Metadata.NO_MODIFY))
            evt.getDrops().clear();
    }

    /**
     * Spawn an armor stand with a set pose.
     * @param loc
     * @param pose
     * @return armorStand
     */
    public static ArmorStand spawnArmorStand(Location loc, String pose) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        assumePose(as, pose);
        return as;
    }

    /**
     * Force an ArmorStand to assume a pose.
     * @param stand
     * @param pose
     */
    public static void assumePose(ArmorStand stand, String pose) {
        assumePose(stand, getPoses().get(pose));
    }

    /**
     * Force an ArmorStand to assume a pose.
     * @param stand
     * @param pose
     */
    public static void assumePose(ArmorStand stand, ArmorPose pose) {
        if (!stand.isInvulnerable()) {
            stand.setInvulnerable(true);
            stand.setBasePlate(false);
            stand.setArms(true);
            MetadataManager.setMetadata(stand, Metadata.NO_MODIFY, true);
        }

        assert pose != null;
        // Set Pose
        stand.setHeadPose(pose.getHead());
        stand.setBodyPose(pose.getBody());
        stand.setLeftArmPose(pose.getLeftArm());
        stand.setRightArmPose(pose.getRightArm());
        stand.setLeftLegPose(pose.getLeftLeg());
        stand.setRightLegPose(pose.getRightLeg());

        // Give gear.
        Map<EquipmentSlot, ItemStack> map = pose.getGear().toEnumMap(EquipmentSlot.class);
        for (EquipmentSlot slot : map.keySet())
            Utils.setItem(stand, slot, map.get(slot));
    }

    /**
     * Get the map of poses.
     * @return poses
     */
    public static JsonMap<ArmorPose> getPoses() {
        return MechanicManager.getInstance(ArmorStands.class).getMap();
    }

    @Getter @AllArgsConstructor
    public static class ArmorPose implements Jsonable {
        private EulerAngle head;
        private EulerAngle body;
        private EulerAngle leftArm;
        private EulerAngle rightArm;
        private EulerAngle leftLeg;
        private EulerAngle rightLeg;
        private JsonMap<ItemStack> gear = new JsonMap<>();

        public ArmorPose() {

        }

        public ArmorPose(ArmorStand a) {
            this(a.getHeadPose(), a.getBodyPose(), a.getLeftArmPose(), a.getRightArmPose(), a.getLeftLegPose(), a.getRightLegPose(), new JsonMap<>());
            for (EquipmentSlot slot : EquipmentSlot.values()) { // Save gear.
                ItemStack i = Utils.getItem(a, slot);
                if (!Utils.isAir(i))
                    getGear().put(slot.name(), i);
            }
        }
    }
}
