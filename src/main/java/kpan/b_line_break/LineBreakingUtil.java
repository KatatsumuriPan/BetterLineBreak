package kpan.b_line_break;

import com.google.budoux.Parser;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler.LineWrappingConsumer;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class LineBreakingUtil {

	public static void wrapLines(TextHandlerAccessor textHandler, String text, float maxWidth, Style resetStyle, boolean retainTrailingWordSplit, LineWrappingConsumer consumer) {
		int i = 0;
		int len = text.length();
		Style style = resetStyle;
		Parser parser = getParser();
		while (i < len) {
			LineBreakResult result = new LineBreaker(textHandler, maxWidth, parser).tryBreak(text, i, style, resetStyle);
			if (!result.hasBreak) {
				consumer.accept(style, i, len);
				break;
			}
			int endingIndex = result.endingIndex;
			char c = text.charAt(endingIndex);
			int next = c == '\n' || c == ' ' ? endingIndex + 1 : endingIndex;
			consumer.accept(style, i, retainTrailingWordSplit ? next : endingIndex);
			i = next;
			style = result.endingStyle;
		}
	}


	public static void method_29971(TextHandlerAccessor textHandler, StringVisitable stringVisitable, float maxWidth, Style resetStyle, BiConsumer<StringVisitable, Boolean> biConsumer) {
		ArrayList<StyledString> list = Lists.newArrayList();
		stringVisitable.visit((style, string) -> {
			if (!string.isEmpty()) {
				list.add(new StyledString(string, style));
			}
			return Optional.empty();
		}, resetStyle);
		LineWrappingCollector lineWrappingCollector = new LineWrappingCollector(list);
		boolean hasBreak = true;
		boolean isLastLineFeed = false;
		boolean bl3 = false;
		Parser parser = getParser();
		block0:
		while (hasBreak) {
			hasBreak = false;
			LineBreaker lineBreaker = new LineBreaker(textHandler, maxWidth, parser);
			for (StyledString styledString : lineWrappingCollector.parts) {
				LineBreakResult result = lineBreaker.tryBreak(styledString.literal, 0, styledString.style, resetStyle);
				if (result.hasBreak) {
					int endingIndex = result.endingIndex;
					Style endingStyle = result.endingStyle;
					char c = lineWrappingCollector.charAt(endingIndex);
					boolean isLineFeed = c == '\n';
					boolean skip = isLineFeed || c == ' ';
					isLastLineFeed = isLineFeed;
					StringVisitable stringVisitable2 = lineWrappingCollector.collectLine(endingIndex, skip ? 1 : 0, endingStyle);
					biConsumer.accept(stringVisitable2, bl3);
					bl3 = !isLineFeed;
					hasBreak = true;
					continue block0;
				}
				lineBreaker.addOffset(styledString.literal.length());
			}
		}
		StringVisitable stringVisitable3 = lineWrappingCollector.collectRemainers();
		if (stringVisitable3 != null) {
			biConsumer.accept(stringVisitable3, bl3);
		} else if (isLastLineFeed) {
			biConsumer.accept(StringVisitable.EMPTY, false);
		}
	}

	public static @Nullable Parser getParser() {
		switch (BetterLineBreak.config.algorithm) {
			case VANILLA, NON_ASCII -> {
				return null;
			}
			case PHRASE -> {
				LanguageDefinition language = MinecraftClient.getInstance().getLanguageManager().getLanguage();
				return switch (language.getCode()) {
					case "ja_jp" -> Parser.loadByFileName("/models/ja_tuned.json");
					case "zh_cn" -> Parser.loadDefaultSimplifiedChineseParser();
					case "zh_tw" -> Parser.loadDefaultTraditionalChineseParser();
					case "th_th" -> Parser.loadDefaultThaiParser();
					default -> null;
				};
			}
			default -> throw new IllegalStateException("Unexpected value: " + BetterLineBreak.config.algorithm);
		}
	}

	public static boolean canBreak(char prevChar, char c, int index, IntSet breakIndices) {
		switch (BetterLineBreak.config.algorithm) {
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

	private static float getWidth(TextHandlerAccessor textHandler, int codePoint, Style style) {
		return textHandler.betterLineBreak$getWidthRetriever().getWidth(codePoint, style);
	}

	public static class LineBreaker {
		private final TextHandlerAccessor textHandler;
		private final float maxWidth;
		private final @Nullable Parser parser;

		private int lastBreak = -1;
		private Style lastBreakStyle = Style.EMPTY;
		private float totalWidth = 0;
		private int offset = 0;

		public LineBreaker(TextHandlerAccessor textHandler, float maxWidth) {
			this(textHandler, maxWidth, null);
		}
		public LineBreaker(TextHandlerAccessor textHandler, float maxWidth, @Nullable Parser parser) {
			this.textHandler = textHandler;
			this.maxWidth = Math.max(maxWidth, 1.0f);
			this.parser = parser;
		}

		public LineBreakResult tryBreak(String text, int startIndex, Style style, Style resetStyle) {
			IntSet breakIndices = phraseIndices(text, parser);
			int len = text.length();
			for (int i = startIndex; i < len; ++i) {
				char c = text.charAt(i);
				if (c == '\n') {
					lastBreak = i + offset;
					lastBreakStyle = style;
					return LineBreakResult.ending(lastBreak, lastBreakStyle);
				}
				if (c == '§') {
					if (i + 1 >= len)
						break;
					char next = text.charAt(i + 1);
					Formatting formatting = Formatting.byCode(next);
					if (formatting != null) {
						style = formatting == Formatting.RESET ? resetStyle : style.withExclusiveFormatting(formatting);
					}
					++i;
					continue;
				}
				if (Character.isHighSurrogate(c)) {
					if (i + 1 >= len)
						break;
					char next = text.charAt(i + 1);
					if (Character.isLowSurrogate(next)) {
						LineBreakResult result = updateWidth(style, Character.toCodePoint(c, next), i);
						if (result != null)
							return result;
						++i;
					} else {
						break;
					}
				} else {
					if (c == ' ' || i > 0 && canBreak(text.charAt(i), c, i, breakIndices)) {
						lastBreak = i + offset;
						lastBreakStyle = style;
					}
					LineBreakResult result = updateWidth(style, c, i);
					if (result != null)
						return result;
				}
			}
			return LineBreakResult.NO_BREAK;
		}
		@Nullable
		private LineBreakResult updateWidth(Style style, int codePoint, int index) {
			float w = getWidth(textHandler, codePoint, style);
			totalWidth += w;
			if (totalWidth != w && totalWidth > maxWidth) {
				if (lastBreak != -1) {
					return LineBreakResult.ending(lastBreak, lastBreakStyle);
				}
				return LineBreakResult.ending(index + offset, style);
			}
			return null;
		}

		public void addOffset(int offset) {
			this.offset += offset;
		}

		private static IntSet phraseIndices(String str, @Nullable Parser parser) {
			if (parser == null)
				return IntSets.EMPTY_SET;
			IntSet result = new IntOpenHashSet();
			int index = 0;
			for (String phrase : parser.parse(str)) {
				index += phrase.length();
				result.add(index);
			}
			return result;
		}

	}

	public static class LineBreakResult {
		public static final LineBreakResult NO_BREAK = new LineBreakResult(false, 0, net.minecraft.text.Style.EMPTY);
		public static LineBreakResult ending(int endingIndex, Style endingStyle) {
			return new LineBreakResult(true, endingIndex, endingStyle);
		}
		public final boolean hasBreak;
		public final int endingIndex;
		public final Style endingStyle;
		private LineBreakResult(boolean hasBreak, int endingIndex, Style endingStyle) {
			this.hasBreak = hasBreak;
			this.endingIndex = endingIndex;
			this.endingStyle = endingStyle;
		}
	}

	@Environment(value = EnvType.CLIENT)
	static class LineWrappingCollector {
		private final List<StyledString> parts;
		private String joined;

		public LineWrappingCollector(List<StyledString> parts) {
			this.parts = parts;
			joined = parts.stream().map(styledString -> styledString.literal).collect(Collectors.joining());
		}

		public char charAt(int index) {
			return joined.charAt(index);
		}

		public StringVisitable collectLine(int lineLength, int skippedLength, Style style) {
			TextCollector textCollector = new TextCollector();
			ListIterator<StyledString> listIterator = parts.listIterator();
			int i = lineLength;
			boolean bl = false;
			while (listIterator.hasNext()) {
				String string2;
				StyledString styledString = listIterator.next();
				String string = styledString.literal;
				int j = string.length();
				if (!bl) {
					if (i > j) {
						textCollector.add(styledString);
						listIterator.remove();
						i -= j;
					} else {
						string2 = string.substring(0, i);
						if (!string2.isEmpty()) {
							textCollector.add(StringVisitable.styled(string2, styledString.style));
						}
						i += skippedLength;
						bl = true;
					}
				}
				if (!bl)
					continue;
				if (i > j) {
					listIterator.remove();
					i -= j;
					continue;
				}
				string2 = string.substring(i);
				if (string2.isEmpty()) {
					listIterator.remove();
					break;
				}
				listIterator.set(new StyledString(string2, style));
				break;
			}
			joined = joined.substring(lineLength + skippedLength);
			return textCollector.getCombined();
		}

		@Nullable
		public StringVisitable collectRemainers() {
			TextCollector textCollector = new TextCollector();
			parts.forEach(textCollector::add);
			parts.clear();
			return textCollector.getRawCombined();
		}
	}

	@Environment(value = EnvType.CLIENT)
	static class StyledString
			implements StringVisitable {
		private final String literal;
		private final Style style;

		public StyledString(String literal, Style style) {
			this.literal = literal;
			this.style = style;
		}

		@Override
		public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
			return visitor.accept(literal);
		}

		@Override
		public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
			return styledVisitor.accept(this.style.withParent(style), literal);
		}
	}

}
