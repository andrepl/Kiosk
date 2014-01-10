package com.norcode.bukkit.kiosk.command.subcommands.staff;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class StaffMemberCopyCommand extends StaffMemberSubcommand {

	protected StaffMemberCopyCommand(Kiosk plugin) {
		super(plugin, "copy", "Copy permissions from one staff member to another.", "kiosk.commands.staff.member.copy", new String[] {"/kiosk staff [from_staff_user] copy [to_staff_user]", "Copies all permissions for the selected shop from `from_staff_user` to `to_staff_user`."});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, StaffMember member, Command command, String label, LinkedList<String> args) {
		String partial = args.peek().toLowerCase();
		List<String> results = new ArrayList<String>();
		for (StaffMember m: shop.getStaff()) {
			if (m.getPlayerId().equals(member.getPlayerId())) continue;
			OfflinePlayer op = plugin.getOfflinePlayer(m.getPlayerId());
			if (op.getName().toLowerCase().startsWith(partial)) {
				results.add(op.getName());
			}
		}
		return results;
	}

	@Override
	public boolean onCommand(Player player, Shop shop, StaffMember member, Command command, String label, LinkedList<String> args) throws Exception {
		List<StaffMember> matches = new LinkedList<StaffMember>();
		String partial = args.peek().toLowerCase();
		for (StaffMember m: shop.getStaff()) {
			if (m.getPlayerId().equals(member.getPlayerId())) continue;
			OfflinePlayer op = plugin.getOfflinePlayer(m.getPlayerId());
			if (op.getName().toLowerCase().startsWith(partial)) {
				matches.add(m);
			}
		}
		if (matches.size() == 0) {
			throw new Exception("Unknown staff member: " + args.peek());
		} else if (matches.size() > 1) {
			throw new Exception("'" + args.peek() + "' is ambiguous");
		}
		OfflinePlayer op = plugin.getOfflinePlayer(matches.get(0).getPlayerId());
		member.setPermissions(new HashSet<StaffPermission>(matches.get(0).getPermissions()));
		plugin.getStore().saveShop(shop);
		OfflinePlayer target = plugin.getOfflinePlayer(member.getPlayerId());
		player.sendMessage(target.getName() + " copied all permissions from " + op.getName());
		return true;
	}
}
