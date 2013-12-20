package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StockCommand extends SelectedShopCommand {

	public StockCommand(Kiosk plugin) {
		super(plugin, "stock", "Add or remove stock to your kiosk's inventory.", "kiosk.command.stock", null, new String[] {"/kiosk stock", "Opens a window allowing you to deposit or withdraw items from your kiosk's inventory."});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return Arrays.asList(new String[]{"addonly"});
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		boolean addOnly = false;
		if (!shop.allow(player, StaffPermission.MANAGE_STOCK)) {
			if (!shop.allow(player, StaffPermission.ADD_STOCK)) {
				throw new Exception("You do not have permission to manage the stock for shop!");
			} else {
				addOnly = true;
			}
		} else {
			if (args.size() > 0) {
				for (String a: args) {
					if (a.equalsIgnoreCase("addonly")) {
						addOnly = true;
					}
				}
			}
		}

		if (shop.inventoryIsLocked()) {
			throw new Exception("Someone else is currently managing the inventory for this shop");
		}
		plugin.debug("Opening inventory in " + (addOnly ? "Read-Only" : "Read-Write") + " mode.");
		player.openInventory(shop.getInventory(addOnly));
		return true;
	}

}
