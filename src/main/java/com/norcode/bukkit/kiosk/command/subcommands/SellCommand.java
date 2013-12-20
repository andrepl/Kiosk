package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SellCommand extends SelectedShopCommand {
	public SellCommand(Kiosk plugin) {
		super(plugin, "sell", "sell items to the selected shop", "kiosk.command.sell", null, new String[] {"sell help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return null;
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		if (!shop.isBuying()) {
			throw new Exception("This shop doesn't buy anything.");
		}
		if (shop.inventoryIsLocked()) {
			throw new Exception("This shop is currently being restocked by the staff, please come again.");
		}

		boolean sellAll = false;
		int qty = shop.getQuantity();
		if (args.size() > 0) {
			if (args.peek().equalsIgnoreCase("all")) {
				sellAll = true;
			} else {
				try {
					qty = Integer.parseInt(args.peek());
				} catch (IllegalArgumentException ex) {
					throw new Exception("Invalid amount: " + args.peek());
				}
			}
		}

		if (sellAll) qty = inventoryQty(shop.getItem(), player.getInventory());

		if (qty <= 0) {
			throw new Exception("Amount must be positive");
		}

		if (qty > (shop.getMaxStock() - shop.getStock())) {
			throw new Exception("The shop can only hold " + (shop.getMaxStock() - shop.getStock()) + " more");
		}

		if (qty % shop.getQuantity() != 0) {
			throw new Exception("You must purchase in multiples of " + shop.getQuantity());
		}

		int playerQty = inventoryQty(shop.getItem(), player.getInventory());
		if (qty > playerQty) {
			throw new Exception("You only have " + playerQty);
		}

		double total = (qty / shop.getQuantity()) * shop.getPrice();

		if (shop.getBalance() < total && !shop.isAdminShop()) {
			int canAfford = qty;
			while (shop.getBalance() < total && canAfford >= shop.getQuantity()) {
				canAfford -= shop.getQuantity();
				total = (canAfford / shop.getQuantity()) * shop.getPrice();
			}
			throw new Exception("The shop can only afford to buy " + canAfford);
		}
		if (plugin.getEconomy().depositPlayer(player.getName(), total).transactionSuccess()) {
			shop.setBalance(shop.getBalance() - total);
			shop.setStock(shop.getStock() + qty);
			List<ItemStack> items = new ArrayList<ItemStack>();
			int rem = qty;
			int maxStackSize = shop.getItem().getMaxStackSize();
			int stackSize = maxStackSize;
			ItemStack stack;
			while (rem > 0) {
				if (rem < maxStackSize) {
					stackSize = rem;
				} else {
					stackSize = maxStackSize;
				}
				rem -= stackSize;
				stack = shop.getItem().clone();
				stack.setAmount(stackSize);
				items.add(stack);
			}
			player.getInventory().removeItem(items.toArray(new ItemStack[0]));
			plugin.getDatastore().saveShop(shop);
			shop.updateSign();
			player.sendMessage("You successfully sold " + qty + " " + shop.getItemDisplayName() + " to " +
					shop.getDisplayName() + " for " + plugin.getEconomy().format(total));
		} else {
			throw new Exception("Failed to deposit " + plugin.getEconomy().format(total) + " into your account, transaction cancelled");
		}
		return true;
	}

	public int inventoryQty(ItemStack stack, Inventory inv) {
		int found = 0;
		for (ItemStack s: inv.getContents()) {
			if (s != null) {
				if (s.isSimilar(stack)) {
					found += s.getAmount();
				}
			}
		}
		return found;
	}
}
