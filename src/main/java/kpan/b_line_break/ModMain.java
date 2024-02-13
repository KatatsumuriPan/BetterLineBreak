package kpan.b_line_break;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import kpan.b_line_break.config.ConfigHolder;
import kpan.b_line_break.config.core.ConfigHandler;
import kpan.b_line_break.proxy.CommonProxy;
import kpan.b_line_break.util.handlers.RegistryHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Mod(modid = ModReference.MODID, version = ModTagsGenerated.VERSION, name = ModReference.MODNAME, acceptedMinecraftVersions = "[1.7.10]"
    , guiFactory = ModReference.MODGROUP + ".config.ModGuiFactory"
    , dependencies = ""
    , acceptableRemoteVersions = "1.0"
)
public class ModMain {

    @SidedProxy(clientSide = ModReference.CLIENT_PROXY_CLASS, serverSide = ModReference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(ModReference.MODID);

    @Nullable
    public static MinecraftServer server = null;
    public static final ConfigHandler defaultConfig = new ConfigHandler(ConfigHolder.class, ModReference.MODID, ConfigHolder.getVersion(), ConfigHolder::updateVersion);

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        defaultConfig.preInit(event);
        RegistryHandler.preInitRegistries(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        RegistryHandler.initRegistries();
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        RegistryHandler.postInitRegistries();
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        RegistryHandler.serverRegistries(event);
    }

    @EventHandler
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        server = event.getServer();
    }

    @EventHandler
    public static void onServerStopped(FMLServerStoppedEvent event) {
        server = null;
    }
}
