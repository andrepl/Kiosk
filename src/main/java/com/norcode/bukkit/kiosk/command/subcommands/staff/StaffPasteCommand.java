package com.norcode.bukkit.kiosk.command.subcommands.staff;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StaffPasteCommand extends SelectedShopCommand {
	protected StaffPasteCommand(Kiosk plugin) {
		super(plugin, "paste", "paste previously copied staff into the selected shop", "kiosk.command.staff.paste", StaffPermission.MANAGE_STAFF, new String[] {"Staff Paste help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		if (!player.hasMetadata("copied-staff")) {
			throw new Exception("You have not copied any shop staff");
		}
		Set<StaffMember> staff = (Set<StaffMember>) player.getMetadata("copied-staff").get(0).value();
		for (StaffMember member: staff) {
			shop.getStaff().add(new StaffMember(member.getPlayerId(), new HashSet<StaffPermission>(member.getPermissions())));
		}
		player.sendMessage(staff.size() + " staff members were added to this shop");
		plugin.getDatastore().saveShop(shop);
		return true;
	}
}
