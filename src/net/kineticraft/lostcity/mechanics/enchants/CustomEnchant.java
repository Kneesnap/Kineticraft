package net.kineticraft.lostcity.mechanics.enchants;

import lombok.Getter;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.enchantments.Enchantment;

/**
 * List of all custom enchants.
 *
 * Created by Kneesnap on 6/11/2017.
 */
@Getter
public enum CustomEnchant {

    GLOWING(GlowEnchant.class);

    private final Enchantment enchant;

    CustomEnchant(Class<? extends Enchantment> enchantClass) {
        this.enchant = ReflectionUtil.construct(enchantClass);
    }
}
