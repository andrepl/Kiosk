package com.norcode.bukkit.kiosk.command.subcommands.staff;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public abstract class StaffMemberSubcommand extends CommandHandler {

	protected StaffMemberSubcommand(Kiosk plugin, String name, String description, String requiredPermission, String[] help) {
		super(plugin, name, description, requiredPermission, help);
	}

	public abstract List<String> onTabComplete(Player player, Shop shop, StaffMember member, Command command, String label, LinkedList<String> args);
	public abstract boolean onCommand(Player player, Shop shop, StaffMember member, Command command, String label, LinkedList<String> args) throws Exception;
}
