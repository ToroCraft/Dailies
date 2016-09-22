package net.torocraft.dailies.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.torocraft.dailies.DailiesMod;

public class DailiesRequest {
	
	public String modVersion;
	public String accessToken;
	public String playerId;
	
	public DailiesRequest() {
		modVersion = DailiesMod.metadata.version;
		Session session = Minecraft.getMinecraft().getSession();
		accessToken = session.getToken();
		playerId = session.getPlayerID();
		System.out.println("playerId = " + playerId);
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
