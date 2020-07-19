package net.torocraft.dailies.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.gui.BaileyInventoryContainer;
import net.torocraft.dailies.quests.DailyQuest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DailiesCommand {

    public static class PlayerDailyQuests {
        public PlayerEntity player = null;
        public LazyOptional<IDailiesCapability> playerDailiesCapability;
        public List<DailyQuest> openDailyQuests = null;
        public List<DailyQuest> acceptedDailyQuests = null;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("spawn")
                .then(Commands.literal("bailey").executes((y) -> spawnBailey(y.getSource()))));

        dispatcher.register(Commands.literal("dailies")
                .then(Commands.literal("list").executes((y) -> listDailyQuests(y.getSource())))
                .then(Commands.literal("accept")
                        .then(Commands.argument("Quest Number", new QuestNumberArgument()).executes((a) -> acceptQuest(a.getSource(), a.getArgument("Quest Number", Integer.class)))))
                .then(Commands.literal("abandon")
                        .then(Commands.argument("Quest Number", new QuestNumberArgument()).executes((a) -> abandonQuest(a.getSource(), a.getArgument("Quest Number", Integer.class)))))
        );
    }

    private static int spawnBailey(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.asPlayer();
        World world = player.world;
        if(!world.isRemote) {
            BlockPos pos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
            EntityRegistryHandler.BAILEY.get().spawn(world, null, null, pos, SpawnReason.COMMAND, false, false);
        }

        return 0;
    }

    private static int listDailyQuests(CommandSource source) throws CommandSyntaxException {
        PlayerDailyQuests questData = setupQuestsData(source);
        String dailiesList = buildDailiesListText(questData);
        questData.player.sendMessage(new StringTextComponent(dailiesList), questData.player.getUniqueID());

        return 0;
    }

    private static PlayerDailyQuests setupQuestsData(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = null;
        player = source.asPlayer();

        if (player == null)
            return null;

        PlayerDailyQuests d = new PlayerDailyQuests();

        d.player = player;
        d.playerDailiesCapability = d.player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null);

        d.playerDailiesCapability.ifPresent(new NonNullConsumer<IDailiesCapability>() {
            @Override
            public void accept(@Nonnull IDailiesCapability iDailiesCapability) {
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

    private static int abandonQuest(CommandSource source, int questId) throws CommandSyntaxException {
        ServerPlayerEntity player = source.asPlayer();
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
                    d.player.sendMessage(new StringTextComponent("Quest " + fromIndex(questId) + " " + q.getDisplayName() + " abandoned"), d.player.getUniqueID());
                } catch (Exception ex) {
                    d.player.sendMessage(new StringTextComponent("Error occured when trying to abandon quest"), d.player.getUniqueID());
                }
            });
        } else {
            d.player.sendMessage(new StringTextComponent("Quest Not Accepted"), d.player.getUniqueID());
        }
        return 0;
    }

    private static int acceptQuest(CommandSource source, int questId) throws CommandSyntaxException {
        ServerPlayerEntity player = source.asPlayer();
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
                    d.player.sendMessage(new StringTextComponent("Quest " + fromIndex(questId) + " " + q.getDisplayName() + " accepted"), d.player.getUniqueID());
                } catch (Exception ex) {
                    d.player.sendMessage(new StringTextComponent("Error occured when trying to accept quest"), d.player.getUniqueID());
                }
            });
        } else {
            d.player.sendMessage(new StringTextComponent("Quest Already Accepted"), d.player.getUniqueID());
        }
        return 0;
    }

    private static String fromIndex(int index) {
        return Integer.toString(index);
    }

    private void openBaileyGui(CommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();

        if(!world.isRemote()) {
            ServerPlayerEntity player = source.asPlayer();
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName()
                { return new TranslationTextComponent("Bailey GUI"); }

                @Override
                public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player)
                { return new BaileyInventoryContainer(id, inventory, player); }
            });
        }
    }

    /* Command Argument Classes */

    public static class QuestNumberArgument implements ArgumentType<Integer> {
        @Override
        public Integer parse(StringReader reader) throws CommandSyntaxException {
            return reader.readInt();
        }
    }
}
