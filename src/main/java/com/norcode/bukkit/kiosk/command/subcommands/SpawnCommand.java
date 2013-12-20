package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.command.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class SpawnCommand extends PlayerCommand {

	public SpawnCommand(Kiosk plugin) {
		super(plugin, "spawn", "Spawn one or more kiosk.", "kiosk.spawn", new String[] {"/kiosk spawn <##>", "Spawns `##` kiosks ready to be placed."});
	}

	@Override
	public List<String> onTabComplete(Player player, Command command, String label, LinkedList<String> args) {
		return null;
	}

	@Override
	public boolean onCommand(Player player, Command command, String label, LinkedList<String> args) throws Exception {
		int qty = 1;
		if (!args.isEmpty()) {
			try {
				qty = Integer.parseInt(args.peek());
			} catch (IllegalArgumentException ex) {
				throw new Exception(ChatColor.DARK_RED + args.pop() + " is not a number!");
			}
		}
		while (qty > Material.ITEM_FRAME.getMaxStackSize()) {
			player.getInventory().addItem(plugin.getNewShopFrame(Material.ITEM_FRAME.getMaxStackSize()));
			qty -= Material.ITEM_FRAME.getMaxStackSize();
		}
		player.getInventory().addItem(plugin.getNewShopFrame(qty));
		return true;
	}
}
