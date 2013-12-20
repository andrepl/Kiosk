package com.norcode.bukkit.kiosk.command.subcommands;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.Shop;
import com.norcode.bukkit.kiosk.command.BaseCommand;
import com.norcode.bukkit.kiosk.command.PlayerCommand;
import com.norcode.bukkit.kiosk.datastore.SearchTask;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ResultsCommand extends BaseCommand {

	public ResultsCommand(Kiosk plugin ) {
		super(plugin, "results", "browse your search results", "kiosk.command.results", null);
		registerSubcommand(new PageCommand(plugin));
		registerSubcommand(new ShowCommand(plugin));
	}

	private class ShowCommand extends PlayerCommand {

		protected ShowCommand(Kiosk plugin) {
			super(plugin, "show", "Show details about one of the search results.", "kiosk.command.results", new String[] {"/kiosk show <number>", "Provides more information about result `number`."});
		}

		@Override
		public List<String> onTabComplete(Player player, Command command, String label, LinkedList<String> args) {
			return new ArrayList<String>();
		}

		@Override
		public boolean onCommand(Player player, Command command, String label, LinkedList<String> args) throws Exception {
			// get the search results
			if (!player.hasMetadata("kiosk-searchtask")) {
				throw new Exception("You do not have any search results");
			}
			SearchTask task = (SearchTask) player.getMetadata("kiosk-searchtask").get(0).value();
			if (task == null) {
				throw new Exception("You do not have any search results");
			}
			if (!task.isFinished()) {
				throw new Exception("Your search is still in progress");
			}

			// get the page number
			int id = -1;
			if (args.size() > 0) {
				try {
					id = Integer.parseInt(args.peek());
				} catch (IllegalArgumentException ex) {
					throw new Exception("Invalid number: " + args.peek());
				}
			}

			if (id == -1) {
				if (task.getResults().size() > 1) {
					throw new Exception("Expected a number");
				} else {
					id = 1;
				}
			}
			if (id > task.getResults().size()) {
				throw new Exception("There are only " + task.getResults().size() + " results");
			}
			Shop result = task.getResults().get(id-1);
			String[] lines = InfoCommand.renderShopInfo(plugin, player, result).toArray(new String[0]);
			plugin.setSelectedShop(player, result);
			player.sendMessage(lines);
			return true;
		}
	}

	private class PageCommand extends PlayerCommand {
		protected PageCommand(Kiosk plugin) {
			super(plugin, "page", "view a specific page of results", "kiosk.command.results", new String[] {"page help"});
		}

		@Override
		public List<String> onTabComplete(Player player, Command command, String label, LinkedList<String> args) {
			return new ArrayList<String>();
		}

		@Override
		public boolean onCommand(Player player, Command command, String label, LinkedList<String> args) throws Exception {
			// get the search results
			if (!player.hasMetadata("kiosk-searchtask")) {
				throw new Exception("You do not have any search results");
			}
			SearchTask task = (SearchTask) player.getMetadata("kiosk-searchtask").get(0).value();
			if (task == null) {
				throw new Exception("You do not have any search results");
			}
			if (!task.isFinished()) {
				throw new Exception("Your search is still in progress");
			}

			// get the page number
			int page = 1;
			if (args.size() > 0) {
				try {
					page = Integer.parseInt(args.peek());
				} catch (IllegalArgumentException ex) {
					throw new Exception("Invalid page number: " + args.peek());
				}
			}
			task.showResults(page);
			return true;
		}
	}
}
