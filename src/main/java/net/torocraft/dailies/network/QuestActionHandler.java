package net.torocraft.dailies.network;

import net.torocraft.dailies.DailiesException;

public class QuestActionHandler {
	
	private static final String requestMethod = "POST";
	private final String username;
	private final String questId;
	private final String action;
	private String path;
	private DailiesRequest request;
	private Transmitter transmitter;
	
	public QuestActionHandler(String username, String questId, String action) {
		this.username = username;
		this.questId = questId;
		this.action = action;
	}
	
	public void update() throws DailiesException {
		request = new DailiesRequest();
		path = username + Transmitter.PATH_QUESTS + "/" + questId + "/" + action;
		transmitter = new Transmitter(path, request.serialize(), requestMethod);
		transmitter.sendRequest();
	}
	
}
