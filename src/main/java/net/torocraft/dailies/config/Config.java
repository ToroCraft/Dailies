package net.torocraft.dailies.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.torocraft.dailies.DailiesMod;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
    private static final String MODID = DailiesMod.MODID;
    public static final CommonConfig COMMON;
    public static final ServerConfig SERVER;
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec COMMON_CONFIG_SPEC;
    public static final ForgeConfigSpec SERVER_CONFIG_SPEC;
    public static final ForgeConfigSpec CLIENT_CONFIG_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> common_ = (new ForgeConfigSpec.Builder()).configure(CommonConfig::new);
        COMMON_CONFIG_SPEC = common_.getRight();
        COMMON = common_.getLeft();
        final Pair<ServerConfig, ForgeConfigSpec> server_ = (new ForgeConfigSpec.Builder()).configure(ServerConfig::new);
        SERVER_CONFIG_SPEC = server_.getRight();
        SERVER = server_.getLeft();
        final Pair<ClientConfig, ForgeConfigSpec> client_ = (new ForgeConfigSpec.Builder()).configure(ClientConfig::new);
        CLIENT_CONFIG_SPEC = client_.getRight();
        CLIENT = client_.getLeft();
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static void onLoad(final net.minecraftforge.fml.config.ModConfig config)
    {
        try {
            apply();
        } catch(Exception ex) {

        }
    }

    public static void onFileChange(final net.minecraftforge.fml.config.ModConfig config)
    {}

    //--------------------------------------------------------------------------------------------------------------------

    public static class ClientConfig
    {
        public final ForgeConfigSpec.BooleanValue isOnline;

        ClientConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings not loaded on servers.")
                    .push("client");
            {
                isOnline = builder
                        //.translation(MODID + ".config.")
                        .comment("Defines the maximum line length in the Code Book.")
                        .define("Is Online Enabled", true);
            }
            builder.pop();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static class ServerConfig
    {
        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings not loaded on clients.")
                    .push("server");
            builder.pop();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static class CommonConfig
    {
        CommonConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings affecting the logical server side, but are also configurable in single player.")
                    .push("server");
            // Config definitions go here
            builder.pop();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    // Cache fields
    //--------------------------------------------------------------------------------------------------------------------

    public static boolean isOnline = true;

    public static final void apply()
    {
        isOnline = CLIENT.isOnline.get();
    }
}
