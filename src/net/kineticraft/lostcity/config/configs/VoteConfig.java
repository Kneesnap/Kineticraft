package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.data.wrappers.JsonItem;
import net.kineticraft.lostcity.mechanics.Voting.*;

import java.util.UUID;

/**
 * Holds on to vote data.
 * Created by Kneesnap on 6/3/2017.
 */
@Setter @Getter
public class VoteConfig extends JsonConfig {

    private int votesPerParty;
    private int totalVotes;
    private UUID topVoter;
    private JsonList<PartyReward> party = new JsonList<>();
    private JsonList<JsonItem> normal = new JsonList<>();
    private JsonList<VoteAchievement> achievements = new JsonList<>();
    private String month; // The mount the votes are currently stored for.

    /**
     * Get the number of votes needed until the next party.
     * @return votesNeeded
     */
    public int getVotesUntilParty() {
        int toParty = getVotesPerParty() - (getTotalVotes() % getVotesPerParty());
        return toParty != getVotesPerParty() ? toParty : 0;
    }

    @Override
    public void load(JsonData data) {
        setVotesPerParty(data.getInt("votesPerParty", 50));
        setTotalVotes(data.getInt("totalVotes"));
        setTopVoter(data.getUUID("topVoter"));
        setParty(data.getList("party", JsonList.class, PartyReward.class));
        setNormal(data.getList("normal", JsonList.class, JsonItem.class));
        setAchievements(data.getList("achievements", JsonList.class, VoteAchievement.class));
        setMonth(data.getString("month"));
    }

    @Override
    public JsonData save() {
        JsonData data = new JsonData();
        data.setNum("votesPerParty", getVotesPerParty());
        data.setNum("totalVotes", getTotalVotes());
        data.setUUID("topVoter", getTopVoter());
        data.setList("party", getParty());
        data.setList("normal", getNormal());
        data.setList("achievements", getAchievements());
        data.setString("month", getMonth());
        return data;
    }
}
