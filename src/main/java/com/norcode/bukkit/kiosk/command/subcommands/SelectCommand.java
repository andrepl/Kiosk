package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.PlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class SelectCommand extends PlayerCommand {
	public SelectCommand(Kiosk plugin) {
		super(plugin, "select", "Select a kiosk by id.", "kiosk.command.select", new String[] {"/kiosk select <id>", "Selects `id` as the current kiosk."});
	}

	@Override
	public List<String> onTabComplete(Player player, Command command, String label, LinkedList<String> args) {
		List<String> results = new ArrayList<String>();
		for (UUID id: plugin.getStore().allShopIds()) {
			if (id.toString().toLowerCase().startsWith(id.toString().toLowerCase())) {
				results.add(id.toString());
			}
		}
		return results;
	}

	@Override
	public boolean onCommand(Player player, Command command, String label, LinkedList<String> args) throws Exception {
		if (args.size() == 0) {
			showHelp(player);
			return true;
		}
		UUID id = UUID.fromString(args.peek());
		Shop shop = plugin.getStore().getShop(id);
		if (shop == null) {
			throw new Exception("Couldn't find shop with id " + args.peek());
		}
		plugin.setSelectedShop(player, shop);
		player.sendMessage("You have selected: " + shop.getName() + " @ " + shop.getLocation().getBlockX() + ", " + shop.getLocation().getBlockZ());
		return true;
	}
}
