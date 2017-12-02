package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.PlayerCommand;

import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.utils.TextBuilder;
import net.kineticraft.lostcity.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CommandProposeWarp extends PlayerCommand {
    /*
    This Class is to prompt the user for understanding our terms
    Once they accept both agreements, then they can choose which catagory their warp will fall under
    Then they will explain why they want a warp.
    We will then grab the location, and what they had to say and input it into our discord channel
     */

    public CommandProposeWarp() {
        super("[command]", "Submit a warp permit!","warpform");
    }
    //initializing variables
    private static List<String> types = Arrays.asList("event", "shop", "showcase", "town", "other");

    private static List<String> greetingMessages = Arrays.asList(
            "Hi there, ",
            "Hey, ",
            "Hello there, ",
            "Hi, ",
            "Bonjour, ",
            "Sup dawg, "
    );

    private static List<String> exitMessages = Arrays.asList(
            "Thanks for submitting, have a pleasant day!",
            "Your submission has been processed, big brother is watching!",
            "Thanks for your submission, have a happy happy joy-joy day.",
            "Your Pokemon are now healed, we hope to see you again soon!",
            "Compiling tattle...transfluxing signal...submission sent! Thanks!",
            "Submission sent, don't forget to give a cookie to your Mod.",
            "Thanks for looking out homie, we got your submission!"
    );
    private static List<String> declineMessages  = Arrays.asList(
            "Thanks for looking, have a pleasant day!",
            "Have a great day!",
            "Let us know what you think!",
            "Thanks again, Now go find some candy"
    );
    private static String prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Borkley" + ChatColor.GREEN + " "
            + "Warp Permit" + ChatColor.GRAY + ": " + ChatColor.WHITE;

    private static String accMessage = ChatColor.GREEN + "Accept";

    private static String declineMessage = ChatColor.DARK_RED + "Decline";

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        // you can get more properties by casting as player
        Player player = (Player) sender;
       // boolean warpContinue = true;
        //boolean readRules = true;
        sender.sendMessage(prefix + "This is to apply for a warp permit. Do you wish to continue? ");
        //This command is to give a prompt to confirm or cancel this request (if confirm continue, if cancel it sends back the usage to the user?)
        Callbacks.promptConfirm(player,
                //either need to test this code into the accept function or come up with a way to store a variable to pass through here
                () -> { sender.sendMessage(prefix + "Please stand where you would like your ideal warp before continuing.");
                    //if accepting, continue warpform
                    sender.sendMessage(prefix + " \n Warp Requirements: \n" +
                            "1) A safe spawn-in and area (well-lit, no mobs, no player harm) \n" +
                            "2) Warps must be fully claimed. \n" +
                            "3) An active owner \n" +
                            "4) A reapplication process each quarter to prevent dead warps. \n");

                    sender.sendMessage(prefix + "Do you accept all requirements for the warps? ");

                    Callbacks.promptConfirm(player,
                            () -> { sender.sendMessage(prefix +
                                    Utils.randElement(greetingMessages) + player.getDisplayName() +
                                    " which type of warp permit are you applying for? \n ( Event |  Shop | Showcase | Town | Exit ) " );
                                    //If Accepting, follow this line of code
                                    Callbacks.promptConfirm(player,
                                        () -> formWarp(sender, "event"),
                                        //If they hit Next: then they will open another Prompt
                                        () ->
                                                Callbacks.promptConfirm(player,
                                                        () -> formWarp(sender, "shop"),
                                                        () ->
                                                                Callbacks.promptConfirm(player,
                                                                        () -> formWarp(sender, "showcase"),
                                                                        () -> Callbacks.promptConfirm(player,
                                                                                () -> formWarp(sender, "town"),
                                                                                () -> Callbacks.promptConfirm(player,
                                                                                        () -> formWarp(sender, "other"),
                                                                                        () -> { sender.sendMessage(prefix + Utils.randElement(declineMessages)); return; },
                                                                                        "Other Ideas!",
                                                                                        "Exit Submission"
                                                                                ),
                                                                                "Town",
                                                                                "Other"
                                                                        ),
                                                                        "Showcase",
                                                                        "Next"
                                                                )
                                                        ,
                                                        "Shop",
                                                        "Next"
                                                )
                                        ,
                                        "Event",
                                        "Next"
                                    );

                                    },
                            () -> { sender.sendMessage(prefix + Utils.randElement(declineMessages)); },
                            accMessage,
                            declineMessage
                            );
                },

                () -> { sender.sendMessage(prefix + Utils.randElement(declineMessages)); },
                accMessage,
                declineMessage
                );
        // (This was used before I got promptConfirm working player.sendMessage(textBuilder.create());

       // else{
       //     sender.sendMessage(prefix + Utils.randElement(declineMessages));
        //}

    }

    private static void formWarp(CommandSender sender, String type ) {
        Player player = (Player) sender;
        if (types.contains(type)) {

            if (type.equals("event")) {
                sender.sendMessage(prefix + "Why do you think your event should become a warp?");
                Callbacks.listenForChat(player, description -> submitWarp(player, type, description));

            } else if (type.equals("shop")) {
                sender.sendMessage(prefix + "Why do you think your shop should become a warp?");
                Callbacks.listenForChat(player, description -> submitWarp(player, type, description));

            } else if (type.equals("showcase")) {
                sender.sendMessage(prefix + "Why do you think your showcase should become a warp?");
                Callbacks.listenForChat(player, description -> submitWarp(player, type, description));

            } else if (type.equals("town")) {
                sender.sendMessage(prefix + "Why do you think your town should become a warp?");
                Callbacks.listenForChat(player, description -> submitWarp(player, type, description));

            }
            else if (type.equals("other")) {
                sender.sendMessage(prefix + "\"Please describe your own warp idea");
                Callbacks.listenForChat(player, description -> submitWarp(player, type, description));

            }

        }
    }

    private static void submitWarp(Player reporter, String type, String description) {
        Location loc = reporter.getLocation();
        String message = "" +
                "New **" + type + "** Warp Permit submission from `" + reporter.getName() + "`\n" +
                "Time: `" + new Date().toString() + "`\n" +
                "Teleport: `/tl " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + (int) loc.getYaw() + " " + (int) loc.getPitch() + " " + loc.getWorld().getName() + "`\n" +
                "Description:\n" +
                "```\n" +
                description + "\n" +
                "```";
        //DiscordAPI.sendMessage(DiscordChannel.WARP_PROPOSALS, message);
        //Chat.log something need to ask Sab
        Core.logInfo(message);
        reporter.sendMessage(prefix + Utils.randElement(exitMessages));
    }
    private static void endMessage(Player reporter){
        reporter.sendMessage(prefix + Utils.randElement(exitMessages));
    }

}
