package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class BuyCommand extends SelectedShopCommand {
	public BuyCommand(Kiosk plugin) {
		super(plugin, "buy", "buy items from the selected shop", "kiosk.command.buy", null, new String[] {"buy help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return null;
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		if (!shop.isSelling()) {
			throw new Exception("This shop doesn't sell anything.");
		}
		if (shop.inventoryIsLocked()) {
			throw new Exception("This shop is currently being restocked by the staff, please come again.");
		}
		int qty = shop.getQuantity();
		if (args.size() > 0) {
			try {
				qty = Integer.parseInt(args.peek());
			} catch (IllegalArgumentException ex) {
				throw new Exception("Invalid amount: " + args.peek());
			}
		}
		if (qty <= 0) {
			throw new Exception("Amount must be positive");
		}
		if (qty % shop.getQuantity() != 0) {
			throw new Exception("You must purchase in multiples of " + shop.getQuantity());
		}
		if (qty > shop.getStock() && !shop.isAdminShop()) {
			throw new Exception("The shop only has " + shop.getStock() + " remaining");
		}
		double total = (qty / shop.getQuantity()) * shop.getPrice();
		if (plugin.getEconomy().withdrawPlayer(player.getName(), total).transactionSuccess()) {
			shop.setStock(shop.getStock() - qty);
			shop.give(player, qty);
			player.sendMessage("You successfully purchased " + qty + " " + shop.getItemDisplayName() + " for " + plugin.getEconomy().format(total));
		} else {
			throw new Exception("You do not have " + plugin.getEconomy().format(total));
		}
		plugin.getDatastore().saveShop(shop);
		shop.updateSign();
		return true;
	}
}
