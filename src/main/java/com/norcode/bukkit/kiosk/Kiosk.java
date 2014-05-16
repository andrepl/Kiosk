package com.norcode.bukkit.kiosk;

import com.norcode.bukkit.kiosk.command.KioskCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import com.norcode.bukkit.kiosk.datastore.Datastore;
import com.norcode.bukkit.kiosk.datastore.YamlDatastore;
import com.norcode.bukkit.kiosk.listener.InventoryListener;
import com.norcode.bukkit.kiosk.listener.PlayerListener;
import com.norcode.bukkit.kiosk.listener.ProtectionListener;
import com.norcode.bukkit.kiosk.util.Lang;
import com.norcode.bukkit.kiosk.util.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Kiosk extends JavaPlugin {

	private PlayerListener playerListener;
	public static String LORE_1 = ChatColor.ITALIC + "" + ChatColor.GOLD + "Place this frame to open your own shop!";
	public static String DISPLAY_NAME = "Kiosk";
	private Datastore datastore;
	private boolean debugMode = true;
	private Economy economy;
	private InventoryListener inventoryListener;
	private ProtectionListener protectionListener;
	private ShapedRecipe craftingRecipe;

	public void onEnable() {
        super.onEnable();
		saveDefaultConfig();
		if (setupEconomy()) {
			loadRecipe();
			initializeLanguage();
			this.playerListener = new PlayerListener(this);
			this.inventoryListener = new InventoryListener(this);
			this.protectionListener = new ProtectionListener(this);
			getCommand("kiosk").setExecutor(new KioskCommand(this));
			StaffPermission.setDefaults(getConfig().getStringList("default-staff-permissions"));
			this.datastore = new YamlDatastore(this);
			this.datastore.initialize();
		} else {
			getLogger().info("Couldn't find vault!");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private void initializeLanguage() {
		try {
			Lang.initialize(this);
		} catch (IOException e) {
			getLogger().severe(getConfig().getString("language") + ".lang not found, disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private void loadRecipe() {
		// remove the existing recipe if it exists.
		Iterator<Recipe> it = getServer().recipeIterator();
		while (it.hasNext()) {
			Recipe r = it.next();
			if (r.getResult().equals(getNewShopFrame(1))) {
				it.remove();
				break;
			}
		}

		if (!getConfig().getBoolean("allow-crafting", false)) {
			return;
		}

		String[] shape = getConfig().getStringList("recipe.shape").toArray(new String[0]);
		debug("Recipe Shape:");
		for (String s: shape) {
			debug("  " + s);
		}
		HashMap<Character, MaterialData> legend = new HashMap<Character, MaterialData>();
		ConfigurationSection legendSection = getConfig().getConfigurationSection("recipe.legend");
		debug("Recipe Legend:");
		for (String key: legendSection.getKeys(false)) {
			String val = legendSection.getString(key);
			legend.put(key.charAt(0), Util.parseRecipeItem(val));
		}

		craftingRecipe = new ShapedRecipe(getNewShopFrame(1)).shape(shape);
		for (Map.Entry<Character, MaterialData> entry: legend.entrySet()) {
			craftingRecipe.setIngredient(entry.getKey(), entry.getValue());
		}
		getServer().addRecipe(craftingRecipe);
	}

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	public OfflinePlayer getOfflinePlayer(UUID id) {
		return getServer().getOfflinePlayer(id);
	}

	public ItemStack getNewShopFrame(int qty) {
		ItemStack stack = new ItemStack(Material.ITEM_FRAME, qty);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(DISPLAY_NAME);
		List<String> lore = new ArrayList<String>();
		lore.add(LORE_1);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public void debug(String s) {
		if (debugMode) {
			getLogger().info(s);
		}
	}

	public void setSelectedShop(Player player, Shop shop) {
		player.setMetadata("selected-shop", new FixedMetadataValue(this, shop.getId()));
	}

	public Shop getSelectedShop(Player player) {
		if (player.hasMetadata("selected-shop")) {
			UUID uuid = UUID.fromString(player.getMetadata("selected-shop").get(0).asString());
			return getStore().getShop(uuid);
		}
		return null;
	}

	public Datastore getStore() {
		return datastore;
	}

	@Override
	public void onDisable() {
        super.onDisable();
		getStore().disable();
	}

	public Economy getEconomy() {
		return economy;
	}

	public ShapedRecipe getRecipe() {
		return craftingRecipe;
	}

}
