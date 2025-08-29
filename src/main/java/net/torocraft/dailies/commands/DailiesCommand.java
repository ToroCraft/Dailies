package net.torocraft.dailies.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.attachments.DailiesAttachmentTypes;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DailiesCommand {

    public static class PlayerDailyQuests {
    public ServerPlayer player = null;
        public IDailiesCapability playerDailiesCapability;
        public List<DailyQuest> openDailyQuests = null;
        public List<DailyQuest> acceptedDailyQuests = null;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("test")
        .then(Commands.literal("one").executes((y) -> test(y.getSource()))));

    dispatcher.register(Commands.literal("dailies")
        .then(Commands.literal("list").executes((c) -> listDailyQuests(c.getSource())))
        .then(Commands.literal("accept")
            .then(Commands.argument("Quest Number", IntegerArgumentType.integer(0)).executes((a) -> acceptQuest(a.getSource(), IntegerArgumentType.getInteger(a, "Quest Number")))))
        .then(Commands.literal("abandon")
            .then(Commands.argument("Quest Number", IntegerArgumentType.integer(0)).executes((a) -> abandonQuest(a.getSource(), IntegerArgumentType.getInteger(a, "Quest Number")))))
        .then(Commands.literal("spawn")
            .then(Commands.literal("bailey").executes((c) -> spawnBailey(c.getSource()))))
        .then(Commands.literal("gui").executes((c) -> openBaileyGui(c.getSource())))
        .then(Commands.literal("config").executes((c) -> showConfigHelp(c.getSource())))
    );
    }

    private static int test(CommandSourceStack source) {
        PacketHandler.getQuests(QuestsFilter.AVAILABLE);
        return 0;
    }

    private static int spawnBailey(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        net.minecraft.server.level.ServerLevel world = (net.minecraft.server.level.ServerLevel) player.level();
        if(!world.isClientSide) {
            BlockPos pos = new BlockPos((int)player.getX(), (int)player.getY(), (int)player.getZ());
            EntityBailey bailey = new EntityBailey(EntityRegistryHandler.BAILEY.get(), world);
            if (bailey != null) {
                bailey.setPos(pos.getX(), pos.getY(), pos.getZ());
                world.addFreshEntity(bailey);
            }
        }
        return 0;
    }

    private static int listDailyQuests(CommandSourceStack source) throws CommandSyntaxException {
        PlayerDailyQuests questData = setupQuestsData(source);
        String dailiesList = buildDailiesListText(questData);
        questData.player.sendSystemMessage(Component.literal(dailiesList));
        
        // Force synchronization with client GUI by sending updated quest data
        if (questData.playerDailiesCapability != null) {
            questData.playerDailiesCapability.sendAcceptedQuestsToClient(questData.player);
            // Also send available quests to client
            net.torocraft.dailies.network.PacketHandler.questsUpdate(
                questData.player, 
                net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter.AVAILABLE, 
                questData.playerDailiesCapability.getAvailableQuests()
            );
        }

        return 0;
    }

    private static PlayerDailyQuests setupQuestsData(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        if (player == null)
            return null;

        PlayerDailyQuests d = new PlayerDailyQuests();

        d.player = player;
        d.playerDailiesCapability = d.player.getData(DailiesAttachmentTypes.DAILIES_DATA);

        if (d.playerDailiesCapability != null) {
            d.openDailyQuests = new ArrayList<DailyQuest>(d.playerDailiesCapability.getAvailableQuests());
            d.acceptedDailyQuests = new ArrayList<DailyQuest>(d.playerDailiesCapability.getAcceptedQuests());
        }

        return d;
    }

    private static String buildDailiesListText(PlayerDailyQuests d) {

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

    private static int abandonQuest(CommandSourceStack source, int questId) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        PlayerDailyQuests d = setupQuestsData(source);
        DailyQuest quest = null;

        try {
            quest = d.acceptedDailyQuests.get(questId);
        } catch (Exception ex) {}

        if(quest != null && d.playerDailiesCapability != null) {
            try {
                DailyQuest q = d.acceptedDailyQuests.get(questId);
                d.playerDailiesCapability.abandonQuest(player, q);
                d.player.sendSystemMessage(Component.literal("Quest " + fromIndex(questId) + " " + q.getDisplayName() + " abandoned"));
                
                // Force synchronization with client
                d.playerDailiesCapability.sendAcceptedQuestsToClient(player);
                net.torocraft.dailies.network.PacketHandler.questsUpdate(
                    player, 
                    net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter.AVAILABLE, 
                    d.playerDailiesCapability.getAvailableQuests()
                );
            } catch (Exception ex) {
                d.player.sendSystemMessage(Component.literal("Error occurred when trying to abandon quest"));
            }
        } else {
            d.player.sendSystemMessage(Component.literal("Quest Not Accepted"));
        }
        return 0;
    }

    private static int acceptQuest(CommandSourceStack source, int questId) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        PlayerDailyQuests d = setupQuestsData(source);
        DailyQuest quest = null;

        try {
            quest = d.acceptedDailyQuests.get(questId);
        } catch (Exception ex) {}

        if(quest == null && d.playerDailiesCapability != null) {
            try {
                DailyQuest q = d.openDailyQuests.get(questId);
                d.playerDailiesCapability.acceptQuest(player, q);
                d.player.sendSystemMessage(Component.literal("Quest " + fromIndex(questId) + " " + q.getDisplayName() + " accepted"));
                
                // Force synchronization with client
                d.playerDailiesCapability.sendAcceptedQuestsToClient(player);
                net.torocraft.dailies.network.PacketHandler.questsUpdate(
                    player, 
                    net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter.AVAILABLE, 
                    d.playerDailiesCapability.getAvailableQuests()
                );
            } catch (Exception ex) {
                d.player.sendSystemMessage(Component.literal("Error occurred when trying to accept quest"));
            }
        } else {
            d.player.sendSystemMessage(Component.literal("Quest Already Accepted"));
        }
        return 0;
    }

    private static String fromIndex(int index) {
        return Integer.toString(index);
    }

    private static int openBaileyGui(CommandSourceStack source) throws CommandSyntaxException {
        Level world = source.getLevel();
        if(!world.isClientSide()) {
            ServerPlayer player = source.getPlayerOrException();
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() { return Component.translatable("Bailey GUI"); }
                @Override
                public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) { 
                    return new DailiesContainer(id, inventory); 
                }
            });
        }
        return 0;
    }
    
    private static int showConfigHelp(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            player.sendSystemMessage(Component.literal("§6Dailies Configuration:"));
            player.sendSystemMessage(Component.literal("§7Config file location: config/dailies-client.toml"));
            player.sendSystemMessage(Component.literal("§7Available settings:"));
            player.sendSystemMessage(Component.literal("§f- isOnline: Enable/disable online quest features"));
            player.sendSystemMessage(Component.literal("§7Edit the config file and restart the game to apply changes."));
            player.sendSystemMessage(Component.literal("§7Current Online Mode: §a" + net.torocraft.dailies.config.Config.isOnline));
        } catch (Exception e) {
            source.sendFailure(Component.literal("Error displaying config info."));
        }
        return 0;
    }
}
