package net.torocraft.dailies.entities;

public class Entities {

	public static void init() {
		int id = 50;
		EntityBailey.init(id++);
	}

	public static void registerRenders() {
		EntityBailey.registerRenders();
	}

}
