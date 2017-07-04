package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.BuildType;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.maps.JsonStringMap;

/**
 * Main plugin config.
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter @Setter
public class MainConfig extends JsonConfig {

    private String voteURL;
    private int afkLimit;
    private JsonStringMap filter = new JsonStringMap();
    private String discordToken;
    private long serverId;
    private BuildType buildType;
    private int build;
    private int lastSize;

    @Override
    public void load(JsonData data) {
        this.voteURL = data.getString("voteURL", "http://google.com/");
        this.filter = data.getStringMap("filter");
        this.afkLimit = data.getInt("afkLimit", 30);
        this.discordToken = data.getString("token");
        this.buildType = data.getEnum("buildType", BuildType.DEV);
        this.serverId = data.getLong("serverId");
        this.build = data.getInt("build");
        this.lastSize = data.getInt("lastNotesSize");
    }

    @Override
    public JsonData save() {
        return new JsonData().setString("voteURL", voteURL).setElement("filter", filter)
                .setNum("afkLimit", afkLimit).setString("token", discordToken).setEnum("buildType", buildType)
                .setNum("serverId", serverId).setNum("lastNotesSize", lastSize).setNum("build", build);
    }
}
