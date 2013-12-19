package com.norcode.bukkit.kiosk.datastore;

import com.norcode.bukkit.kiosk.Kiosk;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

public class SearchRunner extends BukkitRunnable {

	private Kiosk plugin;
	private int shopsPerTick = 10;

	private LinkedList<SearchTask> tasks = new LinkedList<SearchTask>();

	public SearchRunner(Kiosk plugin) {
		this.plugin = plugin;
		this.runTaskTimer(plugin, 1, 1);
	}

	public int getShopsPerTick() {
		return shopsPerTick;
	}

	@Override
	public synchronized void cancel() throws IllegalStateException {
		while (!tasks.isEmpty()) {
			tasks.pop().cancel();
		}
		super.cancel();
	}

	public void setShopsPerTick(int shopsPerTick) {
		this.shopsPerTick = shopsPerTick;
	}

	public void add(SearchTask task) {
		this.tasks.add(task);
	}

	@Override
	public void run() {
		if (tasks.isEmpty()) return;

		int shopsProcessed = 0;
		while (shopsProcessed < shopsPerTick) {
			SearchTask task = tasks.pollLast();
			if (task == null) break;
			shopsProcessed += task.run(shopsPerTick - shopsProcessed);
			if (task.isFinished()) {
				task.onComplete();
			} else {
				tasks.push(task);
			}
		}
	}

}
