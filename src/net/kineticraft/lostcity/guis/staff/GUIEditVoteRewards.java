package net.kineticraft.lostcity.guis.staff;

import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.configs.VoteConfig;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.mechanics.Voting;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Allows staff to edit vote rewards.
 * This is kind of yucky. Please don't use this as a baseline for creating other display. Use interfaces instead.
 * Created on a time crunch.
 * TODO: Make this not terrible.
 * Created by Kneesnap on 6/8/2017.
 */
public class GUIEditVoteRewards extends GUI {

    public GUIEditVoteRewards(Player player) {
        super(player, "Vote Rewards");
    }

    @Override
    public void addItems() {
        VoteConfig data = Configs.getVoteData();

        data.getParty().getValues().forEach(vr ->
            addItem(vr.getItem()).middleClick(e -> {
                getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new chance of getting this reward.");
                Callbacks.listenForNumber(getPlayer(), 0, 100, n -> {
                    vr.setChance(n);
                    getPlayer().sendMessage(ChatColor.GREEN + "Updated chance.");
                });
            }).leftClick(e ->
                new GUIItemEditor(getPlayer(), vr.getItem(), i -> vr.setItem(i.generateItem()))
            ).rightClick(e -> {
                getPlayer().sendMessage(ChatColor.GREEN + "Removed.");
                Configs.getVoteData().getParty().remove(vr);
                reconstruct();
            }).addLore("",
                    ChatColor.YELLOW + "Chance: 1 / " + vr.getChance(),
                    "",
                    "Left-Click: " + ChatColor.WHITE + "Edit Reward",
                    "Middle-Click: " + ChatColor.WHITE + "Edit Chance",
                    "Right-Click: " + ChatColor.WHITE + "Remove"));

        addReward(iw -> data.getParty().add(new Voting.PartyReward(10, iw.generateItem())));

        addDivider(DyeColor.LIME,"Normal");
        data.getNormal().forEach(n -> addItem(n)
                .leftClick(e -> new GUIItemEditor(getPlayer(), iw -> data.getNormal().replace(n, iw.generateItem())))
                .rightClick(e -> {
                    data.getNormal().remove(n);
                    getPlayer().sendMessage(ChatColor.GREEN + "Removed.");
                    reconstruct();
                }).addLore("", "Left-Click: " + ChatColor.WHITE + "Edit Reward", "Right-Click: " + ChatColor.WHITE + "Remove"));
        addReward(iw -> data.getNormal().add(iw.generateItem()));

        addDivider(DyeColor.PURPLE, "Achievement");
        data.getAchievements().getValues().forEach(a -> addItem(a.getItem())
                .leftClick(e -> new GUIItemEditor(getPlayer(), iw -> a.setItem(iw.generateItem())))
                .middleClick(e -> {
                    getPlayer().sendMessage(ChatColor.GREEN + "Please enter the number of votes needed.");
                    Callbacks.listenForNumber(getPlayer(), 0, 500, n -> {
                        a.setVotesNeeded(n);
                        getPlayer().sendMessage(ChatColor.GREEN + "Updated.");
                    });
                })
                .rightClick(e -> {
                    data.getAchievements().remove(a);
                    getPlayer().sendMessage(ChatColor.GREEN + "Removed.");
                    reconstruct();
                }));
        addReward(iw -> data.getAchievements().add(new Voting.VoteAchievement(100, iw.generateItem())));
    }

    private void addReward(Consumer<ItemWrapper> iw) {
        addItem(Material.WOOL, ChatColor.GREEN + "Add Reward", "Click here to add a new vote reward.")
                .anyClick(e -> new GUIItemEditor(getPlayer(), iw)).setColor(DyeColor.LIME);
    }

    /**
     * Adds the divider between rows.
     * @param color
     * @param name
     */
    private void addDivider(DyeColor color, String name) {
        nextRow();
        fillGlass(color);
        setSlotIndex(getSlotIndex() - 5);
        addItem(Material.MAP, ChatColor.GOLD + name + " Rewards");
        nextRow();
    }

    @Override
    public void onClose() {
        Configs.getVoteData().saveToDisk();
    }
}
