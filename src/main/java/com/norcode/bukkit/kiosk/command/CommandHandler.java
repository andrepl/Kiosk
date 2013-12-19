package com.norcode.bukkit.kiosk.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.norcode.bukkit.kiosk.Kiosk;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 * A CommandHandler is a thin wrapper around Bukkit's TabExecutor to encapsulate basic permission and help messages.
 *
 */
public abstract class CommandHandler implements TabExecutor {
	protected Kiosk plugin;
	private final String name;
	private final String description;
	private final String[] help;
	private final String requiredPermission;

	protected CommandHandler(Kiosk plugin, String name, String description, String requiredPermission, String[] help) {
		this.plugin = plugin;
		this.name = name;
		this.description = description;
		this.help = help;
		this.requiredPermission = requiredPermission;
	}

	public void showHelp(CommandSender sender) {
		if (getHelp() != null)
			sender.sendMessage(getHelp());
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String[] getHelp() {
		return help;
	}

	public String getRequiredPermission() {
		return requiredPermission;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
		try {
			return onCommand(commandSender, command, label, new LinkedList<String>(Arrays.asList(args)));
		} catch (Exception e) {
			if (e.getMessage() == null || e.getMessage().equals(""))
				commandSender.sendMessage(ChatColor.DARK_RED + "An unknown error has happened!");
			else
				commandSender.sendMessage(ChatColor.DARK_RED + e.getMessage());
			if (plugin.getConfig().getBoolean("show-stacktraces", false))
				e.printStackTrace();
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
		return onTabComplete(commandSender, command, s, new LinkedList<String>(Arrays.asList(strings)));
	}

	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, LinkedList<String> args) {
		return null;
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, LinkedList<String> args) throws Exception {
		return false;
	}
}
