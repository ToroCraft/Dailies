package net.torocraft.dailies.network.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.entity.player.Player;

public class DailiesRequest {
	
	public String modVersion;
	public String playerId;
	
	public DailiesRequest(Player player) {
		modVersion = "11";//DailiesMod.version;
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
