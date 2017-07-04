package net.kineticraft.lostcity.commands.staff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Cleanup;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Test data storage.
 * Created by Kneesnap on 7/3/2017.
 */
public class CommandDataTest extends StaffCommand {

    public CommandDataTest() {
        super(EnumRank.DEV, "", "Test data.", "test");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer p = KCPlayer.getWrapper(sender);
        test(sender, "Save Old", () -> p.save().toFile("normal"));
        test(sender, "Bytecode", () -> p.bytecodeTest());
        //test(sender, "Save New", () -> JsonSerializer.save(p).toFile("new"));
        /*test(sender, "Gson", () -> {
            try {
                @Cleanup FileWriter fw = new FileWriter(Core.getFile("gson.json"));
                new GsonBuilder().create().toJson(p, fw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
    }

    private void test(CommandSender sender, String name, Runnable r) {
        long start = System.currentTimeMillis();
        r.run();
        sender.sendMessage(ChatColor.GREEN + name + " Test: " + (System.currentTimeMillis() - start));
    }
}
