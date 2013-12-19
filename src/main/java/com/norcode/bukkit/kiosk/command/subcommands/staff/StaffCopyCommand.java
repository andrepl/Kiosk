package com.norcode.bukkit.kiosk.command.subcommands.staff;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StaffCopyCommand extends SelectedShopCommand {
	protected StaffCopyCommand(Kiosk plugin) {
		super(plugin, "copy", "copy all staff members from this shop", "kiosk.command.staff.copy", StaffPermission.MANAGE_STAFF, new String[] {"Staff Copy Help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		Set<StaffMember> staff = new HashSet<StaffMember>();
		for (StaffMember member: shop.getStaff()) {
			staff.add(new StaffMember(member.getPlayerId(), new HashSet<StaffPermission>(member.getPermissions())));
		}
		player.setMetadata("copied-staff", new FixedMetadataValue(plugin, staff));
		player.sendMessage("All staff have been copied from this shop. Select another shop and type '/kiosk staff paste' to paste them into another shop.");
		return true;
	}
}
