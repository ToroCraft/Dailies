package net.torocraft.dailies.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.entity.player.EntityPlayer;
import net.torocraft.dailies.DailiesMod;

public class DailiesRequest {
	
	public String modVersion;
	public String playerId;
	
	public DailiesRequest(EntityPlayer player) {
		modVersion = DailiesMod.metadata.version;
		playerId = String.valueOf(player.getGameProfile().getId());
	}
	
	public String serialize() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public static DailiesRequest deserialize(String json) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(json, DailiesRequest.class);
	}
	
}
