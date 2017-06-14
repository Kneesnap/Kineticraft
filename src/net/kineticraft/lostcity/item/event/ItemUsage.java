package net.kineticraft.lostcity.item.event;

/**
 * List of different possible event triggers.
 *
 * Created by Kneesnap on 6/12/2017.
 */
public enum ItemUsage {
    LEFT_CLICK_AIR,
    LEFT_CLICK_BLOCK,
    RIGHT_CLICK_AIR,
    RIGHT_CLICK_BLOCK,

    LEFT_CLICK_ENTITY,
    RIGHT_CLICK_ENTITY,

    LEFT_CLICK(LEFT_CLICK_AIR, LEFT_CLICK_BLOCK, LEFT_CLICK_ENTITY),
    RIGHT_CLICK(RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_ENTITY),

    ENTITY_CLICK(LEFT_CLICK_ENTITY, RIGHT_CLICK_ENTITY);

    private final ItemUsage[] usages;

    ItemUsage(ItemUsage... usage) {
        this.usages = usage;
    }

    public ItemUsage[] getUsages() {
        return usages.length > 0 ? usages : new ItemUsage[] {this};
    }
}
