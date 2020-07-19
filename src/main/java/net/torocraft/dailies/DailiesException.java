package net.torocraft.dailies;

import net.minecraft.util.text.StringTextComponent;

public class DailiesException extends Exception {
	private static final long serialVersionUID = 521954328281793833L;
	
	protected DailiesException(String message) {
		super(message);
	}
	
	public static DailiesException SYSTEM_ERROR(Exception e) {
		return new DailiesException(DailiesMod.MODID + " Error: " + e.getMessage());
	}
	
	public static DailiesException ACCEPTED_QUEST_LIMIT_HIT() {
		return new DailiesException("You've already accepted " + DailiesMod.MAX_QUESTS_ACCEPTABLE + " quests.");
	}
	
	public static DailiesException SERVICE_ERROR(String message) {
		return new DailiesException("Error from Dailies Service: " + message);
	}
	
	public StringTextComponent getMessageAsTextComponent() {
		return new StringTextComponent(getMessage());
	}

}
