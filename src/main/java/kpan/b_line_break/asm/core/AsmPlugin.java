package kpan.b_line_break.asm.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import kpan.b_line_break.ModReference;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({ModReference.MODGROUP + ".asm.core.", ModReference.MODGROUP + ".asm.tf.", ModReference.MODGROUP + ".util.MyReflectionHelper"})
@MCVersion("1.7.10")
public class AsmPlugin implements IFMLLoadingPlugin {

    public AsmPlugin() {
    }

    @Override
    public String[] getASMTransformerClass() { return new String[]{ASMTransformer.class.getName()}; }

    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) {
        AsmUtil.checkEnvironment(data);
        LogManager.getLogger().debug("This is " + (AsmUtil.isDeobfEnvironment() ? "deobf" : "obf") + " environment");
    }

    @Override
    public String getAccessTransformerClass() { return AccessTransformerForMixin.class.getName(); }

}
