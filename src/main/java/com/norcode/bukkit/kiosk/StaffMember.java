package com.norcode.bukkit.kiosk;

import com.norcode.bukkit.kiosk.command.StaffPermission;

import java.util.Set;
import java.util.UUID;

public class StaffMember {

	private UUID playerId;
	private Set<StaffPermission> permissions;

	public StaffMember(UUID uuid, Set<StaffPermission> perms) {
		playerId = uuid;
		permissions = perms;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public Set<StaffPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<StaffPermission> permissions) {
		this.permissions = permissions;
	}

	public boolean hasPermission(StaffPermission permission) {
		return permissions.contains(permission);
	}
}
