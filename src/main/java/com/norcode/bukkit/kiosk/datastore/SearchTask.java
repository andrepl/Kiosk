package com.norcode.bukkit.kiosk.datastore;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.ShopType;
import com.norcode.bukkit.kiosk.command.subcommands.FindCommand;
import com.norcode.bukkit.kiosk.util.Util;
import com.norcode.bukkit.kiosk.util.chat.Text;
import com.norcode.bukkit.kiosk.util.chat.Trans;
import com.norcode.bukkit.kiosk.util.chat.ClickAction;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SearchTask {

	private String searchString;
	private boolean cancelled = false;
	int PER_PAGE = 8;

	public boolean isFinished() {
		return cancelled || !this.shopIds.hasNext();
	}

	public void onComplete() {
		showResults(1);
	}

	public List<Shop> getResults() {
		return results;
	}

	private IChatBaseComponent getPrevLink(int page, int pageCount) {
		boolean active = page > 1;
		if (!active) {
			return new Text(ChatColor.DARK_GRAY+ "" + ChatColor.BOLD + "<<").setHoverText("You are already viewing the first page");
		} else {
			return new Text(ChatColor.GOLD + "" + ChatColor.BOLD + "<<").setHoverText("Go to page " + (page-1)).setClick(ClickAction.RUN_COMMAND, "/kiosk results page " + (page-1));
		}
	}

	private IChatBaseComponent getNextLink(int page, int pageCount) {
		boolean active = page < pageCount;
		if (!active) {
			return new Text(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">>").setHoverText("You are already viewing the last page");
		} else {
			return new Text(ChatColor.GOLD + "" + ChatColor.BOLD + ">>").setHoverText("Go to page " + (page+1)).setClick(ClickAction.RUN_COMMAND, "/kiosk results page " + (page+1));
		}
	}

	private IChatBaseComponent getPageInfo(int page, int pageCount) {
		return new Trans("book.pageIndicator", page, pageCount).setColor(ChatColor.DARK_AQUA);
	}

	public void showResults(int page) {
		LinkedList<String> lines = new LinkedList<String>();
		int startIdx = (page - 1) * PER_PAGE;
		int pageCount = ((results.size() / PER_PAGE) + (results.size() % PER_PAGE == 0 ? 0 : 1));
		IChatBaseComponent pages = new Text("")
				.append(new Text("[").setBold(true))
				.append(getPrevLink(page, pageCount))
				.append(new Text("] ").setBold(true))
				.append(getPageInfo(page, pageCount))
				.append(new Text(" [").setBold(true))
				.append(getNextLink(page, pageCount))
				.append(new Text("]").setBold(true));
		if (sender instanceof Player) {
			Util.send((Player) sender, pages);
		}
		Shop shop;
		for (int i=0; i<startIdx + PER_PAGE; i++) {
			if (i >= results.size()) break;
			shop = results.get(i);
			String line = "[" + ChatColor.DARK_AQUA + "" + (i+1) + "" + ChatColor.RESET + "] ";
			line += ChatColor.GOLD + shop.getDisplayName() + ChatColor.RESET + " @ ";
			line += shop.getLocation().getWorld().getName() + ":";
			line += shop.getLocation().getBlockX() + "," + shop.getLocation().getBlockZ();
			lines.add(line);
			line = "     " + WordUtils.capitalizeFully(shop.getType().name());
			line += " " + shop.getQuantity() + "x ";
			line += shop.getItemDisplayName() + " for ";
			Double price = shop.getPrice();
			if (price == null) {
				plugin.debug("No price set?!");
			}
			Economy econ = plugin.getEconomy();
			if (econ == null) {
				plugin.debug("No Economy?!");
			}
			line += plugin.getEconomy().format(shop.getPrice());
			lines.add(line);
		}
		sender.sendMessage(lines.toArray(new String[0]));
	}

	public static enum Operator {
		NE("!==", "<>", "><", "!="),
		GTE(">=", "=>"),
		LTE("<=", "=<"),
		EQ("==", "=", ""),
		LT("<"),
		GT(">");

		private static HashMap<String, Operator> byString = new HashMap<String, Operator>();
		private String[] strings;

		static {
			for (Operator op: values()) {
				for (String s: op.strings) {
					byString.put(s, op);
				}
			}
		}

		private Operator(String ... strings) {
			this.strings = strings;
		}

		public static Operator fromString(String s) {
			return byString.get(s);
		}
	}

	private ShopType shopType;
	private Kiosk plugin;
	private Map<FindCommand.SearchField, LinkedList<String>> searchCriteria;
	private CommandSender sender;
	private Iterator<UUID> shopIds;
	private List<Shop> results = new ArrayList<Shop>();

	public SearchTask(Kiosk plugin, ShopType type, Set<UUID> shopIds, HashMap<FindCommand.SearchField, LinkedList<String>> searchCriteria, String searchString, CommandSender sender) {
		this.searchString = searchString;
		this.shopType = type;
		this.plugin = plugin;
		this.shopIds = shopIds.iterator();
		this.sender = sender;
		this.searchCriteria = searchCriteria;

	}

	public int run(int maxShops) {
		int searched = 0;
		while (searched < maxShops && shopIds.hasNext()) {
			Shop shop = plugin.getStore().getShop(shopIds.next());
			if (canSearch(shop)) {
				if (shopMatches(shop)) {
					results.add(shop);
				}
				searched += 1;
			}
		}
		return searched;
	}

	private boolean canSearch(Shop shop) {
		if (shop == null) {
			return false;
		}
		if (shop.getPrice() == null || shop.getQuantity() == null) {
			return false;
		}
		if (shop.isPrivate() && sender instanceof Player) {
			return shop.isOwner((Player) sender) || shop.isStaffMember((Player) sender);
		}
		return true;
	}

	private boolean shopMatches(Shop shop) {
		for (FindCommand.SearchField sf: searchCriteria.keySet()) {
			LinkedList<String> params = searchCriteria.get(sf);
			switch (sf) {
			case TEXT:
			    if (!matchesText(shop, params)) return false;
				break;
			case ENCHANTMENTS:
				if (!matchesEnchantments(shop, params)) return false;
			case PRICE:
				if (!matchesPrice(shop, params)) return false;
			}

		}
		return true;
	}

	private boolean matchesPrice(Shop shop, LinkedList<String> params) {
		double unitPrice = shop.getPrice() / (double) shop.getQuantity();
		String paramStr = "";
		for (String s: params) {
			paramStr += s + " ";
		}
		if (paramStr.endsWith(" ")) {
			paramStr = paramStr.substring(0, paramStr.length()-1);
		}
		// Defaults to price >= X if searching for a shop that buys, <= X if
		// searching for a shop that sells.
		Operator op = shopType == ShopType.BUYING ? Operator.GTE : Operator.LTE;
		String arg = null;
		boolean split = false;
		for (Operator o: Operator.values()) {
			split = false;
			for (String ops: o.strings) {
				if (ops.equals("")) continue;
				if (paramStr.startsWith(ops)) {
					if (Operator.fromString(ops) != null) {
						op = Operator.fromString(ops);
						split = true;
						paramStr = paramStr.substring(ops.length()).trim();
						break;
					}
				}
			}
			if (split) {
				break;
			}
		}

		// Got an operator
		Double price = null;
		try {
			price = Double.parseDouble(paramStr);
		} catch (IllegalArgumentException ex) {
			return false;
		}

		switch (op) {
		case LT:
			return unitPrice < price;
		case LTE:
			return unitPrice <= price;
		case GT:
			return unitPrice > price;
		case GTE:
			return unitPrice >= price;
		case EQ:
			return unitPrice == price;
		case NE:
			return unitPrice != price;
		}
		return true;
	}


	private static class EnchantmentSearch {
		private Enchantment ench;
		private Operator operator = null;
		private Integer level = -1;

		private EnchantmentSearch(Enchantment ench, Operator operator, Integer level) {
			this.ench = ench;
			this.operator = operator;
			this.level = level;
		}

		public boolean matches(ItemStack stack) {
			for (Enchantment e: stack.getEnchantments().keySet()) {
				if (this.ench.equals(e)) {
					if (this.operator == null) return true;
					switch (this.operator) {
					case LT:
						return (stack.getEnchantmentLevel(e) < this.level);
					case GT:
						return (stack.getEnchantmentLevel(e) > this.level);
					case LTE:
						return (stack.getEnchantmentLevel(e) <= this.level);
					case GTE:
						return (stack.getEnchantmentLevel(e) >= this.level);
					case EQ:
						return (stack.getEnchantmentLevel(e) == this.level);
					case NE:
					    return (stack.getEnchantmentLevel(e) != this.level);
					}
				}
			}
			return false;
		}
	}

	private boolean matchesEnchantments(Shop shop, LinkedList<String> params) {
		LinkedList<EnchantmentSearch> searches = new LinkedList<EnchantmentSearch>();

		while (!params.isEmpty()) {
			String arg = params.pop().trim();
			boolean split = false;

			// Split out any operators that weren't spaced.
			Operator oper = Operator.fromString(arg);
			if (oper == null) {
				for (Operator op: Operator.values()) {
					split = false;
					for (String s: op.strings) {
						if (arg.contains(s) && !s.equals("")) {
							String[] parts = arg.split(s);
							if (parts[0].equals("")) {
								arg = s;
								params.add(0, parts[1]);
							} else if (parts[1].equals("")) {
								arg = parts[0];
								params.add(0, s);
							} else {
								arg = parts[0];
								params.add(0, parts[1]);
								params.add(0, s);
							}
							split = true;
							break;
						}
					}
					if (split) break;
				}
			}

			// Handle the first EnchantmentSearch
			if (searches.isEmpty()) {
				Enchantment e = Util.matchEnchantment(arg);
				if (e == null) return false;
				searches.add(new EnchantmentSearch(e, null, null));
				continue;
			}

			// Check for another enchantment starting
			Enchantment e = Util.matchEnchantment(arg);
			if (e != null) {
				searches.add(new EnchantmentSearch(e, null, null));
				continue;
			}
			// Check for a level
			try {
				Integer level = null;
				level = Integer.parseInt(arg);
				if (searches.peekLast().operator == null) {
					searches.peekLast().operator = Operator.EQ;
				}
				searches.peekLast().level = level;
				continue;
			} catch (IllegalArgumentException ex) {
			}

			// should be an operator
			Operator operator = Operator.fromString(arg);
			searches.peekLast().operator = operator;
		}

		// Now we have a list of EnchantmentSearches to perform:
		for (EnchantmentSearch es: searches) {
			if (!es.matches(shop.getItem())) {
				return false;
			}
		}
		return true;
	}

	private boolean matchesText(Shop shop, List<String> params) {
		String base = shop.getItemDisplayName().toLowerCase();
		for (String p: params) {
			if (!base.contains(p.toLowerCase())) return false;
		}
		return true;
	}

	public void cancel() {
		cancelled = true;
		if (sender instanceof Player) {
			if (((Player) sender).hasMetadata("kiosk-searchtask")) {
				((Player) sender).removeMetadata("kiosk-searchtask", plugin);
			}
		}
	}
}
