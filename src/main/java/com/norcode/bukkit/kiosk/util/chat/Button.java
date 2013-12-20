package com.norcode.bukkit.kiosk.util.chat;

import com.norcode.bukkit.kiosk.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Button extends Text {
	public static class Icon {

		public static String BUY = "BUY";
		public static String SELL = "SELL";
		public static String TELEPORT = "WARP";
		public static String STOCK = "STOCK";
//		public static String BUY = "⛁⇨❐";
//		public static String SELL = "❐⇨⛁";
//		public static String TELEPORT = "⇛☺⇛";
//		public static String STOCK = "↷⌂↶";
	}

	public static class Ends {
		public static String SHARP_ROUND = "❨❩";
		public static String ELABORATE = "༺༻";
		public static String THICK_LENTICULAR = "【】";
		public static String HOLLOW_LENTICULAR = "〖〗";
		public static String DOUBLE_ROUND = "｟｠";
	}

	public Button(Text text, String hoverText, String command, String border) {
		super("");
		append(border.substring(0,1)).setBold(false).setColor(ChatColor.GRAY);
		append(text.setHoverText(hoverText).setClick(ClickAction.RUN_COMMAND, command).setBold(false));
		append(border.substring(1)).setBold(false).setColor(ChatColor.GRAY);
	}

	public Button(String text, String hoverText, String command, String border) {
		this(new Text(text), hoverText, command, border);
	}

	public static Button buy(Shop shop) {
		return new Button(new Text(ChatColor.BLUE + Icon.BUY),
				"Buy " + ChatColor.GOLD + shop.getQuantity() + ChatColor.RESET + " " +
						shop.getItemDisplayName() + " for " +
						ChatColor.GOLD + shop.getPlugin().getEconomy().format(shop.getPrice()),
				"/kiosk buy", Ends.THICK_LENTICULAR);
	}

	public static Button sell(Shop shop) {
		return new Button(new Text(ChatColor.BLUE + Icon.SELL),
				"Sell " + ChatColor.GOLD + shop.getQuantity() + ChatColor.RESET + " " +
						shop.getItemDisplayName() + " for " +
						ChatColor.GOLD + shop.getPlugin().getEconomy().format(shop.getPrice()),
				"/kiosk sell", Ends.THICK_LENTICULAR);
	}

	public static Button teleport(Shop shop) {
		Location loc = shop.getLocation();
		return new Button(new Text(ChatColor.BLUE + Icon.TELEPORT),
				"Teleport to " + shop.getDisplayName() + " " + ChatColor.DARK_GRAY + "(" +
						loc.getWorld().getName() + "@X:" + loc.getBlockX() + ",Y:" + loc.getBlockY() + ",Z:" + loc.getBlockZ() + ")",
				"/kiosk teleport", Ends.THICK_LENTICULAR);
	}

	public static Button stock(Shop shop) {
		return new Button(new Text(ChatColor.BLUE + Icon.STOCK),
				"Open shop inventory",
				"/kiosk stock",
				Ends.THICK_LENTICULAR);
	}

}
