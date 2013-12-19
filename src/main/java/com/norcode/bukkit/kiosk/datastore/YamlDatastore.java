package com.norcode.bukkit.kiosk.datastore;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.ShopType;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import com.norcode.bukkit.kiosk.util.ChatArt;
import com.norcode.bukkit.kiosk.util.Util;
import com.norcode.bukkit.kiosk.util.ConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class YamlDatastore extends Datastore {

	private ConfigAccessor accessor;

	public YamlDatastore(Kiosk plugin) {
		super(plugin);

	}

	@Override
	protected List<Shop> loadShopData() {
		Set<String> keys = accessor.getConfig().getKeys(false);
		List<Shop> shops = new ArrayList<Shop>(keys.size());
		Shop shop;
		ConfigurationSection cfg;
		for (String key: keys) {
			shop = new Shop(plugin);
			cfg = accessor.getConfig().getConfigurationSection(key);
			shop.setId(UUID.fromString(key));
			shop.setAdminShop(cfg.getBoolean("is-admin-shop", false));
			shop.setBalance(cfg.getDouble("balance", 0.0));
			Double price = cfg.getDouble("price", -1);
			shop.setPrice(price == -1 ? null : price);
			Integer qty = cfg.getInt("quantity", -1);
			shop.setType(ShopType.valueOf(cfg.getString("type")));
			if (cfg.contains("permdefaults")) {
				shop.setDefaultPermissions(StaffPermission.fromStringList(cfg.getStringList("permdefaults")));
			}
			shop.setQuantity(qty == -1 ? null : qty);
			shop.setDepositAccount(cfg.getString("deposit-account", null));
			shop.setItem(cfg.getItemStack("item"));
			shop.setOwnerId(UUID.fromString(cfg.getString("owner-id", null)));
			Set<StaffMember> staff = new HashSet<StaffMember>();
			ConfigurationSection staffSection= cfg.getConfigurationSection("staff");
			if (staffSection == null) {
				staffSection = cfg.createSection("staff");
			}

			for (String uuid: staffSection.getKeys(false)) {
				Set<StaffPermission> perms = new HashSet<StaffPermission>();
				for (String permNode: staffSection.getStringList(uuid)) {
					perms.add(StaffPermission.valueOf(permNode));
				}
				staff.add(new StaffMember(UUID.fromString(uuid), perms));
			}
			shop.setStaff(staff);
			shop.setStock(cfg.getInt("stock", 0));
			shop.setPrivate(cfg.getBoolean("private", false));
			shop.setName(cfg.getString("name"));
			shop.setLocation(Util.parseLocation(cfg.getString("location")));
			if (cfg.contains("icon")) {
				shop.setIcon(ChatArt.getByName(cfg.getString("icon")));
			}
			if (cfg.contains("border")) {
				shop.setBorder(ChatColor.valueOf(cfg.getString("border")));
			}
			shops.add(shop);
		}
		return shops;
	}

	@Override
	protected void saveShopData(Shop shop) {
		if (!allShops.containsKey(shop.getId())) {
			allShops.put(shop.getId(), shop);
		}
		ConfigurationSection cfg = accessor.getConfig().getConfigurationSection(shop.getId().toString());
		if (cfg == null) {
			cfg = accessor.getConfig().createSection(shop.getId().toString());
		}
		cfg.set("is-admin-shop", shop.isAdminShop());
		cfg.set("balance", shop.getBalance());
		cfg.set("price", shop.getPrice());
		cfg.set("stock", shop.getStock());
		cfg.set("type", shop.getType().name());
		cfg.set("quantity", shop.getQuantity());
		cfg.set("item", shop.getItem());
		cfg.set("deposit-account", shop.getDepositAccount());
		cfg.set("owner-id", shop.getOwnerId().toString());
		List<String> permdefaults = new ArrayList<String>();
		for (StaffPermission p: shop.getDefaultPermissions()) {
			permdefaults.add(p.getDisplay().toUpperCase());
		}
		cfg.set("permdefaults", permdefaults);
		if (shop.hasIcon()) {
			cfg.set("icon", shop.getIcon().getName());
		}
		cfg.set("border", shop.getBorder());
		cfg.set("staff", null);
		ConfigurationSection staffSection = cfg.createSection("staff");
		for (StaffMember sm: shop.getStaff()) {
			List<String> perms = new ArrayList<String>();
			for (StaffPermission node: sm.getPermissions()) {
				perms.add(node.name());
			}
			staffSection.set(sm.getPlayerId().toString(), perms);
		}
		cfg.set("name", shop.getName());
		cfg.set("private", shop.isPrivate());
		cfg.set("location", Util.serializeLocation(shop.getLocation()));
	}

	@Override
	protected void deleteShopData(UUID id) {
		accessor.getConfig().set(id.toString(), null);
		accessor.saveConfig();
	}

	@Override
	public void onDisable() {
		accessor.saveConfig();
	}

	@Override
	public void onEnable() {
		accessor = new ConfigAccessor(plugin, "shops.yml");
	}
}
