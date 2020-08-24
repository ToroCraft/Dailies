package net.torocraft.dailies.network.remote;

import net.minecraft.entity.player.PlayerEntity;
import net.torocraft.dailies.DailiesException;

public class ProgressUpdater {

	private static final String path_progress = "/progress/";
	private static final String requestMethod = "POST";
	private final PlayerEntity player;
	private final String username;
	private final String questId;
	private final int progress;
	private String path;
	private DailiesRequest request;
	private DailiesTransmitter transmitter;
	
	public ProgressUpdater(PlayerEntity player, String questId, int progress) {
		this.player = player;
		this.username = player.getName().toString();
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
		request = new DailiesRequest(player);
	}

	private void buildPath() {
		path = username + DailiesTransmitter.PATH_QUESTS + "/" + questId + path_progress + progress;
	}

	private void buildTransmitter() {
		transmitter = new DailiesTransmitter(path, request.serialize(), requestMethod);
	}

	private void sendRequest() throws DailiesException {
		transmitter.sendRequest();
	}
	
}
