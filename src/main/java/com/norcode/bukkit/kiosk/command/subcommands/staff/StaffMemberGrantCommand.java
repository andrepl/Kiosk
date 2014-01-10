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
import java.util.Set;

public class StaffMemberGrantCommand extends StaffMemberSubcommand {
	protected StaffMemberGrantCommand(Kiosk plugin) {
		super(plugin, "grant", "Grant one or more permissions to the staff member.", "kiosk.command.staff.member.grant", new String[] {"/kiosk staff [staff_user] grant [permission]", "Grants `permission` to `staff_user` for the selected kiosk."});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, StaffMember member, Command command, String label, LinkedList<String> args) {
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
				if (others.contains(perm.getDisplay().toUpperCase()) || !shop.allow(member, perm)) {
					results.add(perm.getDisplay().toUpperCase());
				}
			}
		}
		return results;
	}

	@Override
	public boolean onCommand(Player player, Shop shop, StaffMember member, Command command, String label, LinkedList<String> args) throws Exception {
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
		member.getPermissions().addAll(perms);
		plugin.getStore().saveShop(shop);
		OfflinePlayer staff = plugin.getOfflinePlayer(member.getPlayerId());
		String permStr = "";
		for (StaffPermission p: perms) {
			permStr += p.getDisplay().toUpperCase() + ", ";
		}
		if (permStr.endsWith(", ")) {
			permStr = permStr.substring(0, permStr.length() -2);
		}
		player.sendMessage(staff.getName() + " was granted " + permStr);
		return true;
	}
}
