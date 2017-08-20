package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.mechanics.Voting.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Holds on to vote data.
 * Created by Kneesnap on 6/3/2017.
 */
@Setter @Getter
public class VoteConfig extends JsonConfig {
    private int votesPerParty = 50;
    private int totalVotes;
    private UUID topVoter;
    private JsonList<PartyReward> party = new JsonList<>();
    private JsonList<ItemStack> normal = new JsonList<>();
    private JsonList<VoteAchievement> achievements = new JsonList<>();

    /**
     * Get the number of votes needed until the next party.
     * @return votesNeeded
     */
    public int getVotesUntilParty() {
        int toParty = getVotesPerParty() - (getTotalVotes() % getVotesPerParty());
        return toParty != getVotesPerParty() ? toParty : 0;
    }
}
