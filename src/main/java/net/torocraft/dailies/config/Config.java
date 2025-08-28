package net.torocraft.dailies.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.torocraft.dailies.DailiesMod;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
    private static final String MODID = DailiesMod.MODID;
    public static final CommonConfig COMMON;
    public static final ServerConfig SERVER;
    public static final ClientConfig CLIENT;
    public static final ModConfigSpec COMMON_CONFIG_SPEC;
    public static final ModConfigSpec SERVER_CONFIG_SPEC;
    public static final ModConfigSpec CLIENT_CONFIG_SPEC;

    static {
        final Pair<CommonConfig, ModConfigSpec> common_ = (new ModConfigSpec.Builder()).configure(CommonConfig::new);
        COMMON_CONFIG_SPEC = common_.getRight();
        COMMON = common_.getLeft();
        final Pair<ServerConfig, ModConfigSpec> server_ = (new ModConfigSpec.Builder()).configure(ServerConfig::new);
        SERVER_CONFIG_SPEC = server_.getRight();
        SERVER = server_.getLeft();
        final Pair<ClientConfig, ModConfigSpec> client_ = (new ModConfigSpec.Builder()).configure(ClientConfig::new);
        CLIENT_CONFIG_SPEC = client_.getRight();
        CLIENT = client_.getLeft();
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static void onLoad(final net.neoforged.fml.config.ModConfig config)
    {
        try {
            apply();
        } catch(Exception ex) {

        }
    }

    public static void onFileChange(final net.neoforged.fml.config.ModConfig config)
    {}

    //--------------------------------------------------------------------------------------------------------------------

    public static class ClientConfig
    {
        public final ModConfigSpec.BooleanValue isOnline;
        public final ModConfigSpec.BooleanValue showQuestsInInventory;
        public final ModConfigSpec.IntValue maxQuestsDisplayed;

        ClientConfig(ModConfigSpec.Builder builder)
        {
            builder.comment("Client-side settings that affect the user interface and experience.")
                    .push("client");
            {
                isOnline = builder
                        .comment("Enables online mode for the Dailies mod.")
                        .define("Is Online Enabled", true);
                
                showQuestsInInventory = builder
                        .comment("Show quest progress indicators in the player inventory.")
                        .define("Show Quests in Inventory", true);
                
                maxQuestsDisplayed = builder
                        .comment("Maximum number of quests to display in GUI panels.")
                        .defineInRange("Max Quests Displayed", 10, 1, 50);
            }
            builder.pop();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static class ServerConfig
    {
        public final ModConfigSpec.IntValue questResetHour;
        public final ModConfigSpec.IntValue maxAcceptedQuests;
        public final ModConfigSpec.BooleanValue allowQuestSharing;

        ServerConfig(ModConfigSpec.Builder builder)
        {
            builder.comment("Server-side settings that affect gameplay mechanics.")
                    .push("server");
            {
                questResetHour = builder
                        .comment("Hour of the day (0-23) when daily quests reset.")
                        .defineInRange("Quest Reset Hour", 6, 0, 23);
                
                maxAcceptedQuests = builder
                        .comment("Maximum number of quests a player can accept at once.")
                        .defineInRange("Max Accepted Quests", 10, 1, 50);
                
                allowQuestSharing = builder
                        .comment("Allow players to share quest progress in multiplayer.")
                        .define("Allow Quest Sharing", false);
            }
            builder.pop();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static class CommonConfig
    {
        public final ModConfigSpec.BooleanValue enableBaileySpawning;
        public final ModConfigSpec.IntValue baileySpawnWeight;
        public final ModConfigSpec.DoubleValue questRewardMultiplier;

        CommonConfig(ModConfigSpec.Builder builder)
        {
            builder.comment("Settings affecting both client and server sides.")
                    .push("common");
            {
                enableBaileySpawning = builder
                        .comment("Enable Bailey NPC spawning in villages.")
                        .define("Enable Bailey Spawning", true);
                
                baileySpawnWeight = builder
                        .comment("Weight for Bailey spawning in villages (higher = more common).")
                        .defineInRange("Bailey Spawn Weight", 5, 1, 20);
                
                questRewardMultiplier = builder
                        .comment("Multiplier for quest rewards (1.0 = normal rewards).")
                        .defineInRange("Quest Reward Multiplier", 1.0, 0.1, 5.0);
            }
            builder.pop();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    // Cache fields - Updated when config loads
    //--------------------------------------------------------------------------------------------------------------------

    public static boolean isOnline = true;
    public static boolean showQuestsInInventory = true;
    public static int maxQuestsDisplayed = 10;
    public static int questResetHour = 6;
    public static int maxAcceptedQuests = 10;
    public static boolean allowQuestSharing = false;
    public static boolean enableBaileySpawning = true;
    public static int baileySpawnWeight = 5;
    public static double questRewardMultiplier = 1.0;

    public static final void apply()
    {
        // Client config
        isOnline = CLIENT.isOnline.get();
        showQuestsInInventory = CLIENT.showQuestsInInventory.get();
        maxQuestsDisplayed = CLIENT.maxQuestsDisplayed.get();
        
        // Server config
        questResetHour = SERVER.questResetHour.get();
        maxAcceptedQuests = SERVER.maxAcceptedQuests.get();
        allowQuestSharing = SERVER.allowQuestSharing.get();
        
        // Common config
        enableBaileySpawning = COMMON.enableBaileySpawning.get();
        baileySpawnWeight = COMMON.baileySpawnWeight.get();
        questRewardMultiplier = COMMON.questRewardMultiplier.get();
    }
}
