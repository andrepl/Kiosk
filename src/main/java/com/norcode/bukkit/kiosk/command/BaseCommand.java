package com.norcode.bukkit.kiosk.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A BaseCommand is a CommandHandler that excusively dispatches to sub-commands
 *
 */
public class BaseCommand extends CommandHandler {
	protected final TreeMap<String, CommandHandler> subCommands = new TreeMap<String, CommandHandler>();

	public BaseCommand(Kiosk plugin, String name, String description, String requiredPermission, String[] help) {
		super(plugin, name, description, requiredPermission, help);
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
			}
			return null;
		} else {
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
		if (!sender.hasPermission(this.getRequiredPermission())) {
			return false;
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
			}
			if (!args.getFirst().equals("?"))
				sender.sendMessage(ChatColor.DARK_RED + "Unknown Subcommand!");
		}
		showHelp(sender);
		return true;
	}

	@Override
	public void showHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "Available Subcommands:");
		for (CommandHandler handler : subCommands.values()) {
			if (handler.getRequiredPermission() == null || sender.hasPermission(handler.getRequiredPermission()))
				sender.sendMessage("  " + ChatColor.DARK_AQUA + handler.getName() + " " + ChatColor.WHITE + " - " + ChatColor.GRAY + handler.getDescription());
		}
	}

	public void registerSubcommand(CommandHandler commandHandler) {
		subCommands.put(commandHandler.getName().toLowerCase(), commandHandler);
	}

	public static boolean parseBoolean(String arg) throws Exception {
		arg = arg.toLowerCase();
		if (arg.equals("1") || arg.equals("true") || arg.equals("yes") || arg.equals("y") || arg.equals("t") || arg.equals("on")) {
			return true;
		} else if (arg.equals("0") || arg.equals("false") || arg.equals("no") || arg.equals("n") || arg.equals("f") || arg.equals("off")) {
			return false;
		}
		throw new Exception("Expecting 'true' or 'false', not '" + arg + "'");
	}
}
