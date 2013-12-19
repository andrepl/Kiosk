package com.norcode.bukkit.kiosk.listener;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {
	private Kiosk plugin;

	public InventoryListener(Kiosk plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof Shop) {
			((Shop) event.getInventory().getHolder()).releaseInventory();
			plugin.getDatastore().saveShop((Shop) event.getInventory().getHolder());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof Shop) {
			Shop shop = (Shop) event.getInventory().getHolder();
			if (shop.inventoryIsReadOnly()) {
				boolean clickedTop = (event.getRawSlot() < 54);
				switch(event.getAction()) {
				case MOVE_TO_OTHER_INVENTORY:
					if (clickedTop) {
						event.setCancelled(true);
						return;
					}
					break;
				case COLLECT_TO_CURSOR:
					event.setCancelled(true);
					return;
				case PICKUP_ALL:
				case PICKUP_HALF:
				case PICKUP_ONE:
				case PICKUP_SOME:
					if (clickedTop) {
						event.setCancelled(true);
						return;
					}
					break;
				case HOTBAR_SWAP:
					if (clickedTop) {
						event.setCancelled(true);
						return;
					}
					break;
				case DROP_ONE_SLOT:
				case DROP_ALL_SLOT:
					if (clickedTop) {
						event.setCancelled(true);
						return;
					}
					break;
				}
			}
		}
	}
}
