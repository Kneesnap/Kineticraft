package net.kineticraft.lostcity.guis.staff;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Pick from a list of all items.
 * Created by Kneesnap on 6/9/2017.
 */
public class GUIMaterialPicker extends GUIItemPicker {

    private static final List<Material> IGNORE = Arrays.asList(Material.AIR, Material.WATER, Material.STATIONARY_WATER,
            Material.LAVA, Material.STATIONARY_LAVA, Material.BED_BLOCK, Material.PISTON_MOVING_PIECE, Material.PISTON_BASE,
            Material.PISTON_STICKY_BASE, Material.DOUBLE_STEP, Material.FIRE, null);

    public GUIMaterialPicker(Player player, Consumer<Material> onPick) {
        super(player, Arrays.stream(Material.values()).filter(m -> !IGNORE.contains(m))
                .map(ItemStack::new).collect(Collectors.toList()), item -> onPick.accept(item.getType()));
    }
}