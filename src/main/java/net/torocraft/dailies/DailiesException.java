package net.torocraft.dailies;

import net.minecraft.util.text.TextComponentString;

public class DailiesException extends Exception {
	private static final long serialVersionUID = 521954328281793833L;
	
	private DailiesException(String message) {
		super(message);
	}
	
	public static DailiesException SYSTEM_ERROR(Exception e) {
		return new DailiesException(DailiesMod.metadata.name + " Error: " + e.getMessage());
	}
	
	public static DailiesException ACCEPTED_QUEST_LIMIT_HIT() {
		return new DailiesException("You've already accepted " + DailiesMod.MAX_QUESTS_ACCEPTABLE + " quests.");
	}
	
	public static DailiesException NETWORK_ERROR(Exception e) {
		return new DailiesException("Unable to connect to the dailies service due to the following error: " + e.getMessage());
	}
	
	public static DailiesException SERVICE_ERROR(String message) {
		return new DailiesException("Error from Dailies Service: " + message);
	}
	
	public TextComponentString getMessageAsTextComponent() {
		return new TextComponentString(getMessage());
	}

}
