package com.norcode.bukkit.kiosk.command;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum StaffPermission {

	DISPLAY("display", "turn the item & add/break signs", true),
	MANAGE_STOCK("managestock", "manage shop stock", false),
	ADD_STOCK("addstock", "add stock to the shop", true),
	DEPOSIT("deposit", "deposit money", true),
	WITHDRAW("withdraw", "withdraw money", false),
	CHANGE_ITEM("item", "Change the item", false),
	RENAME("rename", "change the name", false),
	DESTROY("destroy", "destroy the shop", false),
	PRICE("price", "change the quantity and/or price", false),
	PRIVATE("private", "set/toggle shop privacy", false),
	OWNER_ONLY("owneronly", "used internally for owner-only actions", false),
	MANAGE_STAFF("managestaff", "manage staff", false),
	SET_ICON("seticon", "change the shop icon", false);

	private String display;
	private String description;
	private boolean defaultValue;

	StaffPermission(String display, String description, boolean defaultValue) {
		this.defaultValue = defaultValue;
		this.display = display;
		this.description = description;
	};

	public static EnumSet<StaffPermission> getDefaults() {
		EnumSet<StaffPermission> perms = EnumSet.noneOf(StaffPermission.class);
		for (StaffPermission p: values()) {
			if (p.defaultValue) {
				perms.add(p);
			}
		}
		return perms;
	}

	public String getDisplay() {
		return display;
	}

	public String getDescription() {
		return description;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public static EnumSet<StaffPermission> fromStringList(List<String> list) {
		List<String> lower = new ArrayList<String>(list.size());
		for (String s: list) {
			lower.add(s.toLowerCase());
		}

		EnumSet<StaffPermission> perms = EnumSet.noneOf(StaffPermission.class);
		for (StaffPermission p: values()) {
			if (lower.contains(p.getDisplay().toLowerCase())) {
				perms.add(p);
			}
		}
		return perms;
	}

	public static void setDefaults(List<String> stringList) {
		List<String> lower = new ArrayList<String>(stringList.size());
		for (String s: stringList) {
			lower.add(s.toLowerCase());
		}
		for (StaffPermission p: values()) {
			if (lower.contains(p.getDisplay().toLowerCase())) {
				p.defaultValue = true;
			} else {
				p.defaultValue = false;
			}
		}
	}
}
