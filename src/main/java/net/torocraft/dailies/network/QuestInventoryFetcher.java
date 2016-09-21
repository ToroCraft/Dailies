package net.torocraft.dailies.network;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestInventoryFetcher {
	
	private static final String requestMethod = "GET";
	private final String username;
	private String path;
	private DailiesRequest request;
	private String jsonResponse;
	private DailyQuest[] aQuests;
	private Set<DailyQuest> quests;
	
	public QuestInventoryFetcher(String username) {
		this.username = username;
	}

	public Set<DailyQuest> getQuestInventory() throws DailiesException {
		buildPath();
		requestQuestInventory();
		parseResponse();
		return quests;
	}
	
	private void buildPath() {
		path = username + Transmitter.PATH_QUESTS;
	}
	
	private void requestQuestInventory() throws DailiesException {
		new Transmitter(path, request.serialize(), requestMethod).sendRequest();
	}

	private void parseResponse() {
		quests = new HashSet<DailyQuest>();
		if (jsonResponse == null) {
			return;
		}
		Gson gson = new GsonBuilder().create();
		aQuests = gson.fromJson(jsonResponse, DailyQuest[].class);
		for (DailyQuest quest : aQuests) {
			quests.add(quest);
		}
	}

}
