/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kpan.b_line_break.budoux;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * HTMLは使用しないので、translateHTMLStringを削除しています。
 * totalScoreをキャッシュしています。
 * シンプルなCacheクラスを作成しています。
 */
public class Parser {
	private final Map<String, Map<String, Integer>> model;
	private final int totalScore;

	/**
	 * Constructs a BudouX parser.
	 *
	 * @param model the model data.
	 */
	public Parser(Map<String, Map<String, Integer>> model) {
		this.model = model;
		totalScore =
				model.values().stream()
						.mapToInt(group -> group.values().stream().mapToInt(Integer::intValue).sum())
						.sum();
	}

	/**
	 * Loads the default Japanese parser.
	 *
	 * @return a BudouX parser with the default Japanese model.
	 */
	public static Parser loadDefaultJapaneseParser() {
		return loadByFileName("/models/ja.json");
	}

	/**
	 * Loads the default Simplified Chinese parser.
	 *
	 * @return a BudouX parser with the default Simplified Chinese model.
	 */
	public static Parser loadDefaultSimplifiedChineseParser() {
		return loadByFileName("/models/zh-hans.json");
	}

	/**
	 * Loads the default Traditional Chinese parser.
	 *
	 * @return a BudouX parser with the default Traditional Chinese model.
	 */
	public static Parser loadDefaultTraditionalChineseParser() {
		return loadByFileName("/models/zh-hant.json");
	}

	/**
	 * Loads the default Thai parser.
	 *
	 * @return a BudouX parser with the default Thai model.
	 */
	public static Parser loadDefaultThaiParser() {
		return loadByFileName("/models/th.json");
	}

	/**
	 * Loads a parser by specifying the model file path.
	 *
	 * @param modelFileName the model file path.
	 * @return a BudouX parser.
	 */
	public static Parser loadByFileName(String modelFileName) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Map<String, Integer>>>() {
		}.getType();
		InputStream inputStream = Parser.class.getResourceAsStream(modelFileName);
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			Map<String, Map<String, Integer>> model = gson.fromJson(reader, type);
			return new Parser(model);
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Gets the score for the specified feature of the given sequence.
	 *
	 * @param featureKey the feature key to examine.
	 * @param sequence   the sequence to look up the score.
	 * @return the contribution score to support a phrase break.
	 */
	private int getScore(String featureKey, String sequence) {
		return Optional.ofNullable(model.get(featureKey))
				.map(group -> group.get(sequence))
				.orElse(0);
	}

	/**
	 * Parses a sentence into phrases.
	 *
	 * @param sentence the sentence to break by phrase.
	 * @return a list of phrases.
	 */
	public List<String> parse(String sentence) {
		if (sentence.isEmpty()) {
			return new ArrayList<>();
		}
		List<String> result = new ArrayList<>();
		result.add(String.valueOf(sentence.charAt(0)));
		for (int i = 1; i < sentence.length(); i++) {
			int score = -totalScore;
			if (i - 2 > 0) {
				score += 2 * getScore("UW1", sentence.substring(i - 3, i - 2));
			}
			if (i - 1 > 0) {
				score += 2 * getScore("UW2", sentence.substring(i - 2, i - 1));
			}
			score += 2 * getScore("UW3", sentence.substring(i - 1, i));
			score += 2 * getScore("UW4", sentence.substring(i, i + 1));
			if (i + 1 < sentence.length()) {
				score += 2 * getScore("UW5", sentence.substring(i + 1, i + 2));
			}
			if (i + 2 < sentence.length()) {
				score += 2 * getScore("UW6", sentence.substring(i + 2, i + 3));
			}
			if (i > 1) {
				score += 2 * getScore("BW1", sentence.substring(i - 2, i));
			}
			score += 2 * getScore("BW2", sentence.substring(i - 1, i + 1));
			if (i + 1 < sentence.length()) {
				score += 2 * getScore("BW3", sentence.substring(i, i + 2));
			}
			if (i - 2 > 0) {
				score += 2 * getScore("TW1", sentence.substring(i - 3, i));
			}
			if (i - 1 > 0) {
				score += 2 * getScore("TW2", sentence.substring(i - 2, i + 1));
			}
			if (i + 1 < sentence.length()) {
				score += 2 * getScore("TW3", sentence.substring(i - 1, i + 2));
			}
			if (i + 2 < sentence.length()) {
				score += 2 * getScore("TW4", sentence.substring(i, i + 3));
			}
			if (score > 0) {
				result.add("");
			}
			result.set(result.size() - 1, result.get(result.size() - 1) + sentence.charAt(i));
		}
		return result;
	}

	/*
	流石に毎回ファイルIOが走るのは良くないのでキャッシュ
	 */
	public static class Cache {

		private static @Nullable String modelFileName = null;
		private static Parser parser;

		public static Parser getOrLoad(String modelFileName) {
			if (modelFileName.equals(Cache.modelFileName))
				return parser;
			Cache.modelFileName = modelFileName;
			parser = Parser.loadByFileName(modelFileName);
			return parser;
		}
	}
}
