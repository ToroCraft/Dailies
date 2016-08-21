package net.torocraft.dailies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.torocraft.dailies.quests.DailyQuest;

public class DailiesRequester {


	private static final String SERVICE_URL = "http://www.minecraftdailies.com";
	private static final String PATH_QUESTS = "/quests";
	private static final String PATH_ACCEPT = "/accept";
	private static final String PATH_ABANDON = "/abandon";
	private static final String PATH_COMPLETE = "/complete";
	private static final String PATH_PROGRESS = "/progress";
	
	private String username;
	private String questId;
	private String actionPath;
	
	public void acceptQuest(String username, String questId) {
		storeParams(username, questId);
		actionPath = PATH_ACCEPT;
		requestQuestAction();
	}

	private void storeParams(String username, String questId) {
		this.username = username;
		this.questId = questId;
	}
	
	private String requestQuestAction() {
		HttpURLConnection conn = null;
		String json = null;
		try {
			URL url = new URL(SERVICE_URL + "/" + username + PATH_QUESTS + "/" + questId + actionPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			json = s(conn.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return json;
	}
	
	public void abandonQuest(String username, String questId) {
		storeParams(username, questId);
		actionPath = PATH_ABANDON;
		requestQuestAction();
	}
	
	public void completeQuest(String username, String questId) {
		storeParams(username, questId);
		actionPath = PATH_COMPLETE;
		requestQuestAction();
	}
	
	public void progressQuest(String username, String questId, int progress) {
		storeParams(username, questId);
		actionPath = PATH_PROGRESS;
		requestProgressUpdate(progress);
	}
	
	private String requestProgressUpdate(int progress) {
		HttpURLConnection conn = null;
		String json = null;
		try {
			URL url = new URL(SERVICE_URL + "/" + username + PATH_QUESTS + "/" + questId + actionPath + "/" + progress);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			json = s(conn.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return json;
	}

	public Set<DailyQuest> getQuestInventory(String username) {
		this.username = username;
		try {
			return parseResponse(requestQuestInventory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashSet<DailyQuest>(0);
	}

	private String requestQuestInventory() throws IOException {
		HttpURLConnection conn = null;
		String json = null;
		try {
			URL url = new URL(SERVICE_URL + "/" + username + PATH_QUESTS);
			conn = (HttpURLConnection) url.openConnection();
			json = s(conn.getInputStream());
		}finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return json;
	}

	private String s(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String read;
		while ((read = br.readLine()) != null) {
			sb.append(read);
		}
		br.close();
		return sb.toString();
	}

	private Set<DailyQuest> parseResponse(String jsonString) {
		Set<DailyQuest> dailyQuests = new HashSet<DailyQuest>();
		int maxQuests = 5;
		int i = 0;

		if (jsonString == null) {
			return dailyQuests;
		}
		Gson gson = new GsonBuilder().create();
		DailyQuest[] aQuests = gson.fromJson(jsonString, DailyQuest[].class);
		for (DailyQuest quest : aQuests) {
			dailyQuests.add(quest);
			i++;
			if (i >= maxQuests) {
				return dailyQuests;
			}
		}
		return dailyQuests;
	}
}
