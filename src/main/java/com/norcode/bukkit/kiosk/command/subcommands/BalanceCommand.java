package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import net.minecraft.server.v1_7_R1.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class BalanceCommand extends SelectedShopCommand {

	public BalanceCommand(Kiosk plugin) {
		super(plugin, "balance", "Check the balance of a kiosk.", "kiosk.command.balance", null, new String[] { "/kiosk balance <id>", "Displays the balance of kiosk `id` to the user."});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return null;
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		ItemStack stack = CraftItemStack.asNMSCopy(plugin.getNewShopFrame(2));
		String msg = "Balance: " + ChatColor.GREEN + plugin.getEconomy().format(shop.getBalance());
		player.sendMessage(msg);
		return true;
	}
}
