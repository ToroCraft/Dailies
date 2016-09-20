package net.torocraft.dailies.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.RandomQuestGenerator;

public class QuestInventoryFetcher {

	private static final String SERVICE_URL = "http://www.minecraftdailies.com";
	private static final String PATH_QUESTS = "/quests";
	
	private final String username;
	private String jsonResponse;
	private DailyQuest[] aQuests;
	
	public QuestInventoryFetcher(String username) {
		this.username = username;
	}

	public Set<DailyQuest> getQuestInventory() {
		
		
		
		try {
			return parseResponse(requestQuestInventory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return generateOfflineQuests();
	}

	private Set<DailyQuest> parseResponse() {
		Set<DailyQuest> dailyQuests = new HashSet<DailyQuest>();
		if (jsonString == null) {
			return dailyQuests;
		}
		Gson gson = new GsonBuilder().create();
		aQuests = gson.fromJson(jsonString, DailyQuest[].class);
		for (DailyQuest quest : aQuests) {
			dailyQuests.add(quest);
		}
		return dailyQuests;
	}
	
	private Set<DailyQuest> generateOfflineQuests() {
		RandomQuestGenerator questGenerator = new RandomQuestGenerator();
		Set<DailyQuest> dailyQuests = questGenerator.generateQuests();
		return dailyQuests;
	}
}
