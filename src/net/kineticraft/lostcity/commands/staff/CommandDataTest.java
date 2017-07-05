package net.kineticraft.lostcity.commands.staff;

import com.google.gson.JsonObject;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
        test(sender, "old save", () -> p.save().toFile("normal"));
        test(sender, "new save", () -> JsonSerializer.save(p).toFile("new"));

        JsonObject old = JsonData.fromFile("normal").getJsonObject();
        JsonObject newM = JsonData.fromFile("new").getJsonObject();

        test(sender, "old load", () -> new KCPlayer(p.getUuid(), new JsonData(old)));
        test(sender, "new load", () -> JsonSerializer.loadNew(KCPlayer.class, newM));

        new KCPlayer(p.getUuid(), new JsonData(old)).save().toFile("normal2");
        JsonSerializer.save(JsonSerializer.loadNew(KCPlayer.class, newM)).toFile("new2");
    }

    private void test(CommandSender sender, String name, Runnable r) {
        long start = System.nanoTime();
        r.run();
        sender.sendMessage(ChatColor.GREEN.toString() + (System.nanoTime() - start) + " - Testing " + name + ".");
    }
}