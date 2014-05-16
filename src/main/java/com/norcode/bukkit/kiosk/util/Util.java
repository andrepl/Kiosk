package com.norcode.bukkit.kiosk.util;

import Catalano.Imaging.Tools.ColorConverter;
import Catalano.Imaging.Tools.ColorDifference;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.PacketPlayOutChat;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Util {

	private static HashMap<PotionEffectType, String> POTION_EFFECT_NAMES = new HashMap<PotionEffectType,String>();

	static {
		POTION_EFFECT_NAMES.put(PotionEffectType.ABSORPTION, "Absorption");
		POTION_EFFECT_NAMES.put(PotionEffectType.BLINDNESS, "Blindness");
		POTION_EFFECT_NAMES.put(PotionEffectType.CONFUSION, "Nausea");
		POTION_EFFECT_NAMES.put(PotionEffectType.DAMAGE_RESISTANCE, "Resistance");
		POTION_EFFECT_NAMES.put(PotionEffectType.FAST_DIGGING, "Haste");
		POTION_EFFECT_NAMES.put(PotionEffectType.FIRE_RESISTANCE, "Fire Resistance");
		POTION_EFFECT_NAMES.put(PotionEffectType.HARM, "Instant Damage");
		POTION_EFFECT_NAMES.put(PotionEffectType.HEAL, "Instant Health");
		POTION_EFFECT_NAMES.put(PotionEffectType.HEALTH_BOOST, "Health Boost");
		POTION_EFFECT_NAMES.put(PotionEffectType.HUNGER, "Hunger");
		POTION_EFFECT_NAMES.put(PotionEffectType.INCREASE_DAMAGE, "Strength");
		POTION_EFFECT_NAMES.put(PotionEffectType.INVISIBILITY, "Invisibility");
		POTION_EFFECT_NAMES.put(PotionEffectType.JUMP, "Jump Boost");
		POTION_EFFECT_NAMES.put(PotionEffectType.NIGHT_VISION, "Night Vision");
		POTION_EFFECT_NAMES.put(PotionEffectType.POISON, "Poison");
		POTION_EFFECT_NAMES.put(PotionEffectType.REGENERATION, "Regeneration");
		POTION_EFFECT_NAMES.put(PotionEffectType.SATURATION, "Saturation");
		POTION_EFFECT_NAMES.put(PotionEffectType.SLOW, "Slowness");
		POTION_EFFECT_NAMES.put(PotionEffectType.SLOW_DIGGING, "Mining Fatigue");
		POTION_EFFECT_NAMES.put(PotionEffectType.SPEED, "Speed");
		POTION_EFFECT_NAMES.put(PotionEffectType.WATER_BREATHING, "Water Breathing");
		POTION_EFFECT_NAMES.put(PotionEffectType.WEAKNESS, "Weakness");
		POTION_EFFECT_NAMES.put(PotionEffectType.WITHER, "Wither");
	}

	public static String getPotionEffectDisplayName(PotionEffect effect) {
		String name = POTION_EFFECT_NAMES.get(effect);
		if (name == null) {
			return WordUtils.capitalizeFully(effect.getType().getName().replace("_", " "));
		}
		int level = effect.getAmplifier() + 1;
		String duration = ticksToDuration(effect.getDuration());
		name += " " + roman(level) + "(" + duration + ")";
		return name;
	}

	private static String ticksToDuration(long duration) {
		long seconds = duration / 20;
		long hours = TimeUnit.SECONDS.toHours(seconds);
		seconds -= TimeUnit.HOURS.toSeconds(hours);
		long minutes = TimeUnit.SECONDS.toMinutes(seconds);
		seconds -= TimeUnit.MINUTES.toSeconds(minutes);
		String repr = "";
		if (hours > 1) {
			repr = hours + ":";
			if (minutes < 10) {
				repr += "0";
			}
		}
		return repr + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}

	public static String renderDurability(ItemStack stack) {
		int max = stack.getType().getMaxDurability();
		int val = max - stack.getDurability();
		int pct = Math.round((val / (float) max) * 100);
		ChatColor clr = ChatColor.RED;
		if (pct >= 85) {
			clr = ChatColor.GREEN;
		} else if (pct >= 50) {
			clr = ChatColor.YELLOW;
		} else if (pct >= 25) {
			clr = ChatColor.GOLD;
		}
		return clr + "" + pct + "%" + ChatColor.RESET + " " + ChatColor.GRAY + "(" + + val + "/" + max + ")";

	}

	public static String getEnchantmentDisplayName(Enchantment e) {
		String ench = ENCHANTMENT_NAMES.get(e);
		if  (ench == null) {
			ench = e.getName();
		}
		return ench;
	}

	public static Enchantment matchEnchantment(String arg) {
		for (Map.Entry<Enchantment, String> ench: ENCHANTMENT_NAMES.entrySet()) {
			Enchantment e = ench.getKey();
			String simpleName = ench.getValue();
			if (e.getName().equalsIgnoreCase(arg) || e.getName().replace("_", "").equalsIgnoreCase(arg)
				|| simpleName.replace(" ", "").equalsIgnoreCase(arg)
				|| simpleName.replace(" ", "_").equalsIgnoreCase(arg)) {
				return e;
			}
		}
		return null;
	}

	public static MaterialData parseRecipeItem(String string) {
		ItemInfo info = Items.itemByString(string);
		MaterialData data = new MaterialData(info.getType());
		data.setData((byte) info.getSubTypeId());
		return data;
	}

	public enum ChatColorRGB {
		BLACK(ChatColor.BLACK, new int[] {0, 0, 0}),
		DARK_BLUE(ChatColor.DARK_BLUE, new int[] {0, 0, 170}),
		DARK_GREEN(ChatColor.DARK_GREEN, new int[] {0, 170, 0}),
		DARK_AQUA(ChatColor.DARK_AQUA, new int[] {0, 170, 170}),
		DARK_RED(ChatColor.DARK_RED, new int[] {170, 0, 0}),
		DARK_PURPLE(ChatColor.DARK_PURPLE, new int[] {170, 0, 170}),
		GOLD(ChatColor.GOLD, new int[] {255, 170, 0}),
		GRAY(ChatColor.GRAY, new int[] {170, 170, 170}),
		DARK_GRAY(ChatColor.DARK_GRAY, new int[] {85, 85, 85}),
		BLUE(ChatColor.BLUE, new int[] {85, 85, 255}),
		GREEN(ChatColor.GREEN, new int[] {85, 255, 85}),
		AQUA(ChatColor.AQUA, new int[] {85, 255, 255}),
		RED(ChatColor.RED, new int[] {255, 85, 85}),
		LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, new int[] {255, 85, 255}),
		YELLOW(ChatColor.YELLOW, new int[] {255, 255, 85}),
		WHITE(ChatColor.WHITE, new int[] {255, 255, 255});

		private static HashMap<ChatColor, ChatColorRGB> byChatColor = new HashMap<ChatColor, ChatColorRGB>();
		static {
			for (ChatColorRGB r: values()) {
				byChatColor.put(r.chatColor, r);
			}
		}

		private ChatColor  chatColor;
		private int[] rgb;

		ChatColorRGB(ChatColor cc, int[] rgb) {
			this.chatColor = cc;
			this.rgb = rgb;
		}

		public ChatColorRGB byChatColor(ChatColor cc) {
			return byChatColor.get(cc);
		}
	}

	public enum ChatColorLab {

		BLACK(ChatColor.BLACK, new float[] {0.0f, 0.0f, 0.0f}),
		DARK_BLUE(ChatColor.DARK_BLUE, new float[] {20.0662f, 58.4435f, -79.364624f}),
		DARK_GREEN(ChatColor.DARK_GREEN, new float[] {60.877235f, -63.29617f, 61.19883f}),
		DARK_AQUA(ChatColor.DARK_AQUA, new float[] {63.355354f, -35.290867f, -10.357035f}),
		DARK_RED(ChatColor.DARK_RED, new float[] {35.52149f, 58.927402f, 50.127556f}),
		DARK_PURPLE(ChatColor.DARK_PURPLE, new float[] {40.73656f, 72.19474f, -44.670074f}),
		GOLD(ChatColor.GOLD, new float[] {76.291336f, 21.154463f, 79.32993f}),
		GRAY(ChatColor.GRAY, new float[] {69.870636f, -0.0017881393f, 0.0034093857f}),
		DARK_GRAY(ChatColor.DARK_GRAY, new float[] {36.564453f, -0.0010877848f, 0.0020861626f}),
		BLUE(ChatColor.BLUE, new float[] {46.893578f, 50.854324f, -83.60722f}),
		GREEN(ChatColor.GREEN, new float[] {89.07631f, -73.62572f, 65.47763f}),
		AQUA(ChatColor.AQUA, new float[] {92.059715f, -42.07501f, -12.6142025f}),
		RED(ChatColor.RED, new float[] {60.586205f, 63.77524f, 36.392204f}),
		LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, new float[] {66.020256f, 81.949295f, -51.7802f}),
		YELLOW(ChatColor.YELLOW, new float[] {97.431015f, -19.230812f, 76.73251f}),
		WHITE(ChatColor.WHITE, new float[] {100.0f, -0.0024437904f, 0.0046014786f});

		private float[] lab;
		private ChatColor chatColor;

		private static HashMap<ChatColor, ChatColorLab> byChatColor = new HashMap<ChatColor, ChatColorLab>();

		static {
			for (ChatColorLab ccl: values()) {
				byChatColor.put(ccl.chatColor, ccl);
			}
		}

		ChatColorLab(ChatColor cc, float[] lab) {
			this.lab = lab;
			this.chatColor = cc;
		}

		public ChatColorLab byChatColor(ChatColor cc) {
			return byChatColor.get(cc);
		}
	}

	public static String colorToHex(Color color) {
		StringBuilder s = new StringBuilder("#");
		String red = Integer.toHexString(color.getRed()).toUpperCase();
		String green = Integer.toHexString(color.getGreen()).toUpperCase();
		String blue = Integer.toHexString(color.getBlue()).toUpperCase();
		if (red.length() == 1) s.append("0");
		s.append(red);
		if (green.length() == 1) s.append("0");
		s.append(green);
		if (blue.length() == 1) s.append("0");
		s.append(blue);
		return s.toString();
	}

	public static ChatColor closestChatColor(Color c) {
		float[] lab = ColorConverter.RGBtoLAB(c.getRed(), c.getGreen(), c.getBlue(), ColorConverter.CIE2_D65);

		double dist = Float.MAX_VALUE;
		ChatColor match = null;
		for (ChatColorLab lab2: ChatColorLab.values()) {
			double diff = Math.abs(ColorDifference.DeltaE(lab, lab2.lab));
			//System.out.println("Difference between " + c + " and " + lab2.name() + " is " + diff);
			if (diff < dist) {
				match = lab2.chatColor;
				dist = diff;
			}
		}
		return match;
	}

	public enum Numeral {
		I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);
		int weigth;

		Numeral(int weigth) {
			this.weigth = weigth;
		}
	}

	private static HashMap<Enchantment, String> ENCHANTMENT_NAMES = new HashMap<Enchantment, String>();
	static {
		ENCHANTMENT_NAMES.put(Enchantment.ARROW_DAMAGE, "Power");
		ENCHANTMENT_NAMES.put(Enchantment.ARROW_FIRE, "Flame");
		ENCHANTMENT_NAMES.put(Enchantment.ARROW_INFINITE, "Infinity");
		ENCHANTMENT_NAMES.put(Enchantment.ARROW_KNOCKBACK, "Punch");
		ENCHANTMENT_NAMES.put(Enchantment.DAMAGE_ALL, "Sharpness");
		ENCHANTMENT_NAMES.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
		ENCHANTMENT_NAMES.put(Enchantment.DAMAGE_UNDEAD, "Smite");
		ENCHANTMENT_NAMES.put(Enchantment.DIG_SPEED, "Efficiency");
		ENCHANTMENT_NAMES.put(Enchantment.DURABILITY, "Unbreaking");
		ENCHANTMENT_NAMES.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
		ENCHANTMENT_NAMES.put(Enchantment.KNOCKBACK, "Knockback");
		ENCHANTMENT_NAMES.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
		ENCHANTMENT_NAMES.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
		ENCHANTMENT_NAMES.put(Enchantment.OXYGEN, "Respiration");
		ENCHANTMENT_NAMES.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
		ENCHANTMENT_NAMES.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
		ENCHANTMENT_NAMES.put(Enchantment.PROTECTION_FALL, "Feather Falling");
		ENCHANTMENT_NAMES.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
		ENCHANTMENT_NAMES.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
		ENCHANTMENT_NAMES.put(Enchantment.SILK_TOUCH, "Silk Touch");
		ENCHANTMENT_NAMES.put(Enchantment.THORNS, "Thorns");
		ENCHANTMENT_NAMES.put(Enchantment.WATER_WORKER, "Aqua Affinity");
	}

	public static Location parseLocation(String s) {
		String[] parts = s.split(";");
		Location loc = null;
		try {
			loc = new Location(Bukkit.getWorld(UUID.fromString(parts[0])),
					Double.parseDouble(parts[1]),
					Double.parseDouble(parts[2]),
					Double.parseDouble(parts[3]));
			if (parts.length > 4) {
				loc.setYaw(Float.parseFloat(parts[4]));
				if (parts.length > 5) {
					loc.setPitch(Float.parseFloat(parts[5]));
				}
			}
		} catch (IllegalArgumentException ex) {
			// just return whatever we have.
			// it might be null, or it might
			// have the world/x/y/z and possibly
			// even yaw.
		}
		return loc;
	}

	public static String serializeLocation(Location location) {
		String s = location.getWorld().getUID().toString();
		s += ";" + location.getX() + ";" + location.getY();
		s += ";" + location.getZ() + ";" + location.getYaw();
		s += ";" + location.getPitch();
		return s;
	}

	public static String getEnchantmentDisplayName(Map.Entry<Enchantment, Integer> row) {
		String ench = getEnchantmentDisplayName(row.getKey());
		ench += " " + roman(row.getValue());
		return ench;
	}

	public static String roman(long n) {
		if( n <= 0) {
			throw new IllegalArgumentException();
		}

		StringBuilder buf = new StringBuilder();

		final Numeral[] values = Numeral.values();
		for (int i = values.length - 1; i >= 0; i--) {
			while (n >= values[i].weigth) {
				buf.append(values[i]);
				n -= values[i].weigth;
			}
		}
		return buf.toString();
	}
	public static Gson gson = new Gson();

	public static void main(String... args) {

	}

	public static String chatToJson(String s) {
		HashMap<String, Object> obj = new HashMap<String, Object>();
		obj.put("text", s);
		return gson.toJson(obj);
	}

	public static void send(Player player, Object ... lines) {
		for (Object line: lines) {
			if (line instanceof String) {
				player.sendMessage((String) line);
			} else if (line instanceof IChatBaseComponent) {
				Util.send(player, (IChatBaseComponent) line);
			} else {
				Bukkit.getLogger().info("Cannot send unknown type: " + line);
			}
		}
	}

	public static void send(Player player, IChatBaseComponent chat) {
		Bukkit.getLogger().info(chat.toString());
		PacketPlayOutChat packet =	new PacketPlayOutChat(chat, true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
