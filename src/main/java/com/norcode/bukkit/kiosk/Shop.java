package com.norcode.bukkit.kiosk;

import com.norcode.bukkit.kiosk.command.StaffPermission;
import com.norcode.bukkit.kiosk.util.ChatArt;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Shop implements InventoryHolder {

	private Kiosk plugin;
	private UUID id;
	private ChatArt icon;
	private Location location;
	private String name;
	private UUID ownerId;
	private Set<StaffMember> staff = new HashSet<StaffMember>();
	private ShopType type = ShopType.SELLING;
	private Double price = null;
	private Integer quantity = null;
	private EnumSet<StaffPermission> defaultPermissions;
	private boolean _private = true;
	private int stock = 0;
	private double balance = 0.0;
	private String depositAccount;
	private ItemStack item;
	private boolean adminShop = false;
	private Inventory openInventory;
	private boolean inventoryIsReadOnly = false;
	private ChatColor border;

	public static String SIGN_CODE = ChatColor.ITALIC + "" + ChatColor.BOLD + "" + ChatColor.BOLD + "" +
			ChatColor.ITALIC + "" + ChatColor.DARK_PURPLE + "" + ChatColor.RESET + "";


	public Shop(Kiosk plugin) {
		this.plugin = plugin;
		defaultPermissions = StaffPermission.getDefaults();
	}

	public Kiosk getPlugin() {
		return plugin;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public boolean hasIcon() {
		return icon != null;
	}
	public ChatArt getIcon() {
		return icon;
	}

	public void setIcon(ChatArt icon) {
		this.icon = icon;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		if (name == null || name == "") {
			if (isAdminShop()) {
				return "Server Shop";
			} else {
				return getOwnerName() + "'s Shop";
			}
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EnumSet<StaffPermission> getDefaultPermissions() {
		return defaultPermissions;
	}

	public void setDefaultPermissions(EnumSet<StaffPermission> defaultPermissions) {
		this.defaultPermissions = defaultPermissions.clone();
	}

	public OfflinePlayer getOwner() {
		return plugin.getOfflinePlayer(ownerId);
	}

	public String getOwnerName() {
		OfflinePlayer owner = plugin.getOfflinePlayer(ownerId);
		return owner.getName();
	}

	public void setOwnerId(UUID owner) {
		this.ownerId = owner;
	}

	public boolean isOwner(Player player) {
		return this.ownerId.equals(player.getUniqueId());
	}

	public StaffMember getStaffMember(Player player) {
		for (StaffMember sm: staff) {
			if (sm.getPlayerId().equals(player.getUniqueId())) {
				return sm;
			}
		}
		return null;
	}

	public boolean isStaffMember(Player player) {
		return getStaffMember(player) != null;
	}

	public Set<StaffMember> getStaff() {
		return staff;
	}

	public void setStaff(Set<StaffMember> staff) {
		this.staff = staff;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public boolean allow(StaffMember staffMember, StaffPermission permission) {
		if (permission == null) {
			return true;
		}
		if (staffMember == null) {
			return false;
		}

		return staffMember.hasPermission(permission);
	}

	public boolean allow(Player player, StaffPermission permission) {
		if (permission == null) {
			return true;
		}
		if (isOwner(player)) {
			return true;
		} else if (permission == StaffPermission.OWNER_ONLY) {
			return false;
		} else {
			StaffMember staff = getStaffMember(player);
			return allow(staff, permission);
		}
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isPrivate() {
		return _private;
	}

	public void setPrivate(boolean _private) {
		this._private = _private;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public boolean hasMoney(double amount) {
		return isAdminShop() || amount <= getBalance();
	}

	public boolean hasStock(int amount) {
		return isAdminShop() || amount <= getStock();
	}

	public String getDepositAccount() {
		return depositAccount;
	}

	public void setDepositAccount(String depositAccount) {
		this.depositAccount = depositAccount;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public boolean isAdminShop() {
		return adminShop;
	}

	public void setAdminShop(boolean adminShop) {
		this.adminShop = adminShop;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public void updateSign() {
		Sign sign = getSign();
		if (sign == null) return;
		if (getItem() == null || getItem().getType().equals(Material.AIR)
				|| (!isSelling() && !isBuying())
				|| getPrice() == null || getQuantity() == null) {
			sign.setLine(0, "");
			sign.setLine(1, ChatColor.BOLD + "Shop is not");
			sign.setLine(2, ChatColor.BOLD + "Configured");
			sign.setLine(3, SIGN_CODE);
			sign.update();
			return;
		}
		if (isSelling()) {
			sign.setLine(0, ChatColor.GREEN + "" + ChatColor.BOLD + "Selling");
			if (getStock() < getQuantity() && !isAdminShop()) {
				sign.setLine(1, "-Out of Stock-");
				sign.setLine(2, "");
			} else {
				sign.setLine(1, getQuantity() + "/" + plugin.getEconomy().format(getPrice()));
				if (!isAdminShop()) {
					sign.setLine(2, "[" + getStock() + " left]");
				} else {
					sign.setLine(2, "");
				}
			}
		} else if (isBuying()) {
			sign.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "Buying");
			if (getBalance() < getPrice() && !isAdminShop()) {
				sign.setLine(1, "-Out of Funds-");
				sign.setLine(2, "");
			} else {
				sign.setLine(1, getQuantity() + "/" + plugin.getEconomy().format(getPrice()));
				if (!isAdminShop()) {
					sign.setLine(2, "[" + plugin.getEconomy().format(getBalance()) + " left]");
				} else {
					sign.setLine(2, "");
				}
			}
		}
		sign.setLine(3, SIGN_CODE);
		sign.update();
	}

	public boolean isSelling() {
		return this.type == ShopType.SELLING;
	}

	public boolean isBuying() {
		return this.type == ShopType.BUYING;
	}

	public Sign getSign() {
		Location loc = new Location(location.getWorld(), location.getBlockX(), location.getBlockY()-1, location.getBlockZ());
		BlockFace attachedFace = getItemFrame().getAttachedFace();
		if (loc.getBlock().getType().equals(Material.WALL_SIGN)) {
			Sign sign = (Sign) loc.getBlock().getState();
			org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
			if (signData.getAttachedFace() == attachedFace) {
				return sign;
			}
		}
		return null;
	}

	public ItemFrame getItemFrame() {
		for (Entity e: getLocation().getChunk().getEntities()) {
			if (e.getType() == EntityType.ITEM_FRAME) {
				if (e.getUniqueId().equals(this.getId())) {
					return (ItemFrame) e;
				}
			}
		}
		return null;
	}

	public void rightClick(Player player) {

	}

	public void setType(ShopType type) {
		this.type = type;
	}

	public ShopType getType() {
		return type;
	}

	@Override
	public Inventory getInventory() {
		if (this.openInventory == null) {
			this.openInventory = plugin.getServer().createInventory(this, 54, this.getDisplayName());
			int in = getStock();
			int maxStackSize = getItem().getType().getMaxStackSize();
			int stackSize = -1;

			while (in > 0) {
				ItemStack stack = getItem().clone();
				stackSize = maxStackSize > in ? in : maxStackSize;
				stack.setAmount(stackSize);
				HashMap<Integer, ItemStack> remainder = this.openInventory.addItem(stack);
				if (remainder.size() > 0) {
					stackSize -= remainder.get(0).getAmount();
				}
				in -= stackSize;
			}
			return this.openInventory;
		}
		return null;
	}

	public boolean inventoryIsLocked() {
		return this.openInventory != null;
	}

	public void releaseInventory() {
		if (this.openInventory != null) {
			int count = 0;
			for (ItemStack stack: openInventory) {
				if (stack != null) {
					if (!stack.isSimilar(getItem())) {
						this.getLocation().getWorld().dropItem(this.getLocation(), stack);
						continue;
					}
					count += stack.getAmount();
				}
			}

			if (this.inventoryIsReadOnly()) {
				this.inventoryIsReadOnly = false;
			}
			this.stock = count;

			this.openInventory = null;
			plugin.debug("new Stock: " + count);
			updateSign();
		}
	}

	public int getMaxStock() {
		return this.getItem().getMaxStackSize() * 54;
	}

	public String getItemDisplayName() {
		if (getItem() != null && getItem().getType() != Material.AIR) {
			String displayName = "";
			if (getItem().hasItemMeta()) {
				ItemMeta meta = getItem().getItemMeta();
				if (meta.hasDisplayName()) {
					displayName = meta.getDisplayName();
				}
			}
			ItemInfo info = Items.itemByStack(getItem());
			if (displayName.equals("")) {
				displayName = info.getName();
			} else {
				displayName += " (" + info.getName() + ")";
			}
			return displayName;
		}
		return "";
	}

	public Inventory getInventory(boolean addOnly) {
		this.inventoryIsReadOnly = addOnly;
		return getInventory();
	}

	public boolean inventoryIsReadOnly() {
		return inventoryIsReadOnly;
	}

	public void give(Player player, int qty) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		int rem = qty;
		int maxStackSize = getItem().getMaxStackSize();
		int stackSize = maxStackSize;
		ItemStack stack;
		while (qty > 0) {
			if (qty < maxStackSize) {
				stackSize = qty;
			} else {
				stackSize = maxStackSize;
			}
			qty -= stackSize;
			stack = getItem().clone();
			stack.setAmount(stackSize);
			items.add(stack);
		}
		HashMap<Integer, ItemStack> remainder = player.getInventory().addItem(items.toArray(new ItemStack[0]));
		for (Map.Entry<Integer, ItemStack> entry: remainder.entrySet()) {
			player.getWorld().dropItem(player.getLocation(), entry.getValue());
		}
	}


	public void setBorder(ChatColor border) {
		this.border = border;
	}

	public ChatColor getBorder() {
		return border;
	}
}
