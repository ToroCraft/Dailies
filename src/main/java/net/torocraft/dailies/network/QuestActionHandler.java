package net.torocraft.dailies.network;

import net.minecraft.entity.player.EntityPlayer;
import net.torocraft.dailies.DailiesException;

public class QuestActionHandler {

	private static enum Action {
		accept, abandon, complete;
	}
	
	private static final String requestMethod = "POST";
	private final EntityPlayer player;
	private final String username;
	private final String questId;
	private Action action;
	private String path;
	private DailiesRequest request;
	private DailiesTransmitter transmitter;
	
	public QuestActionHandler(EntityPlayer player, String questId) {
		this.player = player;
		this.username = player.getName();
		this.questId = questId;
	}
	
	public void accept() throws DailiesException {
		action = Action.accept;
		doAction();
	}
	
	public void abandon() throws DailiesException {
		action = Action.abandon;
		doAction();
	}
	
	public void complete() throws DailiesException {
		action = Action.complete;
		doAction();
	}
	
	private void doAction() throws DailiesException {
		request = new DailiesRequest(player);
		path = username + DailiesTransmitter.PATH_QUESTS + "/" + questId + "/" + action;
		transmitter = new DailiesTransmitter(path, request.serialize(), requestMethod);
		transmitter.sendRequest();
	}
	
}
