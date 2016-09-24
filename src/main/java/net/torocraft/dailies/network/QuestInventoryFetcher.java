package net.torocraft.dailies.network;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.GsonBuilder;

import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestInventoryFetcher {
	
	private static final String requestMethod = "POST";
	private final String username;
	private String path;
	private DailiesRequest request;
	private String jsonResponse;
	private Set<DailyQuest> quests;
	private DailiesTransmitter transmitter;
	
	public QuestInventoryFetcher(String username) {
		this.username = username;
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
		request = new DailiesRequest();
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
		GsonBuilder gson = new GsonBuilder();
		try {
			quests = gson.create().fromJson(jsonResponse, QuestInventoryResponse.class).quests;
		} catch (Exception e) {
			throw DailiesException.SYSTEM_ERROR(e);
		}
		
	}

}
