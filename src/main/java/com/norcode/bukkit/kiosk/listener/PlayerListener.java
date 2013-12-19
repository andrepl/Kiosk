package com.norcode.bukkit.kiosk.listener;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;

import com.norcode.bukkit.kiosk.command.StaffPermission;
import com.norcode.bukkit.kiosk.command.subcommands.InfoCommand;
import com.norcode.bukkit.kiosk.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Sign;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerListener implements Listener {
	private Kiosk plugin;
	public PlayerListener(Kiosk plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.playerID.registerPlayer(event.getPlayer().getUniqueId(), event.getPlayer());
	}

	@EventHandler(ignoreCancelled=true)
	public void onFrameDeath(EntityDamageEvent event) {
		if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
			Shop shop = plugin.getDatastore().getShop(event.getEntity().getUniqueId());
			if (shop != null) {
				if (event instanceof EntityDamageByEntityEvent) {
					Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
					if (damager instanceof Player) {
						Player player = (Player) damager;
						if (player.hasPermission("kiosk.select")) {
							Util.send(player, InfoCommand.renderShopInfo(plugin, player, shop).toArray(new Object[0]));
							plugin.setSelectedShop(player, shop);
						} else {
							player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use Kiosk");
						}
					}
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerInteractFrame(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
			ItemFrame frame = (ItemFrame) event.getRightClicked();
			Shop shop = plugin.getDatastore().getShop((ItemFrame) event.getRightClicked());
			if (shop == null) {
				return;
			}

			if (frame.getItem() == null || frame.getItem().getType().equals(Material.AIR)) {
				// the frame is empty, check if they're inserting an item.
				if (event.getPlayer().getItemInHand() != null) {
					// Cancel the event so we don't take the item away from the player.
					event.setCancelled(true);
					if (shop.allow(event.getPlayer(), StaffPermission.CHANGE_ITEM)) {
						frame.setItem(event.getPlayer().getItemInHand().clone());
						shop.setItem(frame.getItem());
						plugin.getDatastore().saveShop(shop);
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to set the item in this shop.");
					}
				}
			} else {
				// if sneaking, rotate, else cancel as 'selected'
				if (event.getPlayer().isSneaking() && shop.allow(event.getPlayer(), StaffPermission.DISPLAY)) {
					// allow the event to pass through, rotating the item.
				} else {
					event.setCancelled(true);
				}
			}
			if (event.getPlayer().hasPermission("kiosk.select")) {
				plugin.setSelectedShop(event.getPlayer(), shop);
				shop.updateSign();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() != null && event.getItem().getType() == Material.ITEM_FRAME) {
				if (event.getItem().hasItemMeta()) {
					ItemMeta meta = event.getItem().getItemMeta();
					if (meta.hasLore() && meta.getLore().get(0).equals(Kiosk.LORE_1)) {
						event.getPlayer().setMetadata("placed-kiosk", new FixedMetadataValue(plugin, true));
						return;
					}
				}
				event.getPlayer().removeMetadata("placed-kiosk", plugin);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPlaceFrame(HangingPlaceEvent event) {
		if (event.getPlayer().hasMetadata("placed-kiosk")) {
			event.getPlayer().removeMetadata("placed-kiosk", plugin);
			if (!event.getPlayer().hasPermission("kiosk.place")) {
				event.setCancelled(true);
				return;
			}
			ItemFrame frame = (ItemFrame) event.getEntity();
			final Shop shop = plugin.getDatastore().createShop(frame);
			shop.setOwnerId(event.getPlayer().getUniqueId());
			plugin.getDatastore().saveShop(shop);
			Block below = frame.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (below == null || below.getType().equals(Material.AIR)) {
				if (below.getRelative(frame.getAttachedFace()).getType().isSolid()) {
					Block wall = below.getRelative(frame.getAttachedFace());
					BlockState bs = below.getState();
					bs.setType(Material.WALL_SIGN);
					Sign data = new Sign(Material.WALL_SIGN);
					data.setFacingDirection(frame.getAttachedFace().getOppositeFace());
					bs.setData(data);
					bs.update(true, true);
					plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							shop.updateSign();
						}
					}, 0);
				}
			}
		}
	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent event) {
		plugin.debug("Crafting");
		if (event.getRecipe().getResult().equals(plugin.getRecipe().getResult())) {
			plugin.debug("Crafting Kiosk");
			if (!plugin.getConfig().getBoolean("allow-crafting")) {
				plugin.debug(" Disabled in config.");
				event.getInventory().setResult(new ItemStack(Material.AIR));
			} else {
				plugin.debug(" Enabled in config.");
			}
			HumanEntity player = event.getInventory().getViewers().get(0);
			plugin.debug("Crafter is " + player);
			if (player instanceof Player) {
				plugin.debug("Crafter is a  player");
				if (!(((Player) player).hasPermission("kiosk.craft"))) {
					plugin.debug("Denied by perms");
					event.getInventory().setResult(new ItemStack(Material.AIR));
				} else {
					plugin.debug("Permitted by perms");
				}
			}
		}
	}
}
