package com.norcode.bukkit.kiosk;

import com.norcode.bukkit.kiosk.util.ConfigAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayerID extends ConfigAccessor {

	public PlayerID(JavaPlugin plugin) {
		super(plugin, "players.yml");
	}

	public OfflinePlayer getPlayer(UUID uuid) {
		return Bukkit.getServer().getOfflinePlayer(getConfig().getString(uuid.toString()));
	}

	public void registerPlayer(UUID uuid, OfflinePlayer player) {
		getConfig().set(uuid.toString(), player.getName());
	}

	public void save() {
		this.saveConfig();
	}
}
