package kpan.b_line_break;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import kpan.b_line_break.budoux.Parser;
import kpan.b_line_break.config.ConfigHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class LineBreakingUtil {

	public static int findLineBreak(StringSplitterAccessor stringSplitter, String text, int maxWidth, Style style) {
		Parser parser = getParser();
		LineBreakResult result = new LineBreaker(stringSplitter, maxWidth, parser).tryBreak(text, 0, style, style);
		return result.endingIndex;
	}

	public static void splitLines(StringSplitterAccessor stringSplitter, String text, int maxWidth, Style resetStyle, boolean retainTrailingWordSplit, StringSplitter.LinePosConsumer consumer) {
		splitLines(stringSplitter, text, (float) maxWidth, resetStyle, retainTrailingWordSplit, consumer);
	}
	public static void splitLines(StringSplitterAccessor stringSplitter, String text, float maxWidth, Style resetStyle, boolean retainTrailingWordSplit, StringSplitter.LinePosConsumer consumer) {
		int i = 0;
		int len = text.length();
		Style style = resetStyle;
		Parser parser = getParser();
		while (i < len) {
			LineBreakResult result = new LineBreaker(stringSplitter, maxWidth, parser).tryBreak(text, i, style, resetStyle);
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


	public static void splitLines(StringSplitterAccessor stringSplitter, FormattedText formattedText, int maxWidth, Style resetStyle, BiConsumer<FormattedText, Boolean> biConsumer) {
		splitLines(stringSplitter, formattedText, (float) maxWidth, resetStyle, biConsumer);
	}
	public static void splitLines(StringSplitterAccessor stringSplitter, FormattedText formattedText, float maxWidth, Style resetStyle, BiConsumer<FormattedText, Boolean> biConsumer) {
		List<LineComponent> list = Lists.newArrayList();
		formattedText.visit((style, string) -> {
			if (!string.isEmpty()) {
				list.add(new LineComponent(string, style));
			}
			return Optional.empty();
		}, resetStyle);
		FlatComponents lineWrappingCollector = new FlatComponents(list);
		boolean hasBreak = true;
		boolean isLastLineFeed = false;
		boolean bl3 = false;
		Parser parser = getParser();
		block0:
		while (hasBreak) {
			hasBreak = false;
			LineBreaker lineBreaker = new LineBreaker(stringSplitter, maxWidth, parser);
			for (LineComponent styledString : lineWrappingCollector.parts) {
				LineBreakResult result = lineBreaker.tryBreak(styledString.contents, 0, styledString.style, resetStyle);
				if (result.hasBreak) {
					int endingIndex = result.endingIndex;
					Style endingStyle = result.endingStyle;
					char c = lineWrappingCollector.charAt(endingIndex);
					boolean isLineFeed = c == '\n';
					boolean skip = isLineFeed || c == ' ';
					isLastLineFeed = isLineFeed;
					FormattedText stringVisitable2 = lineWrappingCollector.splitAt(endingIndex, skip ? 1 : 0, endingStyle);
					biConsumer.accept(stringVisitable2, bl3);
					bl3 = !isLineFeed;
					hasBreak = true;
					continue block0;
				}
				lineBreaker.addOffset(styledString.contents.length());
			}
		}
		FormattedText stringVisitable3 = lineWrappingCollector.getRemainder();
		if (stringVisitable3 != null) {
			biConsumer.accept(stringVisitable3, bl3);
		} else if (isLastLineFeed) {
			biConsumer.accept(FormattedText.EMPTY, false);
		}
	}

	public static @Nullable Parser getParser() {
		switch (ConfigHolder.INSTANCE.lineBreakAlgorithm.get()) {
			case VANILLA:
			case NON_ASCII:
				return null;
			case PHRASE:
				String language = Minecraft.getInstance().getLanguageManager().getSelected();
				switch (language) {
					case "ja_jp":
						return Parser.loadByFileName("/models/ja_tuned.json");
					case "zh_cn":
						return Parser.loadDefaultSimplifiedChineseParser();
					case "zh_tw":
						return Parser.loadDefaultTraditionalChineseParser();
					case "th_th":
						return Parser.loadDefaultThaiParser();
					default:
						return null;
				}
			default:
				throw new IllegalStateException("Unexpected value: " + ConfigHolder.INSTANCE.lineBreakAlgorithm.get());
		}
	}

	public static boolean canBreak(char prevChar, char c, int index, IntSet breakIndices) {
		switch (ConfigHolder.INSTANCE.lineBreakAlgorithm.get()) {
			case VANILLA:
				return false;
			case NON_ASCII:
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
			case PHRASE:
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
				if (breakIndices.contains(index))
					return true;
				if (!isNormalAsciiLetter(prevChar) && isNormalAsciiLetter(c))
					return true;
				return false;
			default:
				throw new AssertionError();
		}
	}
	private static boolean isNormalAsciiLetter(char c) {
		if (c <= ' ')
			return false;
		//DELも除く
		switch (c) {
			case '!':
			case '(':
			case ')':
			case ',':
			case '.':
			case ':':
			case ';':
			case '<':
			case '>':
			case '?':
			case '[':
			case ']':
			case '{':
			case '}':
				return false;
			default:
				return c < 0x7F;
		}
	}
	private static boolean isEndBracket(char c) {
		switch (c) {
			case ','://0x002C
			case ')'://0x0029
			case ']'://0x005D
			case '»'://0x00BB
			case '’'://0x2019
			case '”'://0x201D
			case '、'://0x3001
			case '〉'://0x3009
			case '》'://0x300B
			case '」'://0x300D
			case '』'://0x300F
			case '】'://0x3011
			case '〕'://0x3015
			case '〗'://0x3017
			case '〙'://0x3019
			case '〟'://0x301F
			case '）'://0xFF09
			case '，'://0xFF0C
			case '］'://0xFF3D
			case '｝'://0xFF5D
			case '｠'://0xFF60
				return true;
			default:
				return false;
		}
	}
	private static boolean isJapaneseNoBreakChar(char c) {
		switch (c) {
			case '々': //0x3005
			case '〻': //0x303B
			case 'ぁ': //0x3041
			case 'ぃ': //0x3043
			case 'ぅ': //0x3045
			case 'ぇ': //0x3047
			case 'ぉ': //0x3049
			case 'っ': //0x3063
			case 'ゃ': //0x3083
			case 'ゅ': //0x3085
			case 'ょ': //0x3087
			case 'ゎ': //0x308E
			case 'ゕ': //0x3095
			case 'ゖ': //0x3096
			case 'ァ': //0x30A1
			case 'ィ': //0x30A3
			case 'ゥ': //0x30A5
			case 'ェ': //0x30A7
			case 'ォ': //0x30A9
			case 'ッ': //0x30C3
			case 'ャ': //0x30E3
			case 'ュ': //0x30E5
			case 'ョ': //0x30E7
			case 'ヮ': //0x30EE
			case 'ヵ': //0x30F5
			case 'ヶ': //0x30F6
			case 'ー': //0x30FC
			case 'ヽ': //0x30FD
			case 'ヾ': //0x30FE
			case 'ㇰ': //0x31F0
			case 'ㇱ': //0x31F1
			case 'ㇲ': //0x31F2
			case 'ㇳ': //0x31F3
			case 'ㇴ': //0x31F4
			case 'ㇵ': //0x31F5
			case 'ㇶ': //0x31F6
			case 'ㇷ': //0x31F7
			case 'ㇸ': //0x31F8
			case 'ㇹ': //0x31F9
			case 'ㇺ': //0x31FA
			case 'ㇻ': //0x31FB
			case 'ㇼ': //0x31FC
			case 'ㇽ': //0x31FD
			case 'ㇾ': //0x31FE
			case 'ㇿ': //0x31FF
				return true;
			default:
				return false;
		}
	}
	private static boolean isDelimiters(char c) {
		switch (c) {
			case '!': //0x0021
			case '?': //0x003F
			case '‼': //0x203c
			case '⁇': //0x2047
			case '⁈': //0x2048
			case '⁉': //0x2049
			case '！': //0xFF01
			case '？': //0xFF1F
				return true;
			default:
				return false;
		}
	}
	private static boolean isMiddleSentencePunctuation(char c) {
		switch (c) {
			case ':': //0x003A
			case ';': //0x003B
			case '・': //0x30FB
			case '：': //0xFF1A
			case '；': //0xFF1B
				return true;
			default:
				return false;
		}
	}
	private static boolean isSentenceEndingPunctuation(char c) {
		switch (c) {
			case '.': //0x002E
			case '。': //0x3002
			case '．': //0xFF0E
				return true;
			default:
				return false;
		}
	}
	private static boolean isStartBracket(char c) {
		switch (c) {
			case '(': //0x0028
			case '[': //0x005B
			case '«': //0x00AB
			case '‘': //0x2018
			case '"': //0x201C
			case '〈': //0x3008
			case '《': //0x300A
			case '「': //0x300C
			case '『': //0x300E
			case '【': //0x3010
			case '〔': //0x3014
			case '〖': //0x3016
			case '〘': //0x3018
			case '〝': //0x301D
			case '（': //0xFF08
			case '［': //0xFF3B
			case '｛': //0xFF5B
			case '｟': //0xFF5F
				return true;
			default:
				return false;
		}
	}

	private static float getWidth(StringSplitterAccessor stringSplitter, int codePoint, Style style) {
		return stringSplitter.betterLineBreak$getWidthProvider().getWidth(codePoint, style);
	}

	public static class LineBreaker {
		private final StringSplitterAccessor stringSplitter;
		private final float maxWidth;
		private final @Nullable Parser parser;

		private int lastBreak = -1;
		private Style lastBreakStyle = Style.EMPTY;
		private float totalWidth = 0;
		private int offset = 0;

		public LineBreaker(StringSplitterAccessor stringSplitter, float maxWidth) {
			this(stringSplitter, maxWidth, null);
		}
		public LineBreaker(StringSplitterAccessor stringSplitter, float maxWidth, @Nullable Parser parser) {
			this.stringSplitter = stringSplitter;
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
					ChatFormatting formatting = ChatFormatting.getByCode(next);
					if (formatting != null) {
						style = formatting == ChatFormatting.RESET ? resetStyle : style.applyLegacyFormat(formatting);
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

					if (c == ' ' || i > startIndex && canBreak(text.charAt(i), c, i, breakIndices)) {
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
			float w = getWidth(stringSplitter, codePoint, style);
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
		public static final LineBreakResult NO_BREAK = new LineBreakResult(false, 0, Style.EMPTY);
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

	@OnlyIn(Dist.CLIENT)
	static class FlatComponents {
		final List<LineComponent> parts;
		private String flatParts;

		public FlatComponents(List<LineComponent> p_92448_) {
			parts = p_92448_;
			flatParts = p_92448_.stream().map((p_92459_) -> {
				return p_92459_.contents;
			}).collect(Collectors.joining());
		}

		public char charAt(int p_92451_) {
			return flatParts.charAt(p_92451_);
		}

		public FormattedText splitAt(int p_92453_, int p_92454_, Style p_92455_) {
			ComponentCollector componentcollector = new ComponentCollector();
			ListIterator<LineComponent> listiterator = parts.listIterator();
			int i = p_92453_;
			boolean flag = false;

			while (listiterator.hasNext()) {
				LineComponent stringsplitter$linecomponent = listiterator.next();
				String s = stringsplitter$linecomponent.contents;
				int j = s.length();
				if (!flag) {
					if (i > j) {
						componentcollector.append(stringsplitter$linecomponent);
						listiterator.remove();
						i -= j;
					} else {
						String s1 = s.substring(0, i);
						if (!s1.isEmpty()) {
							componentcollector.append(FormattedText.of(s1, stringsplitter$linecomponent.style));
						}

						i += p_92454_;
						flag = true;
					}
				}

				if (flag) {
					if (i <= j) {
						String s2 = s.substring(i);
						if (s2.isEmpty()) {
							listiterator.remove();
						} else {
							listiterator.set(new LineComponent(s2, p_92455_));
						}
						break;
					}

					listiterator.remove();
					i -= j;
				}
			}

			flatParts = flatParts.substring(p_92453_ + p_92454_);
			return componentcollector.getResultOrEmpty();
		}

		@Nullable
		public FormattedText getRemainder() {
			ComponentCollector componentcollector = new ComponentCollector();
			parts.forEach(componentcollector::append);
			parts.clear();
			return componentcollector.getResult();
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class LineComponent implements FormattedText {
		final String contents;
		final Style style;

		public LineComponent(String p_92488_, Style p_92489_) {
			contents = p_92488_;
			style = p_92489_;
		}

		@Override
		public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_92493_) {
			return p_92493_.accept(contents);
		}

		@Override
		public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_92495_, Style p_92496_) {
			return p_92495_.accept(style.applyTo(p_92496_), contents);
		}
	}


}
