package com.norcode.bukkit.kiosk.datastore;


import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.ShopType;
import com.norcode.bukkit.kiosk.command.subcommands.FindCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class Datastore {

	Kiosk plugin;
	protected HashMap<UUID, Shop> allShops = new HashMap<UUID, Shop>();
	protected HashMap<UUID, Set<UUID>> idsByOwner = new HashMap<UUID, Set<UUID>>();
	private SearchRunner searchRunner;

	Datastore(Kiosk plugin) {
		this.plugin = plugin;
	}

	public Shop getShop(UUID id) {
		return allShops.get(id);
	}

	public Shop getShop(ItemFrame frame) {
		return allShops.get(frame.getUniqueId());
	}

	public Shop createShop(ItemFrame frame) {
		Shop shop = new Shop(plugin);
		shop.setId(frame.getUniqueId());
		shop.setLocation(frame.getLocation());
		shop.setType(ShopType.SELLING);
		return shop;
	}

	public final void initialize() {
		this.onEnable();
		for (Shop shop: this.loadShopData()) {
			allShops.put(shop.getId(), shop);
			Set<UUID> shopIds = idsByOwner.get(shop.getOwnerId());
			if (shopIds == null) {
				shopIds = new HashSet<UUID>();
				idsByOwner.put(shop.getOwnerId(), shopIds);
			}
			shopIds.add(shop.getId());
		}
		plugin.debug("Loaded " + allShops.size() + " shops.");
		this.searchRunner = new SearchRunner(plugin);
	}

	public void saveShop(Shop shop) {
		if (!allShops.containsKey(shop.getId())) {
			allShops.put(shop.getId(), shop);
			Set<UUID> shopIds = idsByOwner.get(shop.getOwnerId());
			if (shopIds == null) {
				shopIds = new HashSet<UUID>();
				idsByOwner.put(shop.getOwnerId(), shopIds);
			}
			shopIds.add(shop.getId());
		}
		idsByOwner.get(shop.getOwnerId()).remove(shop.getId());
		saveShopData(shop);
	}

	public void transferOwnership(Shop shop, UUID newOwnerId) {
		idsByOwner.get(shop.getOwnerId()).remove(shop.getId());
		if (idsByOwner.isEmpty()) {
			idsByOwner.remove(shop.getOwnerId());
		}
		shop.setOwnerId(newOwnerId);
		Set<UUID> shopIds = idsByOwner.get(newOwnerId);
		if (shopIds == null) {
			shopIds = new HashSet<UUID>();
			idsByOwner.put(newOwnerId, shopIds);
		}
		shopIds.add(shop.getId());
		saveShopData(shop);
	}

	public void deleteShop(UUID id) {
		Shop shop = allShops.remove(id);
		if (shop != null) {
			idsByOwner.get(shop.getOwnerId()).remove(shop.getId());
			if (idsByOwner.get(shop.getOwnerId()).isEmpty()) {
				idsByOwner.remove(shop.getOwnerId());
			}
			deleteShopData(id);
		}
	}

	public void disable() {
		this.searchRunner.cancel();
		this.onDisable();
	}

	protected abstract List<Shop> loadShopData();
	protected abstract void saveShopData(Shop shop);
	protected abstract void deleteShopData(UUID id);

	protected abstract void onDisable();
	protected abstract void onEnable();

	public Set<UUID> allShopIds() {
		return new HashSet<UUID>(allShops.keySet());
	}

	// Perform a search and return the results to the command sender.
	public void search(ShopType type, HashMap<FindCommand.SearchField, LinkedList<String>> searchCriteria, String searchString, CommandSender sender) {
		SearchTask task = new SearchTask(plugin, type, allShopIds(), searchCriteria, searchString, sender);
		if (sender instanceof Player) {
			((Player) sender).setMetadata("kiosk-searchtask", new FixedMetadataValue(plugin, task));
		}
		searchRunner.add(task);
	}
}
