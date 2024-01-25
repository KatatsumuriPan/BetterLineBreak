package kpan.b_line_break.util;

public class IntegerUtil {
	public static int parseInt(String text) throws NumberFormatException {
		String sign = null;
		if (text.startsWith("+") || text.startsWith("-")) {
			sign = text.substring(0, 1);
			text = text.substring(1);
		}
		int radix = 10;
		if (text.startsWith("0x") || text.startsWith("0X")) {
			radix = 16;
			text = text.substring(2);
		} else if (text.startsWith("0b") || text.startsWith("0B")) {
			radix = 2;
			text = text.substring(2);
		} else if (text.startsWith("0o") || text.startsWith("0O")) {
			radix = 8;
			text = text.substring(2);
		}
		if (sign != null) {
			text = sign + text;
		}
		return Integer.parseInt(text, radix);
	}
	public static long parseLong(String text) throws NumberFormatException {
		String sign = null;
		if (text.startsWith("+") || text.startsWith("-")) {
			sign = text.substring(0, 1);
			text = text.substring(1);
		}
		int radix = 10;
		if (text.startsWith("0x") || text.startsWith("0X")) {
			radix = 16;
			text = text.substring(2);
		} else if (text.startsWith("0b") || text.startsWith("0B")) {
			radix = 2;
			text = text.substring(2);
		} else if (text.startsWith("0o") || text.startsWith("0O")) {
			radix = 8;
			text = text.substring(2);
		}
		if (sign != null) {
			text = sign + text;
		}
		return Long.parseLong(text, radix);
	}
}
