package net.torocraft.dailies.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.torocraft.dailies.DailiesMod;

public class DailiesRequest {
	
	public String modVersion;
	public String accessToken;
	
	public DailiesRequest() {
		modVersion = DailiesMod.metadata.version;
		accessToken = Minecraft.getMinecraft().getSession().getToken();
		System.out.println("accessToken = " + accessToken);
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
