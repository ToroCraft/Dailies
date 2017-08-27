package net.torocraft.dailies;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.gui.DailiesGuiHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesCommand extends CommandBase {

	private static final TextComponentString invalidCommand = new TextComponentString("Invalid Command");
	private static final TextComponentString questNotFound = new TextComponentString("Quest not Found");
	
	private List<String> aliases = new ArrayList<String>();

	@Override
	public String getName() {
		return "dailies";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "dailies <command> <id>";
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	public static class PlayerDailyQuests {
		public EntityPlayer player = null;
		public IDailiesCapability playerDailiesCapability;
		public List<DailyQuest> openDailyQuests = null;
		public List<DailyQuest> acceptedDailyQuests = null;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
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
					try {
						handleSubCommand(questsData, args);
					} catch (DailiesException e) {
						questsData.player.sendMessage(e.getMessageAsTextComponent());
					}
				} else if(args.length == 1 && args[0].equals("gui")) {
					questsData.player.openGui(DailiesMod.instance, DailiesGuiHandler.getGuiID(), questsData.player.world, sender.getPosition().getX(), sender.getPosition().getY(), sender.getPosition().getZ());
				} else if(args.length == 1 && args[0].equals("spawn")) {
					spawnBailey(server, sender);
				}  else {
					sender.sendMessage(invalidCommand);
				}
			}
		}).start();
	}
	
	private void spawnBailey(MinecraftServer server, ICommandSender sender) {
		EntityPlayer player = (EntityPlayer) sender;
		World world = player.world;
		if(!world.isRemote) {
			Entity entity = new EntityBailey(world);
			entity.setPosition(player.getPosition().getX() + 2, player.getPosition().getY(), player.getPosition().getZ());
			world.spawnEntity(entity);
		}
	}

	private void handleSubCommand(PlayerDailyQuests d, String[] args) throws DailiesException {
		String command = args[0];
		int index = toIndex(args[1]);

		if (!validCommand(command)) {
			d.player.sendMessage(invalidCommand);
			return;
		}
		
		DailyQuest quest = null;

		if (command.equalsIgnoreCase("abandon")) {
			
			try {
				quest = d.acceptedDailyQuests.get(index);
			} catch (Exception ex) {}
			
			if (quest == null) {
				d.player.sendMessage(questNotFound);
			} else {
				d.playerDailiesCapability.abandonQuest(d.player, quest);
				d.player.sendMessage(new TextComponentString("Quest " + fromIndex(index) + " " + quest.getDisplayName() + " abandoned"));
			}

		} else if (command.equalsIgnoreCase("accept")) {
			try {
				quest = d.openDailyQuests.get(index);
			} catch (Exception ex) {}
			
			if (quest == null) {
				d.player.sendMessage(questNotFound);
			} else {
				d.playerDailiesCapability.acceptQuest(d.player, quest);
				d.player.sendMessage(new TextComponentString("Quest " + fromIndex(index) + " " + quest.getDisplayName() + " accepted"));
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
		d.openDailyQuests = new ArrayList<DailyQuest>(d.playerDailiesCapability.getAvailableQuests());
		d.acceptedDailyQuests = new ArrayList<DailyQuest>(d.playerDailiesCapability.getAcceptedQuests());

		return d;
	}

	private void listDailyQuests(PlayerDailyQuests questsData) {
		String dailiesList = buildDailiesListText(questsData);
		questsData.player.sendMessage(new TextComponentString(dailiesList));
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
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
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
	
}
