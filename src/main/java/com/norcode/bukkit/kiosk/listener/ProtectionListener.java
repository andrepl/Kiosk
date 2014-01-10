package com.norcode.bukkit.kiosk.listener;


import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.material.Attachable;

public class ProtectionListener implements Listener {
	Kiosk plugin;

	public ProtectionListener(Kiosk plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		if (event.getBlock().getType() == Material.WALL_SIGN) {
			Attachable attachable = (Attachable) event.getBlock().getState().getData();
			Block wall = event.getBlock().getRelative(attachable.getAttachedFace());
			if (!wall.getType().isSolid()) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
			}
		}
	}


	@EventHandler(ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.WALL_SIGN) {
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) event.getBlock().getState();
			int x = event.getBlock().getX();
			int y = event.getBlock().getY();
			int z = event.getBlock().getZ();
			Location entLoc;
			Shop shop;
			if (sign.getLine(3).equals(Shop.SIGN_CODE)) {
				for (Entity e: event.getBlock().getChunk().getEntities()) {
					if (e.getType() == EntityType.ITEM_FRAME) {
						entLoc = e.getLocation();
						if (entLoc.getBlockY() == y && entLoc.getBlockZ() == z && entLoc.getBlockX() == x) {
							shop = plugin.getStore().getShop(e.getUniqueId());
							if (shop != null) {
								if (!shop.allow(event.getPlayer(), StaffPermission.DISPLAY)) {
									event.setCancelled(true);
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {
		Shop shop = plugin.getStore().getShop(event.getEntity().getUniqueId());
		if (shop != null) {
			event.setCancelled(true);
		}
	}
}
