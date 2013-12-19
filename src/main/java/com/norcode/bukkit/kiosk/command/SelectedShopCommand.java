package com.norcode.bukkit.kiosk.command;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public abstract class SelectedShopCommand extends PlayerCommand {
	private StaffPermission requiredStaffPermission;

	protected SelectedShopCommand(Kiosk plugin, String name, String description, String requiredPermission, StaffPermission requiredStaffPermission, String[] help) {
		super(plugin, name, description, requiredPermission, help);
	}

	@Override
	public List<String> onTabComplete(Player player, Command command, String label, LinkedList<String> args) {
		Shop shop = plugin.getSelectedShop(player);
		if (shop == null) {
			player.sendMessage(ChatColor.RED + "You do not have a shop selected.");
			return null;
		}
		return onTabComplete(player, shop, command, label, args);
	}

	@Override
	public boolean onCommand(Player player, Command command, String label, LinkedList<String> args) throws Exception {
		Shop shop = plugin.getSelectedShop(player);
		if (shop == null) {
			player.sendMessage(ChatColor.RED + "You do not have a shop selected.");
			return true;
		}
		return onCommand(player, shop, command, label, args);
	}

	public abstract List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args);
	public abstract boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception;

	public StaffPermission getRequiredStaffPermission() {
		return requiredStaffPermission;
	}
}
