package net.torocraft.dailies.network;

import net.torocraft.dailies.DailiesException;

public class ProgressUpdater {

	private static final String path_progress = "/progress/";
	private static final String requestMethod = "POST";
	private final String username;
	private final String questId;
	private final int progress;
	private String path;
	private DailiesRequest request;
	private Transmitter transmitter;
	
	public ProgressUpdater(String username, String questId, int progress) {
		this.username = username;
		this.questId = questId;
		this.progress = progress;
	}
	
	public void update() throws DailiesException {
		buildRequest();
		buildPath();
		buildTransmitter();
		sendRequest();
	}

	private void buildRequest() {
		request = new DailiesRequest();
	}

	private void buildPath() {
		path = username + Transmitter.PATH_QUESTS + "/" + questId + path_progress + progress;
	}

	private void buildTransmitter() {
		transmitter = new Transmitter(path, request.serialize(), requestMethod);
	}

	private void sendRequest() throws DailiesException {
		transmitter.sendRequest();
	}
	
}
