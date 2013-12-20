package com.norcode.bukkit.kiosk.command.subcommands.staff;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.BaseCommand;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StaffPermDefaultsCommand extends BaseCommand {

	protected StaffPermDefaultsCommand(Kiosk plugin) {
		super(plugin, "permdefaults", "manage the default permissions for staff members",
				"kiosk.command.staff.permdefaults", new String[] {"/kiosk staff permdefaults [add|remove] [permission]", "Will `add` or `remove` `permission` from the default staff permissions."});
		registerSubcommand(new AddCommand(plugin));
		registerSubcommand(new RemoveCommand(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, LinkedList<String> args) throws Exception {
		if (args.size() == 0) {
			if (sender instanceof Player) {
				Shop shop = plugin.getSelectedShop((Player) sender);
				if (shop != null) {
					showDefaults(sender, shop);
					return true;
				}
			}
		}
		return super.onCommand(sender, command, label, args);
	}

	private static void showDefaults(CommandSender sender, Shop shop) {
		String line = ChatColor.BOLD + "Default Staff Permissions: " + ChatColor.RESET;
		for (StaffPermission perm: shop.getDefaultPermissions()) {
			line += perm.getDisplay().toUpperCase() + ", ";
		}
		if (line.endsWith(", ")) {
			line = line.substring(0, line.length() -2);
		}
		sender.sendMessage(line);
	}

	@Override
	public void showHelp(CommandSender sender) {
		super.showHelp(sender);
		if (sender instanceof Player) {
			Shop shop = plugin.getSelectedShop((Player) sender);
			if (shop != null) {
				showDefaults(sender, shop);
			}
		}
	}

	public static class AddCommand extends SelectedShopCommand {

		protected AddCommand(Kiosk plugin) {
			super(plugin, "add", "add a default staff permission", "kiosk.command.staff.permdefaults", StaffPermission.MANAGE_STAFF, new String[] {"Add Default Perm Help"});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			Set<String> others = new HashSet<String>();
			while (args.size() > 1) {
				String a = args.pop();
				for (StaffPermission perm: StaffPermission.values()) {
					if (perm.getDisplay().equalsIgnoreCase(a)) {
						others.add(perm.getDisplay().toUpperCase());
					}
				}
			}

			List<String> results = new ArrayList<String>();
			for (StaffPermission perm: StaffPermission.values()) {
				if (perm.equals(StaffPermission.OWNER_ONLY)) continue;
				if (perm.getDisplay().toLowerCase().startsWith(args.peek().toLowerCase())) {
					if (!others.contains(perm.getDisplay().toUpperCase()) && !shop.getDefaultPermissions().contains(perm)) {
						results.add(perm.getDisplay().toUpperCase());
					}
				}
			}
			return results;
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			if (args.size() == 0) {
				showHelp(player);
				return true;
			}
			HashSet<StaffPermission> perms = new HashSet<StaffPermission>();
			for (String s: args) {
				boolean valid = false;
				for (StaffPermission perm: StaffPermission.values()) {
					if (s.equalsIgnoreCase(perm.getDisplay())) {
						perms.add(perm);
						valid = true;
						break;
					}
				}
				if (!valid) {
					throw new Exception("Unknown Staff Permission: " + s);
				}
			}

			shop.getDefaultPermissions().addAll(perms);
			plugin.getDatastore().saveShop(shop);
			showDefaults(player, shop);
			return true;
		}
	}


	public static class RemoveCommand extends SelectedShopCommand {

		protected RemoveCommand(Kiosk plugin) {
			super(plugin, "remove", "remove a default staff permission", "kiosk.command.staff.permdefaults",
					StaffPermission.MANAGE_STAFF, new String[] {"Remove Default Perm Help"});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			Set<String> others = new HashSet<String>();
			while (args.size() > 1) {
				String a = args.pop();
				for (StaffPermission perm: StaffPermission.values()) {
					if (perm.getDisplay().equalsIgnoreCase(a)) {
						others.add(perm.getDisplay().toUpperCase());
					}
				}
			}

			List<String> results = new ArrayList<String>();
			for (StaffPermission perm: StaffPermission.values()) {
				if (perm.equals(StaffPermission.OWNER_ONLY)) continue;
				if (perm.getDisplay().toLowerCase().startsWith(args.peek().toLowerCase())) {
					if (!others.contains(perm.getDisplay().toUpperCase()) && shop.getDefaultPermissions().contains(perm)) {
						results.add(perm.getDisplay().toUpperCase());
					}
				}
			}
			return results;
		}

		@Override
		public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
			if (args.size() == 0) {
				showHelp(player);
				return true;
			}
			HashSet<StaffPermission> perms = new HashSet<StaffPermission>();
			for (String s: args) {
				boolean valid = false;
				for (StaffPermission perm: StaffPermission.values()) {
					if (s.equalsIgnoreCase(perm.getDisplay())) {
						perms.add(perm);
						valid = true;
						break;
					}
				}
				if (!valid) {
					throw new Exception("Unknown Staff Permission: " + s);
				}
			}

			shop.getDefaultPermissions().removeAll(perms);
			plugin.getDatastore().saveShop(shop);
			showDefaults(player, shop);
			return true;
		}
	}
}
