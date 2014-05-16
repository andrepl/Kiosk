package com.norcode.bukkit.kiosk.util;

import com.norcode.bukkit.kiosk.util.chat.Text;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChatArt {

	private static HashMap<String, ChatArt> images = new HashMap<String, ChatArt>();

	int MAX_WIDTH = 45;

	public static ChatArt getByName(String icon) {
		return images.get(icon);
	}

	private static void register(ChatArt art) {
		images.put(art.getName().toLowerCase(), art);
	}

	public static Collection<ChatArt> allIcons() {
		return images.values();
	}

	public ChatArt(String name, List<String> image) {
		this(name, image.toArray(new String[0]));
	}

	public ChatArt(String name, String[] image) {
		this.name = name;
		this.image = image;
	}

	public ChatArt(String[] image) {
		this.image = image;
	}

	public ChatArt(List<String> image) {
		this(image.toArray(new String[0]));
	}

	public String getName() {
		return name;
	}

	public static enum RepeatMode {
		REPEAT_ALL, REPEAT_LAST, LEFT_JUSTIFY
	}

	static {
		register(new ChatArt("warning", new String[] {
				"§0▒§0▒§0▒§0▒",
				"§0▒§4█§c█§0▒",
				"§0▒§c█§c█§0▒",
				"§0▒§c█§c█§0▒",
				"§0▒§c█§4█§0▒",
				"§0▒§c█§4█§0▒",
				"§0▒§4█§0▒§0▒",
				"§0▒§0▒§0▒§0▒",
				"§0▒§c█§4█§0▒",
				"§0▒§4█§4█§0▒",
				"§0▒§0▒§0▒§0▒"

		}));
		register(new ChatArt("grass", new String[] {
				ChatColor.DARK_GREEN + "████████",
				"█▓█▓██▓█".replace("▓", ChatColor.GOLD + "▓").replace("█", ChatColor.DARK_GREEN + "█"),
				ChatColor.GOLD +  	   "▓▓▒▓▓▒▓▓",
				ChatColor.GOLD +       "▒▓▓▒▓▓▒▓",
				ChatColor.GOLD +       "▓▓▒▓▒▓▓▓",
				ChatColor.GOLD +       "▓▒▓▓▓▒▓▒",
				ChatColor.GOLD +       "▓▓▒▓▓▒▓▓",
				ChatColor.BLACK +      "▒▒▒▒▒▒▒▒"
		}));
		register(new ChatArt("diamond", new String[] {
				ChatColor.BLACK + "▒▒▒▒" + ChatColor.DARK_AQUA + "▒▒▒▒" + ChatColor.BLACK + "▒▒▒▒",
				ChatColor.BLACK + "▒▒▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "████" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒▒▒",
				ChatColor.BLACK + "▒▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█" + ChatColor.AQUA + "███" + ChatColor.WHITE + "▓" + ChatColor.AQUA + "█" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒▒",
				ChatColor.BLACK + "▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█▓" + ChatColor.AQUA + "██" + ChatColor.WHITE + "█" + ChatColor.AQUA + "██" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒▒",
				ChatColor.BLACK + "▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█" + ChatColor.AQUA + "█" + ChatColor.WHITE + "█▓▓▓" + ChatColor.AQUA + "██" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒",
				ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█" + ChatColor.AQUA + "█" + ChatColor.WHITE + "█" + ChatColor.AQUA + "████" + ChatColor.DARK_AQUA + "█" + ChatColor.AQUA + "█" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒",
				ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█" + ChatColor.AQUA + "█" + ChatColor.WHITE + "▓" + ChatColor.AQUA + "████" + ChatColor.DARK_AQUA + "█" + ChatColor.AQUA + "█▓" + ChatColor.DARK_AQUA + "▒",
				ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█▓" + ChatColor.AQUA + "▓████" + ChatColor.DARK_AQUA + "█▓▓▒",
				ChatColor.DARK_AQUA + "▒" + ChatColor.WHITE + "█" + ChatColor.AQUA + "▓▓" + ChatColor.DARK_AQUA + "████" + ChatColor.AQUA + "▓▓▒" + ChatColor.DARK_AQUA + "▒",
				ChatColor.BLACK + "▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.AQUA + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.AQUA + "▓▓▓▓" + ChatColor.DARK_AQUA + "█" + ChatColor.AQUA + "▓" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒",
				ChatColor.BLACK + "▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.AQUA + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.AQUA + "▓▓▓▓" + ChatColor.DARK_AQUA + "█" + ChatColor.AQUA + "▓" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒",
				ChatColor.BLACK + "▒▒" + ChatColor.DARK_AQUA + "▒" + ChatColor.AQUA+ "▓▓▓▓▓▓" + ChatColor.DARK_AQUA + "▒" + ChatColor.BLACK + "▒▒",
				ChatColor.BLACK + "▒▒▒" + ChatColor.DARK_AQUA + "▒▒▒▒▒▒" + ChatColor.BLACK + "▒▒▒",
				ChatColor.BLACK + "▒▒▒▒▒▒▒▒▒▒▒▒"

		}));
		register(new ChatArt("blaze", new String[] {
				"§e█§6█§e█§6█§e█§e█§e█§e█",
				"§6█§e█§6█§e█§e█§6█§e█§6█",
				"§6█§6█§6█§e█§6█§6█§6█§6█",
				"§6█§e█§0█§e█§6█§0█§e█§6█",
				"§6▓§6█§6█§6█§6█§6█§6█§6█",
				"§6▒§4█§4█§0█§0█§4█§6▒§6▓",
				"§6▒§6▒§4▒§8▒§8▒§4▒§6▓§8▒",
				"§4▒§4▒§4▒§4▒§4▓§4▒§8▒§8▒"
		}));

		register(new ChatArt("mooshroom", new String[] {
				"§4▒§4▒§4▒§7█§7█§7█§7█§4▒",
				"§4▒§4▒§4▒§7█§7█§7█§4▒§4▒",
				"§7█§7█§4▒§7█§7█§4▒§7█§7█",
				"§0█§f█§4▒§7█§4▒§4▒§f█§0█",
				"§4▒§4▒§4▒§4▒§4▒§4▒§4▒§4▒",
				"§4▒§4▒§f█§f█§f█§f█§0█§4▒",
				"§4▒§f█§0█§4▒§4▒§0█§f█§4▒",
				"§4▒§7█§4▒§4▒§4▒§4▒§f█§4▒"
		}));

		register(new ChatArt("creeper", new String[] {
				"§7█§2█§2█§7█§7█§2█§2█§2█",
				"§2█§2█§2█§2█§2█§2█§2█§7█",
				"§2█§8█§8█§3█§3█§8█§8█§f█",
				"§2█§8█§0█§2█§2█§0█§8█§2█",
				"§2█§2█§3█§0█§0█§7█§2█§2█",
				"§7█§2█§0█§0█§0█§0█§2█§7█",
				"§f█§3█§0█§7█§2█§0█§2█§2█",
				"§2█§2█§8█§2█§7█§8█§2█§2█"
		}));

	}




	/*
	 *
	 * ░░░░░░░░  █▓▓▓▓▓▓█  ████████  ████████  ▒▒▒▒▒▒▒▒  ░░░░░░░░
	 * ▒▒▒▒▒▒▒▒  █▓▓▓▓▓▓█  ████████  ████████  ▒▒▒▒▒▒▒▒  ░░░░░░░░
	 * ▒██▒▒██▒  ▓█▓▓▓▓█▓  █▒▒▒▒▒▒█  █▒▒▒▒▒▒█  ▒▒▒▒▒▒▒▒  ░░░░░░░░
	 * ▒██▒▒██▒  ██▓▓▓▓██  ▒▒▒▒▒▒▒▒  ▒▒▒▒▒▒▒▒  ▒▒▒▒▒▒▒▒  ░░░░░░░░
	 * ▓▓▓██▓▓▓  ▒▓▒▓▓▒▓▒  ▒░▓▒▒▓░▒  ▒░░▒▒░░▒  ▒██▒▒██▒  ░██░░██░
	 * ▓▓████▓▓  ▓██▓▓██▓  ▒▒▒▓▓▒▒▒  ▒▒▒▓▓▒▒▒  ▒▒▒▓▓▒▒▒  ░░░▓▓░░░
	 * ▓▓████▓▓  █▓▓██▓▓█  ▒▒▓▒▒▓▒▒  ▒▒▓▒▒▓▒▒  ▒▒▒▒▒▒▒▒  ░██████░
	 * ▓▓█▓▓█▓▓  ▓██████▓  ▒▒▓▓▓▓▒▒  ▒▓▓▓▓▓▒▒  ▒▒▒▒▒▒▒▒  ░░░░░░░░
	 *
	 */


	public static enum Px {
		LIGHT("\u2592"),
		DARK("\u2593"),
		SOLID("\u2588");

		private String str;

		Px(String str) {
			this.str = str;
		}

		public String times(int times) {
			return StringUtils.repeat(this.str, times);
		}
	}

	private String name;
	private String[] image;

	public int getMaxWidth() {
		int max = 0;
		for (String s: image) {
			if (s.length() > max) {
				max = ChatColor.stripColor(s).length();
			}
		}
		return max;
	}

	public List<IChatBaseComponent> formatMessage(IChatBaseComponent... message) {
		int offset = (this.image.length - message.length) / 2;
		offset = Math.max(offset, 0);
		int msgPos = 0;
		List<IChatBaseComponent> results = new ArrayList<IChatBaseComponent>();
		for (String line: this.image) {
			IChatBaseComponent msg;
			if (msgPos >= message.length) {
				msg = new Text("");
			} else {
				msg = message[msgPos];
			}
			results.add(new Text(line).append((offset > 0 || msgPos >= message.length) ? new Text("") : msg));
			if (offset > 0) {
				offset --;
			} else {
				msgPos ++;
			}
		}
		while (msgPos < message.length) {
			results.add(new Text("").append(new Text(ChatColor.BLACK + StringUtils.repeat("▒", this.getMaxWidth())))
									.append(message[msgPos++]));
		}
		return results;
	}

	public String[] formatMessage(List<String> message, RepeatMode repeatMode, boolean completeImage) {
		int width = this.getMaxWidth();
		int height = this.image.length;
		LinkedList<String> lines = new LinkedList<String>(message);
		ArrayList<String> out = new ArrayList<String>();
		int maxLineWidth = MAX_WIDTH - getMaxWidth();

		while (!lines.isEmpty()) {
			// decide how much room we have for this line.
			String line = lines.pop();
			if (out.size() > height) {
				if ((out.size() >= height) && repeatMode == RepeatMode.LEFT_JUSTIFY) {
					maxLineWidth = MAX_WIDTH;
				} else {
					maxLineWidth = MAX_WIDTH - width;
				}
			}

			// split the line as necessary and add it back to the message
			if (ChatColor.stripColor(line).length() > maxLineWidth) {

				line = wordWrap(line, maxLineWidth);
				if (line.contains(System.lineSeparator())) {
					String[] parts = line.split(System.lineSeparator());
					for (int i=parts.length-1; i>0; i--) {
						lines.add(0, parts[i]);
					}
					line = parts[0];
				}
			}

			// Render it
			int imgLine = out.size() % height;
			String leftCol = this.image[imgLine] + "  " + ChatColor.RESET;

			if (out.size() >= height) {
				if (repeatMode == RepeatMode.LEFT_JUSTIFY) {
					leftCol = ChatColor.RESET.toString();
				} else if (repeatMode == RepeatMode.REPEAT_LAST) {
					leftCol = this.image[this.image.length-1] + "  " + ChatColor.RESET;
				}
			}
			out.add(leftCol + line);
		}
		if (completeImage && out.size() < height) {
			for (int i=out.size();i<height;i++) {
				out.add(this.image[i]);
			}
		}
		return out.toArray(new String[0]);
	}


	public static String wordWrap(String line, int length) {
		int lastSplittable = 0;
		int visibleCount = 0;
		String activeColors = "";
		int pos = 0;
		boolean codePending = false;
		char c;
		ChatColor code;
		StringBuilder results = new StringBuilder();
		int added = 0;
		while (pos < line.length()) {
			c = line.charAt(pos);
			if (codePending) {
				code = ChatColor.getByChar(c);
				if (code == ChatColor.RESET) {
					activeColors = "";
				} else if (code != null) {
					activeColors += "" + code;
				} else {
					visibleCount += 1;
				}
				codePending = false;
			} else if (c == ChatColor.COLOR_CHAR) {
				codePending = true;
			} else {
				if (c == ' ') {
					lastSplittable = pos + added;
				}
				visibleCount ++;
			}
			results.append(c);
			if (visibleCount > length) {
				String prefix = System.lineSeparator() + ChatColor.getLastColors(activeColors);
				added += prefix.length()-1;				results.deleteCharAt(lastSplittable);

				results.insert(lastSplittable, prefix);
				activeColors = "";
				visibleCount = ChatColor.stripColor(results.substring(lastSplittable+1)).length();
			}
			pos ++;
		}
		return results.toString();
	}


	static FilenameFilter imageFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			System.out.println("Checking " + name);
			return name.toLowerCase().endsWith(".png");
		}
	};

	public static void main(String ... args) {
		File dir = new File("./images");
		System.out.println(dir.getAbsolutePath());
		for (File file: dir.listFiles(imageFilter)) {
			try {
				for (String s: fromImage(ImageIO.read(file))) {
					System.out.println(s);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.out.println("-----------------------------------------------------------------------");
		}

	}


	public static String[] fromImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		String[] lines = new String[height];
		String line;
		for (int y = 0; y < height; y++) {
			line = "";
			for (int x = 0; x < width; x++) {
				int rgb = image.getRGB(x, y);
				int red = (rgb >> 16) & 0x000000FF;
				int green = (rgb >>8 ) & 0x000000FF;
				int blue = (rgb) & 0x000000FF;
				line += Util.closestChatColor(Color.fromRGB(red, green, blue)) + "█";
			}
			lines[y] = line;
		}
		return lines;
	}
}
