package kpan.b_line_break.proxy;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kpan.b_line_break.config.ConfigHolder;

import static kpan.b_line_break.ModReference.MODID;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        ConfigHolder.init(event.getSuggestedConfigurationFile());
	}

	@Override
	public boolean hasClientSide() { return true; }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(MODID)) {
            ConfigHolder.syncConfig();
        }
    }
}
