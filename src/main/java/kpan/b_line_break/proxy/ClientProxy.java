package kpan.b_line_break.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public boolean hasClientSide() {
        return true;
    }

}
