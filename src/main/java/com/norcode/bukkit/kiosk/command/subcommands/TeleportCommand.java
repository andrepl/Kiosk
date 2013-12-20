package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TeleportCommand extends SelectedShopCommand {
	
	public TeleportCommand(Kiosk plugin) {
		super(plugin, "teleport", "Teleport to the selected kiosk.", "kiosk.command.teleport", null, new String[] {"/kiosk teleport <id>", "Teleports the user to shop `id`."});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
	    return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		if (shop.isPrivate() && !(shop.isOwner(player) || shop.isStaffMember(player))) {
			throw new Exception("This shop is private.");
		}

		BlockFace direction = shop.getItemFrame().getAttachedFace();

		Block tpBlock = shop.getLocation().getBlock().getRelative(BlockFace.DOWN)
				.getRelative(direction.getOppositeFace())
				.getRelative(direction.getOppositeFace())
				.getRelative(direction.getOppositeFace());

		Location shopBlockLoc = shop.getItemFrame().getLocation().getBlock().getLocation();
		Block finalDest = null;
		while (tpBlock.getX() != shopBlockLoc.getBlockX() || tpBlock.getZ() != shopBlockLoc.getBlockZ()) {
			tpBlock = tpBlock.getRelative(direction);

			if (tpBlock.getType().isSolid() || tpBlock.getRelative(BlockFace.UP).getType().isSolid()) {
				continue;
			} else {
				finalDest = tpBlock;
				break;
			}
		}
		if (finalDest == null) {
			throw new Exception("Couldn't find a safe spot to teleport.");
		}

		float yaw = 0;
		switch (direction) {
			case NORTH:
				yaw = 179;
				break;
			case EAST:
				yaw = -90;
				break;
			case SOUTH:
				yaw = 0;
				break;
			case WEST:
				yaw =  89;
		}

		Location dest = finalDest.getLocation().add(0.5, 0.01, 0.5);
		dest.setYaw(yaw);
		dest.setPitch(0);
		player.teleport(dest);
		return true;
	}
}
