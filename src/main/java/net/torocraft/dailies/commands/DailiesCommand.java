package net.torocraft.dailies.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.network.NetworkHooks;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
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
        public LazyOptional<IDailiesCapability> playerDailiesCapability;
        public List<DailyQuest> openDailyQuests = null;
        public List<DailyQuest> acceptedDailyQuests = null;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("test")
        .then(Commands.literal("one").executes((y) -> test(y.getSource()))));

    dispatcher.register(Commands.literal("spawn")
        .then(Commands.literal("bailey").executes((c) -> spawnBailey(c.getSource()))));

    dispatcher.register(Commands.literal("dailies")
        .then(Commands.literal("list").executes((c) -> listDailyQuests(c.getSource())))
        .then(Commands.literal("accept")
            .then(Commands.argument("Quest Number", IntegerArgumentType.integer(0)).executes((a) -> acceptQuest(a.getSource(), IntegerArgumentType.getInteger(a, "Quest Number")))))
        .then(Commands.literal("abandon")
            .then(Commands.argument("Quest Number", IntegerArgumentType.integer(0)).executes((a) -> abandonQuest(a.getSource(), IntegerArgumentType.getInteger(a, "Quest Number")))))
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
        net.minecraft.server.level.ServerLevel world = (net.minecraft.server.level.ServerLevel) player.level;
        if(!world.isClientSide) {
            BlockPos pos = new BlockPos(player.getX(), player.getY(), player.getZ());
            EntityRegistryHandler.BAILEY.get().spawn(world, null, null, pos, MobSpawnType.COMMAND, false, false);
        }
        return 0;
    }

    private static int listDailyQuests(CommandSourceStack source) throws CommandSyntaxException {
        PlayerDailyQuests questData = setupQuestsData(source);
        String dailiesList = buildDailiesListText(questData);
        questData.player.sendSystemMessage(Component.literal(dailiesList));
        
        // Force synchronization with client GUI by sending updated quest data
        questData.playerDailiesCapability.ifPresent(cap -> {
            cap.sendAcceptedQuestsToClient(questData.player);
            // Also send available quests to client
            net.torocraft.dailies.network.PacketHandler.questsUpdate(
                questData.player, 
                net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter.AVAILABLE, 
                cap.getAvailableQuests()
            );
        });

        return 0;
    }

    private static PlayerDailyQuests setupQuestsData(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        if (player == null)
            return null;

        PlayerDailyQuests d = new PlayerDailyQuests();

        d.player = player;
        d.playerDailiesCapability = d.player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null);

        d.playerDailiesCapability.ifPresent(new NonNullConsumer<IDailiesCapability>() {
            @Override
            public void accept(IDailiesCapability iDailiesCapability) {
                d.openDailyQuests = new ArrayList<DailyQuest>(iDailiesCapability.getAvailableQuests());
                d.acceptedDailyQuests = new ArrayList<DailyQuest>(iDailiesCapability.getAcceptedQuests());
            }
        });

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

        if(quest != null) {
            d.playerDailiesCapability.ifPresent(x -> {
                try {
                    DailyQuest q = d.acceptedDailyQuests.get(questId);
                    x.abandonQuest(player, q);
                    d.player.sendSystemMessage(Component.literal("Quest " + fromIndex(questId) + " " + q.getDisplayName() + " abandoned"));
                    
                    // Force synchronization with client
                    x.sendAcceptedQuestsToClient(player);
                    net.torocraft.dailies.network.PacketHandler.questsUpdate(
                        player, 
                        net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter.AVAILABLE, 
                        x.getAvailableQuests()
                    );
                } catch (Exception ex) {
                    d.player.sendSystemMessage(Component.literal("Error occurred when trying to abandon quest"));
                }
            });
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

        if(quest == null) {
            d.playerDailiesCapability.ifPresent(x -> {
                try {
                    DailyQuest q = d.openDailyQuests.get(questId);
                    x.acceptQuest(player, q);
                    d.player.sendSystemMessage(Component.literal("Quest " + fromIndex(questId) + " " + q.getDisplayName() + " accepted"));
                    
                    // Force synchronization with client
                    x.sendAcceptedQuestsToClient(player);
                    net.torocraft.dailies.network.PacketHandler.questsUpdate(
                        player, 
                        net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter.AVAILABLE, 
                        x.getAvailableQuests()
                    );
                } catch (Exception ex) {
                    d.player.sendSystemMessage(Component.literal("Error occurred when trying to accept quest"));
                }
            });
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
            NetworkHooks.openScreen(player, new MenuProvider() {
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
