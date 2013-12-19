package com.norcode.bukkit.kiosk.util;

import com.norcode.bukkit.kiosk.Kiosk;
import net.minecraft.server.v1_7_R1.Item;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lang {
	Kiosk plugin;
	private static Pattern pat = Pattern.compile("^\\s*([\\w\\d\\.]+)\\s*=\\s*(.*)\\s*$");

	private static HashMap<String, String> language;
	;

	public static void initialize(Kiosk plugin) throws IOException {
		language = new HashMap<String, String>();
		String lang = plugin.getConfig().getString("language");
		String filename = lang + ".lang";
		String resourcePath = "/assets/minecraft/lang/" + filename;
		plugin.debug("Loading " + resourcePath + " from jar");
		InputStream fis = Item.class.getResourceAsStream(resourcePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String line;
		Matcher matcher;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.contains("=")) {
				matcher = pat.matcher(line);
				if (matcher.matches()) {
					language.put(matcher.group(1), matcher.group(2));
				}
			}
		}

	}


	public static String translatableFromStack(ItemStack stack) {
		net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		Item item = nms.getItem();
		return item.a(nms);
	}

	public static String fromStack(ItemStack stack) {
		String node = translatableFromStack(stack);
		String val = language.get(node);
		if (val == null) {
			return node;
		}
		return val;
	}

	public static String translatableFromEnchantment(Enchantment ench) {
		net.minecraft.server.v1_7_R1.Enchantment nms = net.minecraft.server.v1_7_R1.Enchantment.byId[ench.getId()];
		if (nms == null) {
			return ench.getName();
		} else {
			return nms.a();
		}
	}

	public static String fromEnchantment(Enchantment ench) {
		String node = translatableFromEnchantment(ench);
		String val = language.get(node);
		if (val == null) {
			return node;
		}
		return val;
	}

	public static String translate(String key, Object... args) {
		return String.format(language.get(key), args);
	}

}
