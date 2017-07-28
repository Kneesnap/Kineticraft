package net.kineticraft.lostcity.utils.tasks;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Allows resource allocation of large tasks.
 * @param <T>
 * Created by Kneesnap on 6/30/2017.
 */
@Getter
public class SchedulerTask<T> {

    private final List<T> values;
    private final Consumer<T> handler;
    private final Runnable onFinish;
    private final int perThread;
    private final int perTick;
    private BukkitTask checkComplete;

    private final List<BukkitTask> tasks = new CopyOnWriteArrayList<>();

    public SchedulerTask(List<T> values, Consumer<T> handler, Runnable onFinish) {
        this(values, handler, onFinish, 100, 50);
    }

    public SchedulerTask(List<T> values, Consumer<T> handler, Runnable onFinish, int perThread, int perTick) {
        this.values = values;
        this.handler = handler;
        this.onFinish = onFinish;
        this.perThread = perThread;
        this.perTick = perTick;
        start();
    }

    /**
     * Start the task at hand.
     */
    public void start() {
        while (!values.isEmpty()) {
            List<T> threadList = new ArrayList<>();
            for (int i = 0; i < Math.min(perThread, values.size()); i++)
                threadList.add(values.remove(0));

            BukkitTask[] task = new BukkitTask[1];
            task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
                for (int i = 0; i < Math.min(perTick, threadList.size()); i++)
                    getHandler().accept(threadList.remove(0));

                // Only continue if this thread's task is done.
                if (!threadList.isEmpty())
                    return;


                // Cancel this task
                tasks.remove(task[0]);
                task[0].cancel();
            }, 0L, 1L);

            tasks.add(task[0]);
        }

        checkComplete = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (!tasks.isEmpty())
                return;

            checkComplete.cancel(); // Don't call again.
            getOnFinish().run();
        }, 0L, 1L);
    }
}
