package com.norcode.bukkit.kiosk.command;

import com.norcode.bukkit.kiosk.Kiosk;
import com.norcode.bukkit.kiosk.command.subcommands.BalanceCommand;
import com.norcode.bukkit.kiosk.command.subcommands.BuyCommand;
import com.norcode.bukkit.kiosk.command.subcommands.DepositCommand;
import com.norcode.bukkit.kiosk.command.subcommands.FindCommand;
import com.norcode.bukkit.kiosk.command.subcommands.InfoCommand;
import com.norcode.bukkit.kiosk.command.subcommands.ResultsCommand;
import com.norcode.bukkit.kiosk.command.subcommands.SelectCommand;
import com.norcode.bukkit.kiosk.command.subcommands.SellCommand;
import com.norcode.bukkit.kiosk.command.subcommands.SetCommand;
import com.norcode.bukkit.kiosk.command.subcommands.SpawnCommand;
import com.norcode.bukkit.kiosk.command.subcommands.staff.StaffCommand;
import com.norcode.bukkit.kiosk.command.subcommands.StockCommand;
import com.norcode.bukkit.kiosk.command.subcommands.TeleportCommand;
import com.norcode.bukkit.kiosk.command.subcommands.WithdrawCommand;

public class KioskCommand extends BaseCommand {
	public KioskCommand(Kiosk plugin) {
		super(plugin, "kiosk", "kiosk!", "kiosk.command", null);
		registerSubcommand(new SpawnCommand(plugin));
		registerSubcommand(new SetCommand(plugin));
		registerSubcommand(new StockCommand(plugin));
		registerSubcommand(new StaffCommand(plugin));
		registerSubcommand(new InfoCommand(plugin));
		registerSubcommand(new DepositCommand(plugin));
		registerSubcommand(new WithdrawCommand(plugin));
		registerSubcommand(new BalanceCommand(plugin));
		registerSubcommand(new TeleportCommand(plugin));
		registerSubcommand(new SelectCommand(plugin));
		registerSubcommand(new FindCommand(plugin));
		registerSubcommand(new ResultsCommand(plugin));
		registerSubcommand(new BuyCommand(plugin));
		registerSubcommand(new SellCommand(plugin));
		registerSubcommand(new DestroyCommand(plugin));
	}
}


