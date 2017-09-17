package net.kineticraft.lostcity.utils.tasks;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

/**
 * A wrapper around BukkitScheduler that remembers all registered tasks, for cancelling later.
 * Created by Kneesnap on 9/16/2017.
 */
public class TaskList {
    private Set<BukkitTask> tasks = new HashSet<>();

    private BukkitScheduler getReal() {
        return Bukkit.getScheduler();
    }

    private BukkitTask add(BukkitTask task) {
        tasks.add(task);
        return task;
    }

    /**
     * Cancel all tasks in this list.
     */
    public void cancelAll() {
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    /**
     * Run a task synchronously.
     * @param runnable
     * @return task
     */
    public BukkitTask runTask(Runnable runnable) {
        return add(getReal().runTask(Core.getInstance(), runnable));
    }

    /**
     * Run a task asynchronously.
     * @param runnable
     * @return task
     */
    public BukkitTask runTaskAsynchronously(Runnable runnable) {
        return add(getReal().runTaskAsynchronously(Core.getInstance(), runnable));
    }

    /**
     * Run a task later.
     * @param runnable
     * @param delay
     * @return task
     */
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return add(getReal().runTaskLater(Core.getInstance(), runnable, delay));
    }

    /**
     * Run a task later, asynchronously.
     * @param runnable
     * @param delay
     * @return task
     */
    public BukkitTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return add(getReal().runTaskLaterAsynchronously(Core.getInstance(), runnable, delay));
    }

    /**
     * Run a task every so often.
     * @param runnable
     * @param delay
     * @param interval
     * @return tasl
     */
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long interval) {
        return add(getReal().runTaskTimer(Core.getInstance(), runnable, delay, interval));
    }

    /**
     * Run a task asynchronously, at an interval.
     * @param runnable
     * @param delay
     * @param interval
     * @return task
     */
    public BukkitTask runTaskTimerAsynchronously(Runnable runnable, long delay, long interval) {
        return add(getReal().runTaskTimerAsynchronously(Core.getInstance(), runnable, delay, interval));
    }
}
