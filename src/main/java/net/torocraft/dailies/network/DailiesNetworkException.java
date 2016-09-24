package net.torocraft.dailies.network;

import net.torocraft.dailies.DailiesException;

public class DailiesNetworkException extends DailiesException {
	private static final long serialVersionUID = 521954328281793833L;
	
	public DailiesNetworkException(Exception e) {
		super("Unable to connect to the dailies service.");
	}

}
