package net.kineticraft.lostcity.config.configs;

import net.kineticraft.lostcity.config.Config;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.JsonData;

/**
 * Holds on to punishment data.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class PunishmentConfig extends JsonConfig {

    @Override
    public void load(JsonData data) {

    }

    @Override
    public JsonData save() {
        return new JsonData();
    }
}
