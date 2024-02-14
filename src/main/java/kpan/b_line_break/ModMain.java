package kpan.b_line_break;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import kpan.b_line_break.config.ConfigHolder;
import kpan.b_line_break.config.core.ConfigHandler;
import kpan.b_line_break.proxy.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModReference.MODID, version = ModTagsGenerated.VERSION, name = ModReference.MODNAME, acceptedMinecraftVersions = "[1.7.10]"
        , guiFactory = ModReference.MODGROUP + ".config.ModGuiFactory"
        , dependencies = ""
        , acceptableRemoteVersions = "1.0"
)
public class ModMain {

    @SidedProxy(clientSide = ModReference.CLIENT_PROXY_CLASS, serverSide = ModReference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(ModReference.MODNAME);

    public static final ConfigHandler defaultConfig = new ConfigHandler(ConfigHolder.class, ModReference.MODID, ConfigHolder.getVersion(), ConfigHolder::updateVersion);

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        defaultConfig.preInit(event);
        ModMain.proxy.preInit(event);
    }
}
