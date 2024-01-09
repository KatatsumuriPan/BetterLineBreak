package kpan.b_line_break.asm.hook;

import com.google.budoux.Parser;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import kpan.b_line_break.compat.CompatFontRenderer;
import kpan.b_line_break.config.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class HK_FontRenderer {

	public static int sizeStringToWidth(FontRenderer self, String str, int wrapWidth) {
		return sizeStringToWidth2(self, str, wrapWidth);
	}

	public static int sizeStringToWidth2(FontRenderer fontRenderer, String str, float wrapWidth) {
		switch (ConfigHolder.client.lineBreakAlgorithm) {
			case VANILLA, NON_ASCII -> {
				return sizeStringToWidth3(fontRenderer, str, wrapWidth, IntSets.EMPTY_SET);
			}
			case PHRASE -> {
				Parser parser;
				switch (Minecraft.getMinecraft().gameSettings.language) {
					case "ja_jp" -> parser = Parser.loadByFileName("/models/ja_tuned.json");
					case "zh_cn" -> parser = Parser.loadDefaultSimplifiedChineseParser();
					case "zh_tw" -> parser = Parser.loadDefaultTraditionalChineseParser();
					case "th_th" -> parser = Parser.loadDefaultThaiParser();
					default -> parser = null;
				}
				if (parser != null)
					return sizeStringToWidth3(fontRenderer, str, wrapWidth, phraseIndices(str, parser));
				else
					return sizeStringToWidth3(fontRenderer, str, wrapWidth, IntSets.EMPTY_SET);
			}
			default -> throw new AssertionError();
		}
	}

	public static int sizeStringToWidth3(FontRenderer fontRenderer, String str, float wrapWidth, IntSet breakIndices) {
		int length = str.length();
		float width = 0;
		int i = 0;
		int breakIndex = -1;
		boolean isBold = false;

		for (; i < length; ++i) {
			char c0 = str.charAt(i);

			if (c0 == '\n') {
				breakIndex = i;
				break;
			}

			switch (c0) {
				case ' ':
					breakIndex = i;
				default:
					width += CompatFontRenderer.getCharWidthFloat(fontRenderer, c0);
					if (isBold) {
						width += CompatFontRenderer.getOffsetBold(fontRenderer, c0);
					}
					if (i > 0 && canBreak(str.charAt(i - 1), c0, i, breakIndices))
						breakIndex = i;
					break;
				case '§':
					if (i < length - 1) {
						++i;
						char formatting = str.charAt(i);

						if (formatting == 'l' || formatting == 'L') {
							isBold = true;
						} else if (formatting == 'r' || formatting == 'R' || FontRenderer.isFormatColor(formatting)) {
							isBold = false;
						}
					}
			}

			if (width > wrapWidth) {
				break;
			}
		}

		return i != length && breakIndex != -1 && breakIndex < i ? breakIndex : i;
	}

	private static IntSet phraseIndices(String str, Parser parser) {
		IntSet result = new IntOpenHashSet();
		int index = 0;
		for (String phrase : parser.parse(str)) {
			index += phrase.length();
			result.add(index);
		}
		return result;
	}

	private static boolean canBreak(char prevChar, char c, int index, IntSet breakIndices) {
		switch (ConfigHolder.client.lineBreakAlgorithm) {
			case VANILLA -> {
				return false;
			}
			case NON_ASCII -> {
				if (c == '§')
					return false;
				if (isNormalAsciiLetter(prevChar) && isNormalAsciiLetter(c))
					return false;
				if (c == 0x2011)//Non-breaking Hyphen
					return false;
				if (c == 0xa0)//No-break Space
					return false;
				if (c == 0x202F)//Narrow No-break Space
					return false;
				if (isEndBracket(c))
					return false;
				if (isJapaneseNoBreakChar(c))
					return false;
				if (isDelimiters(c))
					return false;
				if (isMiddleSentencePunctuation(c))
					return false;
				if (isSentenceEndingPunctuation(c))
					return false;
				if (isStartBracket(prevChar))
					return false;
				return true;
			}
			case PHRASE -> {
				if (c == '§')
					return false;
				if (isNormalAsciiLetter(prevChar) && isNormalAsciiLetter(c))
					return false;
				if (c == 0x2011)//Non-breaking Hyphen
					return false;
				if (c == 0xa0)//No-break Space
					return false;
				if (c == 0x202F)//Narrow No-break Space
					return false;
				if (isEndBracket(c))
					return false;
				if (isJapaneseNoBreakChar(c))
					return false;
				if (isDelimiters(c))
					return false;
				if (isMiddleSentencePunctuation(c))
					return false;
				if (isSentenceEndingPunctuation(c))
					return false;
				if (isStartBracket(prevChar))
					return false;
				return breakIndices.contains(index);
			}
			default -> throw new AssertionError();
		}
	}

	private static boolean isNormalAsciiLetter(char c) {
		if (c <= ' ')
			return false;
		return switch (c) {
			case '!',
					'(',
					')',
					',',
					'.',
					':',
					';',
					'<',
					'>',
					'?',
					'[',
					']',
					'{',
					'}' -> false;
			default -> c < 0x7F;//DELも除く
		};
	}

	private static boolean isEndBracket(char c) {
		return switch (c) {
			case ',',//0x002C
					')',//0x0029
					']',//0x005D
					'»',//0x00BB
					'’',//0x2019
					'”',//0x201D
					'、',//0x3001
					'〉',//0x3009
					'》',//0x300B
					'」',//0x300D
					'』',//0x300F
					'】',//0x3011
					'〕',//0x3015
					'〗',//0x3017
					'〙',//0x3019
					'〟',//0x301F
					'）',//0xFF09
					'，',//0xFF0C
					'］',//0xFF3D
					'｝',//0xFF5D
					'｠'//0xFF60
					-> true;
			default -> false;
		};
	}
	private static boolean isJapaneseNoBreakChar(char c) {
		return switch (c) {
			case '々', //0x3005
					'〻', //0x303B
					'ぁ', //0x3041
					'ぃ', //0x3043
					'ぅ', //0x3045
					'ぇ', //0x3047
					'ぉ', //0x3049
					'っ', //0x3063
					'ゃ', //0x3083
					'ゅ', //0x3085
					'ょ', //0x3087
					'ゎ', //0x308E
					'ゕ', //0x3095
					'ゖ', //0x3096
					'ァ', //0x30A1
					'ィ', //0x30A3
					'ゥ', //0x30A5
					'ェ', //0x30A7
					'ォ', //0x30A9
					'ッ', //0x30C3
					'ャ', //0x30E3
					'ュ', //0x30E5
					'ョ', //0x30E7
					'ヮ', //0x30EE
					'ヵ', //0x30F5
					'ヶ', //0x30F6
					'ー', //0x30FC
					'ヽ', //0x30FD
					'ヾ', //0x30FE
					'ㇰ', //0x31F0
					'ㇱ', //0x31F1
					'ㇲ', //0x31F2
					'ㇳ', //0x31F3
					'ㇴ', //0x31F4
					'ㇵ', //0x31F5
					'ㇶ', //0x31F6
					'ㇷ', //0x31F7
					'ㇸ', //0x31F8
					'ㇹ', //0x31F9
					'ㇺ', //0x31FA
					'ㇻ', //0x31FB
					'ㇼ', //0x31FC
					'ㇽ', //0x31FD
					'ㇾ', //0x31FE
					'ㇿ' //0x31FF
					-> true;
			default -> false;
		};
	}
	private static boolean isDelimiters(char c) {
		return switch (c) {
			case '!', //0x0021
					'?', //0x003F
					'‼', //0x203c
					'⁇', //0x2047
					'⁈', //0x2048
					'⁉', //0x2049
					'！', //0xFF01
					'？' //0xFF1F
					-> true;
			default -> false;
		};
	}
	private static boolean isMiddleSentencePunctuation(char c) {
		return switch (c) {
			case ':', //0x003A
					';', //0x003B
					'・', //0x30FB
					'：', //0xFF1A
					'；' //0xFF1B
					-> true;
			default -> false;
		};
	}
	private static boolean isSentenceEndingPunctuation(char c) {
		return switch (c) {
			case '.', //0x002E
					'。', //0x3002
					'．' //0xFF0E
					-> true;
			default -> false;
		};
	}
	private static boolean isStartBracket(char c) {
		return switch (c) {
			case '(', //0x0028
					'[', //0x005B
					'«', //0x00AB
					'‘', //0x2018
					'"', //0x201C
					'〈', //0x3008
					'《', //0x300A
					'「', //0x300C
					'『', //0x300E
					'【', //0x3010
					'〔', //0x3014
					'〖', //0x3016
					'〘', //0x3018
					'〝', //0x301D
					'（', //0xFF08
					'［', //0xFF3B
					'｛', //0xFF5B
					'｟' //0xFF5F
					-> true;
			default -> false;
		};
	}
}
