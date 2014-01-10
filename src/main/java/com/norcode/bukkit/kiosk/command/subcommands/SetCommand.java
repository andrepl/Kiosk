package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.ShopType;
import com.norcode.bukkit.kiosk.command.BaseCommand;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import com.norcode.bukkit.kiosk.util.ChatArt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetCommand extends BaseCommand {

	private static Pattern qtyPriceRe = Pattern.compile("^(\\d+\\s*)??(?:for|\\/)?(\\s*\\$?[\\d.]+)$",
														Pattern.CASE_INSENSITIVE);

	public SetCommand(Kiosk plugin) {
		super(plugin, "set", "change shop settings", "kiosk.command.set", null);
		registerSubcommand(new SetSellingCommand(plugin));
		registerSubcommand(new SetBuyingCommand(plugin));
		registerSubcommand(new SetPrivateCommand(plugin));
		registerSubcommand(new SetNameCommand(plugin));
		registerSubcommand(new SetDepositAccountCommand(plugin));
		registerSubcommand(new SetAdminShopCommand(plugin));
		registerSubcommand(new SetIconCommand(plugin));
		registerSubcommand(new SetItemCommand(plugin));
		registerSubcommand(new SetBorderCommand(plugin));


	}

	public static class SetIconCommand extends SelectedShopCommand {

		public SetIconCommand(Kiosk plugin) {
			super(plugin, "icon", "Set the kiosk icon.", "kiosk.command.set.icon", StaffPermission.SET_ICON, new String [] {"/kiosk icon <icon_name>.", "Sets `icon_name` as the icon for the kiosk."});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			List<String> results = new ArrayList<String>();
			if (args.size() == 1) {
				String partial = args.peek().toLowerCase();
				for (ChatArt art: ChatArt.allIcons()) {
					if (art.getName().toLowerCase().startsWith(partial)) {
						results.add(art.getName());
					}
				}
			}
			return results;
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			if (args.size() == 0) {
				shop.setIcon(null);
				plugin.getStore().saveShop(shop);
				player.sendMessage("Shop icon cleared");
			} else {
				ChatArt icon = ChatArt.getByName(args.peek().toLowerCase());
				if (icon == null) {
					throw new Exception("Unknown Icon: " + args.peek());
				} else {
					shop.setIcon(icon);
					plugin.getStore().saveShop(shop);
					player.sendMessage("Shop icon set to: " + icon.getName());
				}
			}
			return true;
		}
	}

	public static class SetItemCommand extends SelectedShopCommand {

		public SetItemCommand(Kiosk plugin) {
			super(plugin, "item", "Set the shop item.", "kiosk.command.set.item", StaffPermission.CHANGE_ITEM, new String [] {"Set Item Help"});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			List<String> results = new ArrayList<String>();
			return results;
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			if (shop.getStock() > 0 && !shop.isAdminShop()) {
				throw new Exception("You cannot change the item while there is stock");
			} else if (shop.inventoryIsLocked()) {
				throw new Exception("You cannot change the item while the inventory is open");
			}
			if (args.size() == 0) {
				shop.setItem(null);
				plugin.getStore().saveShop(shop);
				shop.getItemFrame().setItem(new ItemStack(Material.AIR));
				player.sendMessage("The shop Item has been cleared, place a new Item in the frame reactivate the shop.");
				shop.updateSign();
			}
			return true;
		}
	}

	public static class SetSellingCommand extends SelectedShopCommand {

		protected SetSellingCommand(Kiosk plugin) {
			super(plugin, "selling", "Setup a shop that sells", "kiosk.command.set.selling", StaffPermission.PRICE, new String[] {
					"set selling help"
			});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			return new ArrayList<String>();
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			String argstr = "";
			for (String s: args) {
				argstr += s + " ";
			}
			if (argstr.endsWith(" ")) {
				argstr = argstr.substring(0,argstr.length()	- 1);
			}
			Matcher matcher = qtyPriceRe.matcher(argstr);
			if (!matcher.matches()) {
				 throw new Exception("Couldn't understand '" + argstr + "'");
			}
			int qty = 1;
			String qtyS = matcher.group(1) == null ? "1" : matcher.group(1).trim();
			if (!qtyS.equals("")) {
				try {
					qty = Integer.parseInt(qtyS);
				} catch (IllegalArgumentException ex) {
					throw new Exception("Invalid quantity: '" + qtyS + "'");
				}
			}

			double price = -1;
			String priceS = matcher.group(2).trim();
			try {
				price = Double.parseDouble(priceS);
			} catch (IllegalArgumentException ex) {
				throw new Exception("Invalid price: '" + priceS + "'");
			}

			shop.setType(ShopType.SELLING);
			shop.setPrice(price);
			shop.setQuantity(qty);
			plugin.getStore().saveShop(shop);
			String message = "This shop is now selling " + shop.getQuantity();
			message += " " + shop.getItemDisplayName() + " for ";
			Double _price = shop.getPrice();
			plugin.getLogger().info("shop price is " + _price);
			message += plugin.getEconomy().format(_price);
			player.sendMessage(message);
			shop.updateSign();
			return true;
		}
	}


	public static class SetBuyingCommand extends SelectedShopCommand {

		protected SetBuyingCommand(Kiosk plugin) {
			super(plugin, "buying", "Setup a shop that buys", "kiosk.command.set.buying",
					StaffPermission.PRICE, new String[] {
					"set buying help"
			});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			return new ArrayList<String>();
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			String argstr = "";
			for (String s: args) {
				argstr += s + " ";
			}
			if (argstr.endsWith(" ")) {
				argstr = argstr.substring(0,argstr.length()	- 1);
			}
			Matcher matcher = qtyPriceRe.matcher(argstr);
			if (!matcher.matches()) {
				throw new Exception("Couldn't understand '" + argstr + "'");
			}
			int qty = 1;
			String qtyS = matcher.group(1) == null ? "1" : matcher.group(1).trim();
			if (!qtyS.equals("")) {
				try {
					qty = Integer.parseInt(qtyS);
				} catch (IllegalArgumentException ex) {
					throw new Exception("Invalid quantity: '" + qtyS + "'");
				}
			}

			double price = -1;
			String priceS = matcher.group(2).trim();
			try {
				price = Double.parseDouble(priceS);
			} catch (IllegalArgumentException ex) {
				throw new Exception("Invalid price: '" + priceS + "'");
			}

			shop.setType(ShopType.BUYING);
			shop.setPrice(price);
			shop.setQuantity(qty);
			plugin.getStore().saveShop(shop);
			String message = "This shop is now buying " + shop.getQuantity();
			message += " " + shop.getItemDisplayName() + " for ";
			Double _price = shop.getPrice();
			plugin.getLogger().info("shop price is " + _price);
			message += plugin.getEconomy().format(_price);
			player.sendMessage(message);
			shop.updateSign();
			return true;
		}
	}

	public static class SetPrivateCommand extends SelectedShopCommand {

		protected SetPrivateCommand (Kiosk plugin) {
			super(plugin, "private", "Set or toggle the private flag", "kiosk.command.set.private",
					StaffPermission.PRIVATE, new String[] {
					"set private help"
			});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			return Arrays.asList("true", "false");
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			if (args.size() > 0) {
				shop.setPrivate(BaseCommand.parseBoolean(args.peek()));
			} else {
				shop.setPrivate(!shop.isPrivate());

			}
			plugin.getStore().saveShop(shop);
			player.sendMessage("This shop is " + (shop.isPrivate() ? "now" : "no longer") + " private");
			return true;
		}
	}


	public static class SetNameCommand extends SelectedShopCommand {

		protected SetNameCommand (Kiosk plugin) {
			super(plugin, "name", "Set the name of the shop", "kiosk.command.set.name", StaffPermission.RENAME, new String[] {
					"set name help"
			});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			return new ArrayList<String>();
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			String name = "";
			while (args.size() > 0) {
				name += args.pop();
				if (args.size() > 0) {
					name += " ";
				}
			}
			shop.setName(name == "" ? null : name);
			plugin.getStore().saveShop(shop);
			player.sendMessage("This shop is now named '" + name + "'");
			return true;
		}
	}

	public static class SetBorderCommand extends SelectedShopCommand {

		protected SetBorderCommand (Kiosk plugin) {
			super(plugin, "border", "Set the color of the border around the text", "kiosk.command.set.border", StaffPermission.DISPLAY, new String[] {
					"set border help"
			});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			List<String> results = new ArrayList<String>();
			if (args.size() == 1) {
				String border = args.pop().toLowerCase();
				if ("none".startsWith(border)) {
					results.add("none");
				}
				for (ChatColor c: ChatColor.values()) {
					if (c.name().toLowerCase().startsWith(border)) {
						results.add(c.name());
					}
				}
			}
			return results;
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			String name = "";
			while (args.size() > 0) {
				name += args.pop();
				if (args.size() > 0) {
					name += " ";
				}
			}
			shop.setName(name == "" ? null : name);
			plugin.getStore().saveShop(shop);
			return true;
		}
	}

	public static class SetDepositAccountCommand extends SelectedShopCommand {

		protected SetDepositAccountCommand(Kiosk plugin) {
			super(plugin, "depositaccount", "Set the account to deposit profits into", "kiosk.command.set.depositaccount",
					StaffPermission.OWNER_ONLY, new String[] {
					"set depositaccount help"
			});
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

			if (plugin.getEconomy().hasAccount(args.peek())) {
				shop.setDepositAccount(args.pop());
				plugin.getStore().saveShop(shop);
				player.sendMessage("Proceeds from this shop will be automatically deposited into the account: '" + shop.getDepositAccount() + "'");
				return true;
			} else {
				throw new Exception("No such account '" + args.peek() + "'");
			}

		}
	}

	public static class SetAdminShopCommand extends SelectedShopCommand {

		protected SetAdminShopCommand(Kiosk plugin) {
			super(plugin, "adminshop", "sets/toggle the adminshop flag", "kiosk.command.set.adminshop",
					StaffPermission.OWNER_ONLY, new String[] {
					"set adminshop help"
			});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			return Arrays.asList(new String[] {"true", "false"});
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			if (args.size() > 0) {
				shop.setAdminShop(BaseCommand.parseBoolean(args.peek()));
			} else {
				shop.setAdminShop(!shop.isAdminShop());

			}
			plugin.getStore().saveShop(shop);
			player.sendMessage("This shop is " + (shop.isAdminShop() ? "now" : "no longer") + " an Admin-Shop");
			return true;

		}
	}
}
