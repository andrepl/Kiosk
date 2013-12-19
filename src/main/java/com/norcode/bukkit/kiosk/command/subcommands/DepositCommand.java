package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DepositCommand extends SelectedShopCommand {

	public DepositCommand(Kiosk plugin) {
		super(plugin, "deposit", "deposit money into a shop account.", "kiosk.command.deposit",
				StaffPermission.DEPOSIT, new String[] {"deposit help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		if (args.size() == 0) {
			showHelp(player);
			return true;
		}

		double amt = -1;
		try {
			amt = Double.parseDouble(args.peek());
		} catch (IllegalArgumentException ex) {
			throw new Exception("Invalid amount: " + args.peek());
		}
		if (amt < 0) {
			throw new Exception("Amount must be a positive number!");
		}
		if (plugin.getEconomy().withdrawPlayer(player.getName(), amt).transactionSuccess()) {
			shop.setBalance(shop.getBalance() + amt);
		}
		player.sendMessage("New shop balance: " + plugin.getEconomy().format(shop.getBalance()));
		shop.updateSign();
		return true;
	}
}
