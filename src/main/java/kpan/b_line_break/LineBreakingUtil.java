package kpan.b_line_break;

import com.google.budoux.Parser;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import kpan.b_line_break.config.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.Language;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LineBreakingUtil {

	public static int getCharacterCountForWidth(FontRenderer fontRenderer, String text, float wrapWidth) {
		wrapWidth = Math.max(1, wrapWidth);
		int length = text.length();
		float width = 0.0f;
		int breakIndex = -1;
		boolean isBold = false;
		boolean breakLine = true;
		Parser parser = getParser();
		IntSet breakIndices = phraseIndices(text, parser);
		int idx;
		for (idx = 0; idx < length; ++idx) {
			char c = text.charAt(idx);
			switch (c) {
				case '§': {
					if (idx < length - 1) {
						++idx;
						char formatting = text.charAt(idx);

						if (formatting == 'l' || formatting == 'L') {
							isBold = true;
						} else if (formatting == 'r' || formatting == 'R' || FontRenderer.isFormatColor(formatting)) {
							isBold = false;
						}
					}
					break;
				}
				case '\n': {
					--idx;
					break;
				}
				case ' ': {
					breakIndex = idx;
					//fall through
				}
				default: {
					if (width != 0.0f)
						breakLine = false;
					if (idx > 0 && canBreak(text.charAt(idx - 1), c, idx, breakIndices))
						breakIndex = idx;
					width += fontRenderer.getCharWidth(c);
					if (isBold)
						width += 1.0f;
					break;
				}
			}
			if (c == '\n') {
				breakIndex = ++idx;
				break;
			}
			if (!(width > wrapWidth))
				continue;
			if (!breakLine)
				break;
			++idx;
			break;
		}
		if (idx != length && breakIndex != -1 && breakIndex < idx) {
			return breakIndex;
		}
		return idx;
	}

	public static int findWordEdge(String text, int direction, int position, boolean skipWhitespaceToRightOfWord) {
		int i = position;
		boolean bl = direction < 0;
		int j = Math.abs(direction);
		for (int k = 0; k < j; ++k) {
			if (bl) {
				while (skipWhitespaceToRightOfWord && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
					--i;
				}
				while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
					--i;
				}
				continue;
			}
			int l = text.length();
			int m = text.indexOf(' ', i);
			int n = text.indexOf('\n', i);
			i = m == -1 && n == -1 ? -1 : (m != -1 && n != -1 ? Math.min(m, n) : (m != -1 ? m : n));
			if (i == -1) {
				i = l;
				continue;
			}
			while (skipWhitespaceToRightOfWord && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
				++i;
			}
		}
		return i;
	}

	public static List<ITextComponent> wrapLines(ITextComponent text, int width, FontRenderer fontRenderer, boolean removeWhiteSpace, boolean forceColor) {
		int lineWidth = 0;
		TextComponentString lineText = new TextComponentString("");
		ArrayList<ITextComponent> result = Lists.newArrayList();
		ArrayList<ITextComponent> stack = Lists.newArrayList(text);
		for (int i = 0; i < stack.size(); ++i) {
			ITextComponent current = stack.get(i);
			String currentString = current.getUnformattedComponentText();
			boolean breakLine = false;
			//改行コードを含む場合は以降をTextとして追加&改行
			if (currentString.contains("\n")) {
				int idx = currentString.indexOf('\n');
				String afterNewLine = currentString.substring(idx + 1);
				currentString = currentString.substring(0, idx + 1);
				ITextComponent after = new TextComponentString(afterNewLine).setStyle(current.getStyle().createDeepCopy());
				stack.add(i + 1, after);
				breakLine = true;
			}
			String coloredStr = removeTextColorsIfConfigured(current.getStyle().getFormattingCode() + currentString, forceColor);
			String beforeNewLine = coloredStr.endsWith("\n") ? coloredStr.substring(0, coloredStr.length() - 1) : coloredStr;
			int widthBefore = fontRenderer.getStringWidth(beforeNewLine);
			ITextComponent before = new TextComponentString(beforeNewLine).setStyle(current.getStyle().createDeepCopy());

			//幅がはみ出たら
			if (lineWidth + widthBefore > width) {
				//幅でカット
				String partBeforeStr = fontRenderer.trimStringToWidth(coloredStr, width - lineWidth, false);
				String partAfterStr = partBeforeStr.length() < coloredStr.length() ? coloredStr.substring(partBeforeStr.length()) : null;
				//普通はここはtrueになる
				if (partAfterStr != null && !partAfterStr.isEmpty()) {
					//before側に空白があればそこを区切りにする
					int breakIdx = partAfterStr.charAt(0) != ' ' ? partBeforeStr.lastIndexOf(' ') : partBeforeStr.length();
					//BetterLineBreak部
					Parser parser = getParser();
					IntSet breakIndices = phraseIndices(coloredStr, parser);
					for (int j = partBeforeStr.length(); j >= Math.max(1, breakIdx); j--) {
						if (canBreak(coloredStr.charAt(j - 1), coloredStr.charAt(j), j, breakIndices)) {
							breakIdx = j;
							break;
						}
					}
					if (breakIdx >= 0 && fontRenderer.getStringWidth(coloredStr.substring(0, breakIdx)) > 0) {
						partBeforeStr = coloredStr.substring(0, breakIdx);
						if (removeWhiteSpace && coloredStr.charAt(breakIdx) == ' ') {
							++breakIdx;
						}
						partAfterStr = coloredStr.substring(breakIdx);
					} else if (lineWidth > 0 && !coloredStr.contains(" ")) {
						//行の途中から開始し、空白が無い場合は新しい行から開始
						//trueになるのはTextとTextの境目しかない
						partBeforeStr = "";
						partAfterStr = coloredStr;
					}
					ITextComponent partAfter = new TextComponentString(partAfterStr).setStyle(current.getStyle().createDeepCopy());
					stack.add(i + 1, partAfter);
				}
				coloredStr = partBeforeStr;
				widthBefore = fontRenderer.getStringWidth(coloredStr);
				before = new TextComponentString(coloredStr);
				before.setStyle(current.getStyle().createDeepCopy());
				breakLine = true;
			}
			//普通はtrueじゃね？
			if (lineWidth + widthBefore <= width) {
				lineWidth += widthBefore;
				lineText.appendSibling(before);
			} else {
				breakLine = true;
			}
			if (!breakLine)
				continue;
			result.add(lineText);
			lineWidth = 0;
			lineText = new TextComponentString("");
		}
		result.add(lineText);
		return result;
	}

	public static @Nullable Parser getParser() {
		switch (ConfigHolder.client.lineBreakAlgorithm) {
			case VANILLA:
			case NON_ASCII:
				return null;
			case PHRASE:
				Language language = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
				switch (language.getLanguageCode()) {
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
				throw new IllegalStateException("Unexpected value: " + ConfigHolder.client.lineBreakAlgorithm);
		}
	}

	public static IntSet phraseIndices(String str, @Nullable Parser parser) {
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

	public static boolean canBreak(char prevChar, char c, int index, IntSet breakIndices) {
		switch (ConfigHolder.client.lineBreakAlgorithm) {
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
				return breakIndices.contains(index);
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

	private static String removeTextColorsIfConfigured(String text, boolean forceColor) {
		return !forceColor && !Minecraft.getMinecraft().gameSettings.chatColours ? TextFormatting.getTextWithoutFormattingCodes(text) : text;
	}

}
