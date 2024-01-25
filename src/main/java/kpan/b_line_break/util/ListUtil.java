package kpan.b_line_break.util;

import java.util.List;

public class ListUtil {

	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}
	public static <T> void setLast(List<T> list, T value) {
		list.set(list.size() - 1, value);
	}
}
