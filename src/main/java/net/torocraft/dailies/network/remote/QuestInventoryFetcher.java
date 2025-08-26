package net.torocraft.dailies.network.remote;

import com.google.gson.GsonBuilder;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.player.Player;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestInventoryFetcher {
	
	private static final String requestMethod = "POST";
	private final Player player;
	private final String username;
	private String path;
	private DailiesRequest request;
	private String jsonResponse;
	private Set<DailyQuest> quests;
	private DailiesTransmitter transmitter;
	
	public QuestInventoryFetcher(Player player) {
		this.player = player;
		this.username = player.getName().getString();
	}

	public Set<DailyQuest> getQuestInventory() throws DailiesException {
		buildPath();
		buildRequest();
		requestQuestInventory();
		parseResponse();
		return quests;
	}
	
	private void buildPath() {
		path = username + DailiesTransmitter.PATH_QUESTS;
	}
	
	private void buildRequest() {
		request = new DailiesRequest(player);
		System.out.println("DAILIES REQUEST modVersion = " + request.modVersion);
	}
	
	private void requestQuestInventory() throws DailiesException {
		transmitter = new DailiesTransmitter(path, request.serialize(), requestMethod);
		jsonResponse = transmitter.sendRequest();
	}

	private void parseResponse() throws DailiesException {
		quests = new HashSet<DailyQuest>();
		if (jsonResponse == null) {
			return;
		}
		
		// Log the actual response for debugging
		System.out.println("DAILIES JSON Response: " + jsonResponse);
		
		GsonBuilder gson = new GsonBuilder();
		try {
			QuestInventoryResponse response = gson.create().fromJson(jsonResponse, QuestInventoryResponse.class);
			if (response != null && response.quests != null) {
				quests = response.quests;
			}
		} catch (Exception e) {
			// Instead of throwing an exception that shows in chat, log the error and use network exception
			// This will trigger the fallback to random quest generation
			System.err.println("DAILIES JSON parsing failed: " + e.getMessage());
			System.err.println("Response was: " + jsonResponse);
			throw new DailiesNetworkException(e);
		}
		
	}

}
