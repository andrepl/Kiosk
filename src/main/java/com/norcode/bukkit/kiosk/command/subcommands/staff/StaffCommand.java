package com.norcode.bukkit.kiosk.command.subcommands.staff;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.BaseCommand;
import com.norcode.bukkit.kiosk.command.CommandHandler;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StaffCommand extends BaseCommand {

	HashMap<String, StaffMemberSubcommand> staffSubcommands = new HashMap<String, StaffMemberSubcommand>();

	public void registerStaffSubcommand(StaffMemberSubcommand sub) {
		staffSubcommands.put(sub.getName().toLowerCase(), sub);
	}

	public StaffCommand(Kiosk plugin) {
		super(plugin, "staff", "manage staff and staff options", "kiosk.command.staff", null);
		registerSubcommand(new AddStaffCommand(plugin));
		registerSubcommand(new RemoveStaffCommand(plugin));
		registerSubcommand(new StaffCopyCommand(plugin));
		registerSubcommand(new StaffPasteCommand(plugin));
		registerSubcommand(new StaffPermDefaultsCommand(plugin));
		registerStaffSubcommand(new StaffMemberCopyCommand(plugin));
		registerStaffSubcommand(new StaffMemberGrantCommand(plugin));
		registerStaffSubcommand(new StaffMemberRevokeCommand(plugin));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, LinkedList<String> args) {
		if (args.size() > 1) {
			CommandHandler subCmd = subCommands.get(args.peek().toLowerCase());
			if (subCmd != null) {
				if (subCmd.getRequiredPermission() == null || sender.hasPermission(subCmd.getRequiredPermission())) {
					if (subCmd instanceof SelectedShopCommand) {
						if (sender instanceof Player) {
							Shop shop = plugin.getSelectedShop((Player) sender);
							if (shop != null) {
								if (shop.allow((Player) sender, ((SelectedShopCommand) subCmd).getRequiredStaffPermission())) {
									return subCmd.onTabComplete(sender, command, args.pop(), args);
								}
							}
						}
					} else {
						return subCmd.onTabComplete(sender, command, args.pop(), args);
					}
				}
			} else {
				Shop shop = plugin.getSelectedShop((Player) sender);
				if (shop != null) {
					StaffMember found = null;
					for (StaffMember staff: shop.getStaff()) {
						OfflinePlayer op = plugin.getOfflinePlayer(staff.getPlayerId());
						if (op.getName().equalsIgnoreCase(args.peek())) {
							found = staff;
							break;
						}
					}
					if (found != null) {
						args.pop();
						if (args.size() == 1) {
							List<String> results = new ArrayList<String>();
							for (StaffMemberSubcommand c: staffSubcommands.values()) {
								if (c.getName().toLowerCase().startsWith(args.peek().toLowerCase())) {
									if (c.getRequiredPermission() == null || sender.hasPermission(c.getRequiredPermission())) {
										results.add(c.getName());
									}
								}
							}
							return results;
						} else if (args.size() > 1) {
							StaffMemberSubcommand cmd = staffSubcommands.get(args.peek().toLowerCase());
							if (cmd != null) {
								if (cmd.getRequiredPermission() == null || sender.hasPermission(cmd.getRequiredPermission())) {
									return cmd.onTabComplete((Player) sender, shop, found, command, args.pop(), args);
								}
							}
						}
					}
				}
			}
			return null;
		} else {
			// Single arg, must process it
			List<String> results = new ArrayList<String>();
			for (Map.Entry<String, CommandHandler> cmdPair : subCommands.entrySet()) {
				if (cmdPair.getKey().startsWith(args.peek().toLowerCase())) {
					if (cmdPair.getValue().getRequiredPermission() == null || sender.hasPermission(cmdPair.getValue().getRequiredPermission())) {
						if (cmdPair.getValue() instanceof SelectedShopCommand) {
							if (sender instanceof Player) {
								Shop shop = plugin.getSelectedShop((Player) sender);
								if (shop != null) {
									if (shop.allow((Player) sender, ((SelectedShopCommand) cmdPair.getValue()).getRequiredStaffPermission())) {
										results.add(cmdPair.getKey());
									}
								}
							}
							continue;
						}
						results.add(cmdPair.getKey());
					}
				}
			}
			if (sender instanceof Player) {
				Shop shop = plugin.getSelectedShop((Player) sender);
				if (shop != null) {
					for (StaffMember m: shop.getStaff()) {
						OfflinePlayer op = plugin.getOfflinePlayer(m.getPlayerId());
						if (op.getName().toLowerCase().startsWith(args.peek())) {
							results.add(op.getName());
						}
					}
				}
			}
			return results;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, LinkedList<String> args) throws Exception {
		if (sender instanceof Player) {
			if (!plugin.getConfig().getStringList("enabled-worlds").contains(((Player) sender).getWorld().getName())) {
				sender.sendMessage(ChatColor.DARK_RED + "This command is not allowed in this world!");
				return true;
			}
		}
		if (args.size() > 0) {
			CommandHandler subCmd = subCommands.get(args.peek().toLowerCase());
			if (subCmd != null) {
				if (subCmd.getRequiredPermission() == null || sender.hasPermission(subCmd.getRequiredPermission())) {
					if (subCmd instanceof SelectedShopCommand) {
						if (sender instanceof Player) {
							Shop shop = plugin.getSelectedShop((Player) sender);
							if (shop == null) {
								throw new Exception("You do not have a shop selected.");
							}
							if (!shop.allow((Player) sender, ((SelectedShopCommand) subCmd).getRequiredStaffPermission())) {
								throw new Exception("You do not have permission for that command.");
							}
							return ((SelectedShopCommand) subCmd).onCommand((Player) sender, shop, command, args.pop(), args);
						}
					} else {
						return subCmd.onCommand(sender, command, args.pop(), args);
					}
				} else {
					throw new Exception("You do not have permission for that command.");
				}
			} else {
				// Handle per-staff member commands.
				if (sender instanceof Player) {
					Shop shop = plugin.getSelectedShop((Player) sender);
					if (shop == null) {
						throw new Exception("You do not have a shop selected.");
					}
					if (!shop.allow((Player) sender, StaffPermission.MANAGE_STAFF)) {
						throw new Exception("You do not have permission to manage staff for this shop");
					}
					String partial = args.peek().toLowerCase();
					List<StaffMember> matches = new ArrayList<StaffMember>();
					for (StaffMember member: shop.getStaff()) {
						OfflinePlayer op = plugin.getOfflinePlayer(member.getPlayerId());
						if (op.getName().toLowerCase().startsWith(partial)) {
							matches.add(member);
						}
					}
					if (matches.size() == 0) {
						throw new Exception("No staff member matching '" + args.peek() + "' was found");
					} else if (matches.size() > 1) {
						throw new Exception("Name '" + args.peek() + "' is ambiguous");
					}
					OfflinePlayer match = plugin.getOfflinePlayer(matches.get(0).getPlayerId());
					args.pop();
					if (args.size() == 0) {
						displayStaff(sender, match, matches.get(0), shop);
					} else {
						// There are more args. look for a subcommand.
						StaffMemberSubcommand sub = staffSubcommands.get(args.peek().toLowerCase());
						if (sub.getRequiredPermission() == null || sender.hasPermission(sub.getRequiredPermission())) {
							return sub.onCommand((Player) sender, shop, matches.get(0), command, args.pop(), args);
						} else {
							throw new Exception("You do not have permission to edit staff members");
						}
					}
					return true;
				}
			}

		}
		showHelp(sender);
		return true;
	}

	private void displayStaff(CommandSender sender, OfflinePlayer match, StaffMember staffMember, Shop shop) {
		List<String> lines = new LinkedList<String>();
		lines.add(ChatColor.BOLD + "Staff Member Permissions: " + match.getName());
		for (StaffPermission perm: StaffPermission.values()) {
			if (perm.equals(StaffPermission.OWNER_ONLY)) continue;
			boolean allowed = shop.allow(staffMember, perm);
			lines.add("[" + (allowed ? ChatColor.GREEN : ChatColor.RED) + perm.getDisplay().toUpperCase() + ChatColor.RESET + "] " +
					match.getName() + (allowed ? ChatColor.GREEN + " can " : ChatColor.RED + " cannot ") + ChatColor.RESET + perm.getDescription());
		}
		sender.sendMessage(lines.toArray(new String[0]));
	}

	@Override
	public void showHelp(CommandSender sender) {
		super.showHelp(sender);
		if (sender instanceof Player) {
			Shop shop = plugin.getSelectedShop((Player) sender);
			if (shop != null) {
				List<String> lines = new LinkedList<String>();

				String line = ChatColor.BOLD + "Staff: " + ChatColor.RESET;
				for (StaffMember member:  shop.getStaff()) {
					OfflinePlayer op = plugin.getOfflinePlayer(member.getPlayerId());
					line += op.getName() + ", ";
				}
				if (line.endsWith(", ")) {
					line = line.substring(0, line.length() -2);
				}
				lines.add(line);
				if (line.equals(ChatColor.BOLD + "Staff: " + ChatColor.RESET)) {
					lines.add(ChatColor.ITALIC + "  this shop has no staff");
				}
				sender.sendMessage(lines.toArray(new String[0]));
			}
		}
	}

	public static class RemoveStaffCommand extends SelectedShopCommand {

		protected RemoveStaffCommand(Kiosk plugin) {
			super(plugin, "remove", "Remove a staff member", "kiosk.command.staff.remove", StaffPermission.MANAGE_STAFF, new String[] {"Remove Staff Help"});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			String partial = args.pop().toLowerCase();
			List<String> results = new ArrayList<String>();
			for (Player p: plugin.getServer().getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(partial)) {
					if (shop.isStaffMember(p)) {
						results.add(p.getName());
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

			String name = args.peek();
			List<StaffMember> matches = new ArrayList<StaffMember>();
			OfflinePlayer op;
			for (StaffMember member: shop.getStaff()) {
				op = plugin.getOfflinePlayer(member.getPlayerId());
				if (op.getName().toLowerCase().startsWith(name.toLowerCase())) {
					matches.add(member);
				}
			}

			if (matches.size() == 0) {
				throw new Exception("No staff member matching '" + name + "' was found.");
			} else if (matches.size() > 1) {
				throw new Exception("More than 1 staff member matches '" + name + "'");
			}
			shop.getStaff().remove(matches.get(0));
			plugin.getDatastore().saveShop(shop);
			op = plugin.getOfflinePlayer(matches.get(0).getPlayerId());
			player.sendMessage(op.getName() + " is now a staff member");
			return true;
		}
	}

	public static class AddStaffCommand extends SelectedShopCommand {

		protected AddStaffCommand(Kiosk plugin) {
			super(plugin, "add", "Add a staff member", "kiosk.command.staff.add", StaffPermission.MANAGE_STAFF, new String[] {"Add Staff Help"});
		}

		@Override
		public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
			String partial = args.pop().toLowerCase();
			List<String> results = new ArrayList<String>();
			for (Player p: plugin.getServer().getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(partial)) {
					if (!shop.isStaffMember(p) && !shop.isOwner(p)) {
						results.add(p.getName());
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

			String name = args.peek();
			List<Player> matches = plugin.getServer().matchPlayer(name);
			if (matches.size() == 0) {
				throw new Exception("Unknown player: " + name);
			} else if (matches.size() > 1) {
				throw new Exception("Name '" + name + "' is ambiguous");
			}
			Player p = matches.get(0);
			if (shop.isStaffMember(p)) {
				throw new Exception(p.getName() + " is already a staff member");
			} else if (shop.isOwner(p)) {
				throw new Exception(p.getName() + " is the owner of this shop!");
			}
			StaffMember sm = new StaffMember(p.getUniqueId(), shop.getDefaultPermissions());
			shop.getStaff().add(sm);
			plugin.getDatastore().saveShop(shop);
			player.sendMessage(p.getName() + " is now a staff member for this shop.");
			return true;
		}
	}
}
