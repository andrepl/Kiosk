package com.norcode.bukkit.kiosk.command;

import com.norcode.bukkit.kiosk.Kiosk;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * A CommandHandler that is only available to Player's, not from the console.
 *
 */
public abstract class PlayerCommand extends CommandHandler {
	protected PlayerCommand(Kiosk plugin, String name, String description, String requiredPermission, String[] help) {
		super(plugin, name, description, requiredPermission, help);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, LinkedList<String> args) {
		if (sender instanceof Player)
			return onTabComplete((Player) sender, command, label, args);
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, LinkedList<String> args) throws Exception {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_RED + "This command is only available to players.");
			return true;
		}
		// Show Help
		if ("?".equals(args.peek())) {
			showHelp((Player) sender);
			return true;
		}
		return onCommand((Player) sender, command, label, args);
	}

	public abstract List<String> onTabComplete(Player player, Command command, String label, LinkedList<String> args);

	public abstract boolean onCommand(Player player, Command command, String label, LinkedList<String> args) throws Exception;
}
