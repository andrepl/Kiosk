package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.ShopType;
import com.norcode.bukkit.kiosk.command.CommandHandler;
import com.norcode.bukkit.kiosk.datastore.SearchTask;
import com.norcode.bukkit.kiosk.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class FindCommand extends CommandHandler {

	public static enum SearchField {
		TEXT(null, ""),
		ENCHANTMENTS("-ench", "Searches for specific enchantments"),
		PRICE("-price", "searches within a specific price range (per unit)");

		private String opt;
		private String description;

		private SearchField(String opt, String description) {
			this.opt = opt;
			this.description = description;
		}

		public static SearchField getByOption(String arg) {
			for (SearchField sf: values()) {
				if (sf.opt != null && sf.opt.equalsIgnoreCase(arg.toLowerCase())) {
					return sf;
				}
			}
			return null;
		}
	}

	public FindCommand(Kiosk plugin) {
		super(plugin, "find", "Search all public kiosks.", "kiosk.command.find", new String[] {"/kiosk find [buying|selling] <needle> [-price <comparator> <value>] [-ench <enchantment> [<comparator> <level>]]", "Very powerful search that can be used to search for kiosks `buying` or `selling` the `needle`.", "Examples: /kiosk find selling cobble -price < 2.50", "/kiosk find selling diamond sword -ench looting >= 3"});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, LinkedList<String> args) throws Exception {

		if (args.size() == 0) {
			showHelp(sender);
			return true;
		}
	    String searchString = "";
		for (String a: args) {
			searchString += a + " ";
		}
		if (searchString.endsWith(" ")) searchString = searchString.substring(0, searchString.length()-1);

		// We require a BUYING or SELLING as the first argument
		ShopType type = null;
		if (args.peek().equalsIgnoreCase("BUYING")) {
			type = ShopType.BUYING;
		} else if (args.peek().equalsIgnoreCase("SELLING")) {
			type = ShopType.SELLING;
		} else {
			throw new Exception("Expecting 'BUYING' or 'SELLING' as the first argument");
		}
		args.pop();

		// First build up a map of all search criteria.
		HashMap<SearchField, LinkedList<String>> searchCriteria = new HashMap<SearchField, LinkedList<String>>();
		SearchField current = SearchField.TEXT;
		searchCriteria.put(current, new LinkedList<String>());
		while (args.size() > 0) {
			String arg = args.pop();
			SearchField field = SearchField.getByOption(arg);
			if (field != null) {
				// Switching search field
				searchCriteria.put(field, new LinkedList<String>());
				current = field;
				continue;
			} else if (arg.startsWith("-")) {
				throw new Exception("Unknown search option '" + arg + "'");
			}
			searchCriteria.get(current).add(arg);
		}

		// Make sure they provided enough search criteria.
		if (searchCriteria.size() == 0) {
			throw new Exception("You must provide some search criteria");
		} else if (searchCriteria.size() == 1) {
			if (searchCriteria.get(SearchField.TEXT) != null) {
				if (searchCriteria.get(SearchField.TEXT).isEmpty()) {
					throw new Exception("You must provide some search criteria");
				}
			}
		}

		if (sender instanceof Player) {
			if (((Player) sender).hasMetadata("kiosk-searchtask")) {
				SearchTask existingSearch = (SearchTask) ((Player) sender).getMetadata("kiosk-searchtask").get(0).value();
				if (existingSearch != null && !existingSearch.isFinished()) {
					throw new Exception("You already have a search in progress, please wait until your results are returned.");
				}
			}
		}
		plugin.getStore().search(type, searchCriteria, searchString, sender);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, LinkedList<String> args) {
		Set<String> used = new HashSet<String>();
		SearchField lastField = SearchField.TEXT;
		List<String> switchOptions = new ArrayList<String>();
		List<String> results = new ArrayList<String>();
		if (args.size() == 1) {
			if ("buying".startsWith(args.peek().toLowerCase())) {
				results.add("BUYING");
			}
			if ("selling".startsWith(args.peek().toLowerCase())) {
				results.add("SELLING");
			}
			return results;
		}

		while (args.size() > 1) {
			String arg = args.pop().toLowerCase();
			if (arg.startsWith("-")) {
				used.add(arg);
				SearchField option = SearchField.getByOption(arg);
				switchOptions.clear();
			} else {
				switchOptions.add(arg);
			}
		}

		if (args.peek().startsWith("-") || args.peek().equals("")) {
			for (SearchField sf: SearchField.values()) {
				if (sf.opt != null && sf.opt.startsWith(args.peek().toLowerCase())) {
					if (!used.contains(sf.opt)) {
						results.add(sf.opt);
					}
				}
			}
		} else {
			switch (lastField) {
			case ENCHANTMENTS:
				for (Enchantment e: Enchantment.values()) {
					if (switchOptions.contains(e.getName().toLowerCase()) ||
							switchOptions.contains(Util.getEnchantmentDisplayName(e).replace(" ", "").toLowerCase())) {
						continue;
					}
					if (e.getName().toLowerCase().startsWith(args.peek().toLowerCase())) {
						results.add(e.getName());
					} else if (Util.getEnchantmentDisplayName(e).replace(" ", "").toLowerCase().contains(args.peek().toLowerCase())) {
						results.add(Util.getEnchantmentDisplayName(e).replace(" ", ""));
					}
				}
				return results;
			}
		}
		return results;
	}
}
