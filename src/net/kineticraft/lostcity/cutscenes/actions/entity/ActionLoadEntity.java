package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * Load an existing entity into the cutscene bank.
 * This entity will not be removed upon the end of the cutscene.
 * Created by Kneesnap on 8/2/2017.
 */
@ActionData(Material.BUCKET)
public class ActionLoadEntity extends ActionEntity {
    @Override
    public void execute() {
        Entity ent = Utils.getNearbyEntities(getCamera().getLocation(), 100).stream()
                .filter(e -> Utils.containsIgnoreCase(e.getName(), getEntityName())).findAny().orElse(null);
        MetadataManager.setMetadata(ent, Metadata.CUTSCENE_KEEP, true);
        getEvent().getStatus().getEntityMap().put(getEntityName(), ent);
    }
}
