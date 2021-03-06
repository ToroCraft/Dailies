package net.torocraft.dailies;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.entities.Entities;
import net.torocraft.dailies.generation.BaileysShopVillagePiece;
import net.torocraft.dailies.generation.VillageHandlerBailey;
import net.torocraft.dailies.gui.DailiesGuiHandler;
import net.torocraft.dailies.messages.DailiesPacketHandler;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent e) {
		CapabilityDailiesHandler.register();
		DailiesPacketHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(DailiesMod.instance, new DailiesGuiHandler());
		MapGenStructureIO.registerStructureComponent(BaileysShopVillagePiece.class, "baileyshop");
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageHandlerBailey());
	}

	public void init(FMLInitializationEvent e) {
		Entities.init();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
