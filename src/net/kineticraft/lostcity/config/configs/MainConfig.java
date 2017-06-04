package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.config.Config;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.JsonData;

/**
 * Main plugin config.
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter @Setter
public class MainConfig extends JsonConfig {

    private String voteURL;

    public MainConfig() {
        super(Configs.ConfigType.MAIN);
    }

    @Override
    public void load(JsonData data) {
        setVoteURL(data.getString("voteURL", "http://google.com/"));
    }

    @Override
    public JsonData save() {
        return new JsonData().setString("voteURL", getVoteURL());
    }
}
