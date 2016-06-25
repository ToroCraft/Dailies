package net.torocraft.dailies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.gui.GuiDailyProgressIndicators;
import net.torocraft.dailies.messages.ClientMessageHandler;
import net.torocraft.dailies.messages.StatusUpdateToClient;
import net.torocraft.dailies.quests.DailyQuest;

public class ClientProxy extends CommonProxy {

	GuiDailyProgressIndicators dailyGui = new GuiDailyProgressIndicators();

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		CommonProxy.simpleNetworkWrapper.registerMessage(ClientMessageHandler.class, StatusUpdateToClient.class, CommonProxy.DAILY_RESPONSE_ID, Side.CLIENT);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
		MinecraftForge.EVENT_BUS.register(dailyGui);
	}

	@Override
	public void displayQuestProgress(DailyQuest quest) {
		dailyGui.setQuest(quest);
	}

}