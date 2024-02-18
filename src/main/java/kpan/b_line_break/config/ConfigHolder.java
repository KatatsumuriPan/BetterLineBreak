package kpan.b_line_break.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
	public static final ConfigHolder INSTANCE;
	public static final ForgeConfigSpec FORGE_CONFIG_SPEC;

	public EnumValue<EnumAlgorithm> lineBreakAlgorithm;

	private ConfigHolder(ForgeConfigSpec.Builder builder) {
		lineBreakAlgorithm = builder.comment("The algorithm used for line breaks").defineEnum("Line Break Algorithm", EnumAlgorithm.NON_ASCII);
	}

	static {
		Pair<ConfigHolder, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
				.configure(ConfigHolder::new);
		INSTANCE = pair.getLeft();
		FORGE_CONFIG_SPEC = pair.getRight();
	}
}
