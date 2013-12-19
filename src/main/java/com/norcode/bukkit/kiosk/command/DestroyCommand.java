package com.norcode.bukkit.kiosk.command;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.util.ChatArt;
import com.norcode.bukkit.kiosk.util.Util;
import com.norcode.bukkit.kiosk.util.chat.Text;
import com.norcode.bukkit.kiosk.util.chat.ClickAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DestroyCommand extends SelectedShopCommand {
	protected DestroyCommand(Kiosk plugin) {
		super(plugin, "destroy", "destroy the selected shop", "kiosk.command.destroy", StaffPermission.DESTROY, new String[] {"Destroy Help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		if (args.size() == 0) {
			Util.send(player, ChatArt.getByName("warning").formatMessage(
					new Text("Are You Sure?").setBold(true).setUnderline(true),
					new Text(""),
					new Text("You are about to destroy " + shop.getDisplayName()),
					new Text("Remaining stock and funds will be returned to you"),
					new Text("Any items you can't hold will be dropped on the ground"),
					new Text("If you're absolutely sure you want to permanently"),
					new Text("destroy this shop, click below"),
					new Text(""),
					new Text("       ").append(
							new Text("[").setBold(true),
							new Text(ChatColor.RED + "" + ChatColor.BOLD + "DESTROY THIS SHOP")
									.setHoverText("Click to permanently destroy " + shop.getDisplayName())
									.setClick(ClickAction.RUN_COMMAND, "/kiosk destroy " + shop.getId().toString().substring(0, 4)),
							new Text("]").setBold(true))).toArray(new Object[0]));
			return true;
		}
		String key = args.peek().toLowerCase();
		if (shop.getId().toString().toLowerCase().startsWith(key) && key.length() >= 4) {
			if (plugin.getEconomy().depositPlayer(player.getName(), shop.getBalance()).transactionSuccess()) {
				player.sendMessage(plugin.getEconomy().format(shop.getBalance()) + " has been added to your account.");
				shop.give(player, shop.getStock());
				shop.getItemFrame().setItem(new ItemStack(Material.AIR));
				shop.getLocation().getWorld().dropItem(shop.getLocation(), plugin.getNewShopFrame(1));
				shop.getItemFrame().remove();
				Sign sign = shop.getSign();
				if (sign != null) {
					sign.setType(Material.AIR);
				}
				plugin.getDatastore().deleteShop(shop.getId());
			} else {
				throw new Exception("Failed to deposit " + shop.getBalance() + " into your account! Cannot destroy the shop with funds in it!");
			}
		} else {
			throw new Exception("Invalid shop key");
		}
		return true;
	}
}
