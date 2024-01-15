package kpan.b_line_break.config;

import kpan.b_line_break.BetterLineBreak;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = BetterLineBreak.MOD_ID)
public class ModConfig implements ConfigData {
	public EnumAlgorithm algorithm = EnumAlgorithm.NON_ASCII;

	@Override
	public void validatePostLoad() {
		for (EnumAlgorithm value : EnumAlgorithm.values()) {
			if (value == algorithm)
				return;
		}
		algorithm = EnumAlgorithm.VANILLA;
	}
}
