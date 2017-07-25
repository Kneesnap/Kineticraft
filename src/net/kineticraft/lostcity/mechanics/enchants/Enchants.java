package net.kineticraft.lostcity.mechanics.enchants;

import net.kineticraft.lostcity.mechanics.system.Mechanic;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;

/**
 * Handles registering custom enchants.
 *
 * Created by Kneesnap on 6/11/2017.
 */
public class Enchants extends Mechanic {

    @Override
    public void onEnable() {
        registerEnchants();
    }

    /**
     * Register all of our custom enchants.
     */
    private static void registerEnchants() {

        // Tell bukkit it's ok to register new enchants.
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register each enchant.
        for (CustomEnchant ce : CustomEnchant.values())
            Enchantment.registerEnchantment(ce.getEnchant());
    }
}
