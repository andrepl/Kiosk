package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.ShopType;
import com.norcode.bukkit.kiosk.StaffMember;
import com.norcode.bukkit.kiosk.command.SelectedShopCommand;
import com.norcode.bukkit.kiosk.command.StaffPermission;
import com.norcode.bukkit.kiosk.util.Util;
import com.norcode.bukkit.kiosk.util.chat.Button;
import com.norcode.bukkit.kiosk.util.chat.Text;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfoCommand extends SelectedShopCommand {

	public InfoCommand(Kiosk plugin) {
		super(plugin, "info", "Display information about the selected shop", "kiosk.command.info", null, new String[] {"info help"});
	}

	@Override
	public List<String> onTabComplete(Player player, Shop shop, Command command, String label, LinkedList<String> args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(Player player, Shop shop, Command command, String label, LinkedList<String> args) throws Exception {
		List<IChatBaseComponent> lines = renderShopInfo(plugin, player, shop);
		Util.send(player, lines);
		return true;
	}

	public static List<IChatBaseComponent> renderShopInfo(Kiosk plugin, Player player, Shop shop) {

		List<IChatBaseComponent> lines = new LinkedList<IChatBaseComponent>();
		int lineLength = 59 - (shop.hasIcon() ? shop.getIcon().getMaxWidth() : 0);
		lines.add(new Text(ChatColor.DARK_GRAY + " ╭" + StringUtils.repeat("╌", lineLength)));
		Text shopTitle = new Text(ChatColor.DARK_GRAY + " ┊    ").append(new Text(ChatColor.DARK_AQUA + "༺ " + ChatColor.GOLD + shop.getDisplayName() + ChatColor.DARK_AQUA + " ༻").setBold(true));
		lines.add(shopTitle);
		String action = (shop.isSelling() ? ChatColor.GREEN : ChatColor.RED) + shop.getType().name() + ChatColor.RESET;
		Text itemInfo = new Text("").appendItem(shop.getItem());
		lines.add(new Text(ChatColor.DARK_GRAY + " ┊ ").append(new Text(action + " " + (shop.getQuantity() == null ? ChatColor.RESET + "" + ChatColor.ITALIC + "(no qty set) " : shop.getQuantity() +"x ")).append(itemInfo)));
		if (shop.getItem().getType().getMaxDurability() > 0) {
			lines.add(new Text(ChatColor.DARK_GRAY + " ┊ ").append("Durability: " + ChatColor.RESET + Util.renderDurability(shop.getItem())));
		}
		lines.add(new Text(ChatColor.DARK_GRAY + " ┊" + StringUtils.repeat("╌", lineLength)));
		String statusLine = ChatColor.DARK_GRAY + " ┊ ";
		if (shop.isSelling() || shop.isOwner(player) || shop.isStaffMember(player)) {
			statusLine += ChatColor.WHITE + "Stock: " +
				(shop.isAdminShop() ? ChatColor.GOLD + "\u221E" : ChatColor.RESET + "" + ChatColor.GOLD + shop.getStock());
		}

		if (shop.isBuying() || shop.isOwner(player) || shop.isStaffMember(player)) {
			if (!statusLine.equals("")) {
				statusLine += " " + ChatColor.DARK_GRAY + "/" + ChatColor.RESET + " ";
			}
			statusLine += "Funds: " +
				(shop.isAdminShop() ? ChatColor.GOLD + "\u221E" : ChatColor.RESET + "" + ChatColor.GOLD + plugin.getEconomy().format(shop.getBalance()));
		}

		lines.add(new Text(statusLine));
		boolean isOnline = shop.getOwner().isOnline();
		Text staffList = new Text("")
				.append(new Text(shop.getOwnerName())
						.setBold(true)
						.setColor(ChatColor.GOLD)
						.setHoverText(ChatColor.GOLD + "OWNER  " + ChatColor.WHITE + "[" +
								(isOnline ? ChatColor.GREEN + "" + ChatColor.BOLD + "ONLINE" : ChatColor.DARK_RED + "" + ChatColor.BOLD + "OFFLINE") +
								ChatColor.WHITE + "]"));

		for (StaffMember m: shop.getStaff()) {
			OfflinePlayer op = plugin.getOfflinePlayer(m.getPlayerId());
			isOnline = op.isOnline();
			staffList.append(new Text(", ").setColor(ChatColor.DARK_AQUA),
							 new Text(op.getName()).setHoverText(ChatColor.DARK_AQUA + "STAFF  " + ChatColor.WHITE + "[" +
							 (isOnline ? ChatColor.GREEN + "" + ChatColor.BOLD + "ONLINE" : ChatColor.DARK_RED + "" + ChatColor.BOLD + "OFFLINE") +
							 ChatColor.WHITE + "]"));

		}
		lines.add(new Text(ChatColor.DARK_GRAY + " ┊ ").append(new Text("Staff: "), staffList));

		lines.add(new Text(ChatColor.DARK_GRAY + " ┊" + StringUtils.repeat("╌", lineLength)));

		Text buttons = new Text(ChatColor.DARK_GRAY + " ┊ ");
		/* show BUY or SELL button */
		if (shop.getItem() != null && shop.getQuantity() != null && shop.getPrice() != null) {
			if (!shop.isPrivate() || shop.isStaffMember(player) || shop.isOwner(player)) {
				if (shop.hasStock(shop.getQuantity()) && shop.getType() == ShopType.SELLING) {
					buttons.append(Button.buy(shop));
					buttons.append(" ");
				} else if (shop.hasMoney(shop.getPrice()) && shop.getType() == ShopType.BUYING) {
					buttons.append(Button.sell(shop));
					buttons.append(" ");
				}
			}
		}

		/* Show stock */
		if (shop.allow(player, StaffPermission.ADD_STOCK) || shop.allow(player, StaffPermission.MANAGE_STOCK)) {
			buttons.append(Button.stock(shop));
			buttons.append(" ");
		}

		/* show teleport button */
		if (player.hasPermission("kiosk.command.teleport")) {
			if (!shop.isPrivate() || shop.isStaffMember(player) || shop.isOwner(player)) {
				buttons.append(Button.teleport(shop));
				buttons.append(" ");
			}
		}
		lines.add(buttons);
		lines.add(new Text(ChatColor.DARK_GRAY + " ╰" + StringUtils.repeat("╌", lineLength)));
		if (shop.hasIcon()) {
			return shop.getIcon().formatMessage(lines.toArray(new IChatBaseComponent[0]));
		} else {
			return lines;
		}
	}

	public static Collection<? extends String> renderItemInfo(ItemStack stack) {
		ItemMeta _meta = stack.getItemMeta();
		List<String> lines = new ArrayList<String>();
		if (_meta instanceof EnchantmentStorageMeta) {
			lines.add(ChatColor.BOLD + "Stored Enchantments:");
			EnchantmentStorageMeta meta = ((EnchantmentStorageMeta) _meta);
			for (Map.Entry<Enchantment, Integer> ench: meta.getStoredEnchants().entrySet()) {
				lines.add("  " + Util.getEnchantmentDisplayName(ench));
			}
		} else if (_meta instanceof SkullMeta) {
			SkullMeta meta = ((SkullMeta) _meta);
			lines.add(ChatColor.BOLD + "Head of " + meta.getOwner());
		} else if (_meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) _meta;
			Color color = meta.getColor();
			String hexCode = Util.colorToHex(color);
			ChatColor ccc = Util.closestChatColor(color);
			lines.add(ccc + "" + ChatColor.ITALIC + "Dyed " + WordUtils.capitalizeFully(ccc.name().replace("_", " ")) + " (" + hexCode + ")");
		} else if (_meta instanceof PotionMeta) {
			PotionMeta meta = (PotionMeta) _meta;
			if (meta.hasCustomEffects()) {
				lines.add(ChatColor.BOLD + "Custom Effects:");
				for (PotionEffect effect: meta.getCustomEffects()) {
					lines.add(Util.getPotionEffectDisplayName(effect));
				}
			}
		} else if (_meta instanceof BookMeta) {
			BookMeta meta = (BookMeta) _meta;
			if (meta.hasTitle()) {
				lines.add(ChatColor.BOLD + "Title: " + ChatColor.RESET + meta.getTitle());
				if (meta.hasAuthor()) {
					lines.add(ChatColor.BOLD + "Author: " + ChatColor.RESET + meta.getAuthor());
				} else {
				 	lines.add(ChatColor.BOLD + "Author: " + ChatColor.RESET + "~anonymous");
				}
			} else {
				lines.add(ChatColor.ITALIC + "~unsigned~");
			}
			if (meta.hasPages()) {
				lines.add("(" + meta.getPageCount() + " pages)");
			}
		} else if (_meta instanceof MapMeta) {
			MapMeta meta = (MapMeta) _meta;

		} else if (_meta instanceof FireworkEffectMeta) {
			FireworkEffectMeta meta = (FireworkEffectMeta) _meta;
			if (meta.hasEffect()) {
				lines.addAll(renderFireworkEffect(meta.getEffect()));
			}
		} else if (_meta instanceof FireworkMeta) {
			FireworkMeta meta = (FireworkMeta) _meta;
			lines.add(ChatColor.BOLD + "Flight Duration: " + meta.getPower());
			if (meta.hasEffects()) {
				int count = 1;
				for (FireworkEffect effect: meta.getEffects()) {
					lines.add(ChatColor.BOLD + "Effect #"  + count + ":");
					lines.addAll(renderFireworkEffect(effect));
					count ++;
				}
			}
		}
		if (_meta.hasEnchants()) {
			lines.add(ChatColor.BOLD + "Enchantments:");
			for (Map.Entry<Enchantment, Integer> ench: _meta.getEnchants().entrySet()) {
				lines.add("  " + Util.getEnchantmentDisplayName(ench));
			}
		}

		return lines;
	}

	public static List<String> renderFireworkEffect(FireworkEffect effect) {
		List<String> lines = new ArrayList<String>();
		String type = WordUtils.capitalizeFully(effect.getType().name().replace("_", " "));
		List<String> extras = new ArrayList<String>();
		if (effect.hasFlicker()) {
			extras.add("Flicker");
		}
		if (effect.hasTrail()) {
			extras.add("Trail");
		}
		if (extras.size() > 0) {
			type += " (" + extras.get(0);
			if (extras.size() == 2) {
				type += ", " + extras.get(1);
			}
			type += ")";
		}

		lines.add("  Type: " + type);
		String colorList = "";
		for (Color c: effect.getColors()) {
			colorList += Util.closestChatColor(c) + Util.colorToHex(c) + ", ";
		}
		if (colorList.endsWith(", ")) {
			colorList = colorList.substring(0, colorList.length()-2);
			lines.add("  Colors: " + colorList);
		}
		colorList = "";
		for (Color c: effect.getFadeColors()) {
			colorList += Util.closestChatColor(c) + Util.colorToHex(c) + ", ";
		}
		if (colorList.endsWith(", ")) {
			colorList = colorList.substring(0, colorList.length()-2);
			lines.add("  Fade: " + colorList);
		}
		return lines;
	}
}
