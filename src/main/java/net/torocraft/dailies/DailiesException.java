package net.torocraft.dailies;

import net.minecraft.util.text.TextComponentString;

public class DailiesException extends Exception {
	private static final long serialVersionUID = 521954328281793833L;
	
	public DailiesException(String message) {
		super(message);
	}
	
	public static DailiesException ACCEPTED_QUEST_LIMIT_HIT() {
		return new DailiesException("You've already accepted " + DailiesMod.MAX_QUESTS_ACCEPTABLE + " quests.");
	}
	
	public TextComponentString getMessageAsTextComponent() {
		return new TextComponentString(getMessage());
	}

}
