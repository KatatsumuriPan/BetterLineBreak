package kpan.b_line_break.util.handlers;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import kpan.b_line_break.ModMain;

public class RegistryHandler {

    public static void preInitRegistries(FMLPreInitializationEvent event) {
        ModMain.proxy.registerOnlyClient();
    }

    public static void initRegistries() {
    }

    public static void postInitRegistries() {
    }

    public static void serverRegistries(FMLServerStartingEvent event) {
    }

}
