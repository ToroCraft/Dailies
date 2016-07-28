package net.torocraft.dailies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesCommand implements ICommand {

	private static final TextComponentString invalidCommand = new TextComponentString("Invalid Command");
	private static final TextComponentString questNotFound = new TextComponentString("Quest not Found");
	private static final TextComponentString loadingDailies = new TextComponentString("Loading Dailies...");
	private List<String> aliases = new ArrayList<String>();

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "dailies";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "dailies <command> <id>";
	}

	@Override
	public List<String> getCommandAliases() {
		return this.aliases;
	}

	public static class PlayerDailyQuests {
		public EntityPlayer player = null;
		public IDailiesCapability playerDailiesCapability;
		public List<DailyQuest> openDailyQuests = null;
		public List<DailyQuest> acceptedDailyQuests = null;
	}

	@Override
	public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PlayerDailyQuests questsData = setupQuestsData(server, sender);

				if (args.length == 0) {
					listDailyQuests(questsData);
				} else if (args.length == 2) {
					handleSubCommand(questsData, args);
				} else if(args.length == 1 && args[0].equals("gui")) {
					questsData.player.openGui(DailiesMod.instance, DailiesGuiHandler.getGuiID(), questsData.player.worldObj, questsData.player.chunkCoordX, questsData.player.chunkCoordY, questsData.player.chunkCoordZ);
				} else {
					sender.addChatMessage(invalidCommand);
				}
			}
		}).start();
	}

	private void handleSubCommand(PlayerDailyQuests d, String[] args) {
		String command = args[0];
		int index = toIndex(args[1]);

		if (!validCommand(command)) {
			d.player.addChatMessage(invalidCommand);
			return;
		}
		
		DailyQuest quest = null;

		if (command.equalsIgnoreCase("abandon")) {
			
			try {
				quest = d.acceptedDailyQuests.get(index);
			} catch (Exception ex) {}
			
			if (quest == null) {
				d.player.addChatMessage(questNotFound);
			} else {
				new DailiesRequester().abandonQuest(d.player.getName(), quest.id);
				d.player.addChatMessage(new TextComponentString("Quest " + fromIndex(index) + " " + quest.getDisplayName() + " abandoned"));
			}

		} else if (command.equalsIgnoreCase("accept")) {
			try {
				quest = d.openDailyQuests.get(index);
			} catch (Exception ex) {}
			
			if (quest == null) {
				d.player.addChatMessage(questNotFound);
			} else {
				new DailiesRequester().acceptQuest(d.player.getName(), quest.id);
				d.player.addChatMessage(new TextComponentString("Quest " + fromIndex(index) + " " + quest.getDisplayName() + " accepted"));
			}
		}
	}

	private PlayerDailyQuests setupQuestsData(MinecraftServer server, ICommandSender sender) {

		if (!(sender instanceof EntityPlayer)) {
			return null;
		}

		PlayerDailyQuests d = new PlayerDailyQuests();

		d.player = (EntityPlayer) sender;
		d.playerDailiesCapability = d.player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
		Set<DailyQuest> serversDailyQuests = getDailyQuests(d.player);

		d.openDailyQuests = new ArrayList<DailyQuest>();
		d.acceptedDailyQuests = new ArrayList<DailyQuest>();

		for (DailyQuest quest : serversDailyQuests) {
			if ("available".equals(quest.status)) {
				d.openDailyQuests.add(quest);
			} else if ("accepted".equals(quest.status)) {
				d.acceptedDailyQuests.add(quest);
			}
		}
		
		d.playerDailiesCapability.setAcceptedQuests(new HashSet<DailyQuest>(d.acceptedDailyQuests));
		d.playerDailiesCapability.writeNBT();

		return d;
	}

	private void listDailyQuests(PlayerDailyQuests questsData) {
		String dailiesList = buildDailiesListText(questsData);
		questsData.player.addChatMessage(new TextComponentString(dailiesList));
	}

	private Set<DailyQuest> getDailyQuests(EntityPlayer player) {
		player.addChatMessage(loadingDailies);
		DailiesRequester requester = new DailiesRequester();
		Set<DailyQuest> dailies = requester.getQuestInventory(player.getName());
		return dailies;
	}

	private int toIndex(String string) {
		try {
			return Integer.valueOf(string) - 1;
		} catch (Exception e) {
			return 0;
		}
	}
	
	private String fromIndex(int index) {
		return Integer.toString(index + 1);
	}

	private boolean validCommand(String command) {
		if (command.equals("abandon") || command.equals("accept") || command.equals("gui")) {
			return true;
		}
		return false;
	}

	private String buildDailiesListText(PlayerDailyQuests d) {

		StringBuilder builder = new StringBuilder();

		if (d.openDailyQuests.size() < 1) {
			builder.append("No new daily quests found.\n");
		} else {
			for (int i = 0; i < d.openDailyQuests.size(); i++) {
				builder.append("(").append(i + 1).append(") OPEN :: ");
				builder.append(d.openDailyQuests.get(i).getDisplayName());
				builder.append("\n");
			}
		}

		builder.append("\n");

		if (d.acceptedDailyQuests.size() < 1) {
			builder.append("You have no accepted quests.\n");
		} else {
			for (int i = 0; i < d.acceptedDailyQuests.size(); i++) {
				builder.append("(").append(i + 1).append(") ACCEPTED :: ");
				builder.append(d.acceptedDailyQuests.get(i).getDisplayName());
				builder.append("\n");
			}
		}

		return builder.toString();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		List<String> tabOptions = new ArrayList<String>();

		if (args.length == 0) {
			tabOptions.add("dailies");
		} else if (args.length == 1) {
			String command = args[0];

			if (command.length() > 2) {
				tabOptions.add(getTabbedCommand(command));
			}
		}

		return tabOptions;
	}

	private String getTabbedCommand(String command) {
		if (command.startsWith("ac")) {
			return "accept";
		} else if (command.startsWith("ab"))
			return "abandon";

		return "";
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
