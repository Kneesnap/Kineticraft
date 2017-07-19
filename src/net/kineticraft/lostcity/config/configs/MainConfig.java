package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.BuildType;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.lists.StringList;
import net.kineticraft.lostcity.data.maps.JsonStringMap;

/**
 * Main plugin config.
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter @Setter
public class MainConfig extends JsonConfig {
    private String voteURL = "http://google.com/";
    private int afkLimit = 30;
    private JsonStringMap filter = new JsonStringMap();
    private String discordToken;
    private long serverId;
    private BuildType buildType;
    private int build;
    private int lastNotesSize;
    private StringList swearWords = new StringList();
}
