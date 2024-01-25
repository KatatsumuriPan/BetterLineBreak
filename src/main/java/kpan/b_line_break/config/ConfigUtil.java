package kpan.b_line_break.config;

import kpan.b_line_break.MyReflectionHelper;
import kpan.b_line_break.util.IntegerUtil;
import kpan.b_line_break.util.ListUtil;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ConfigUtil {
	public static boolean requiresWorldRestart(ForgeConfigSpec configSpec, List<String> categoryPath) {
		return false;
	}
	@Nullable
	public static String getComment(ForgeConfigSpec configSpec, List<String> categoryPath) {
		Map<List<String>, String> levelComments = MyReflectionHelper.getPrivateField(configSpec, "levelComments");
		return levelComments.get(categoryPath);
	}
	public static String getTranslationKey(ForgeConfigSpec configSpec, List<String> categoryPath) {
		if (categoryPath.isEmpty())
			return "root";
		else
			return ListUtil.getLast(categoryPath);
	}
	public static boolean requiresMcRestart(ForgeConfigSpec configSpec, List<String> categoryPath) {
		return false;
	}

	public static ValueSpec toValueSpec(ForgeConfigSpec configSpec, ConfigValue<?> configValue) {
		return configSpec.get(configValue.getPath());
	}
	public static Object getDefault(ForgeConfigSpec configSpec, ConfigValue<?> configValue) {
		return toValueSpec(configSpec, configValue).getDefault();
	}
	public static String getComment(ForgeConfigSpec configSpec, ConfigValue<?> configValue) {
		return toValueSpec(configSpec, configValue).getComment();
	}
	public static String getTranslationKey(ForgeConfigSpec forgeConfigSpec, ConfigValue<?> configValue) {
		return toValueSpec(forgeConfigSpec, configValue).getTranslationKey();
	}
	public static IFormattableTextComponent getTranslatedText(ForgeConfigSpec forgeConfigSpec, ConfigValue<?> configValue) {
		String translationKey = getTranslationKey(forgeConfigSpec, configValue);
		if (translationKey == null)
			return new StringTextComponent(ListUtil.getLast(configValue.getPath()));
		else
			return new TranslationTextComponent(translationKey);
	}
	public static boolean requiresWorldRestart(ForgeConfigSpec configSpec, ConfigValue<?> configValue) {
		return toValueSpec(configSpec, configValue).needsWorldRestart();
	}
	public static boolean requiresMcRestart(ConfigValue<?> configValue) {
		return false;
	}

	public static boolean isValid(ConfigValue<?> configValue, String valueStr) {
		if (configValue instanceof BooleanValue) {
			return valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false");
		} else if (configValue instanceof IntValue) {
			try {
				int value = IntegerUtil.parseInt(valueStr);
				ValueSpec valueSpec = toValueSpec(getSpec(configValue), configValue);
				return valueSpec.test(value);
			} catch (NumberFormatException ignored) {
				return false;
			}
		} else if (configValue instanceof LongValue) {
			try {
				long value = IntegerUtil.parseLong(valueStr);
				ValueSpec valueSpec = toValueSpec(getSpec(configValue), configValue);
				return valueSpec.test(value);
			} catch (NumberFormatException ignored) {
				return false;
			}
		} else if (configValue instanceof DoubleValue) {
			if (!NumberUtils.isParsable(valueStr))
				return false;
			double value = Double.parseDouble(valueStr);
			ValueSpec valueSpec = toValueSpec(getSpec(configValue), configValue);
			return valueSpec.test(value);
		} else if (configValue instanceof EnumValue<?>) {
			Class<Enum<?>> clazz = MyReflectionHelper.getPrivateField(configValue, "clazz");
			for (Enum<?> item : clazz.getEnumConstants()) {
				if (item.name().equalsIgnoreCase(valueStr)) {
					return true;
				}
			}
			return false;
//			throw new IllegalArgumentException("No enum constant " + clazz.getCanonicalName() + "." + value);
		} else {
			throw new IllegalArgumentException("Invalid configValue:" + configValue.getClass());
		}
	}

	public static Object parse(ConfigValue<?> configValue, String valueStr) {
		if (configValue instanceof BooleanValue) {
			if (valueStr.equalsIgnoreCase("true"))
				return true;
			if (valueStr.equalsIgnoreCase("false"))
				return false;
			throw new RuntimeException("Can't parse:" + valueStr);
		} else if (configValue instanceof IntValue) {
			return IntegerUtil.parseInt(valueStr);
		} else if (configValue instanceof LongValue) {
			return IntegerUtil.parseLong(valueStr);
		} else if (configValue instanceof DoubleValue) {
			return Double.parseDouble(valueStr);
		} else if (configValue instanceof EnumValue<?>) {
			Class<Enum<?>> clazz = MyReflectionHelper.getPrivateField(configValue, "clazz");
			for (Enum<?> item : clazz.getEnumConstants()) {
				if (item.name().equalsIgnoreCase(valueStr)) {
					return item;
				}
			}
			throw new IllegalArgumentException("No enum constant " + clazz.getCanonicalName() + "." + valueStr);
		} else {
			throw new IllegalArgumentException("Invalid configValue:" + configValue.getClass());
		}
	}

	public static ForgeConfigSpec getSpec(ConfigValue<?> configValue) {
		return MyReflectionHelper.getPrivateField(ConfigValue.class, configValue, "spec");
	}
}
