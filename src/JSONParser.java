
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class JsonParser {
	private String jsonStr;
	private int index;
	private int length;
	
	private JsonParser(Path path, Charset charset) {
		try {
			jsonStr = Files.readString(path, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		length = jsonStr.length();
	}
	
	protected static JsonParser getParser(Path path, Charset charset) {
		return new JsonParser(path, charset);
	}
	
	public void resetCursor() throws IOException {
		index = 0;
	}
	
	private void skipToValue() {		
		for (; index < length; index++) {
			char nextChar = jsonStr.charAt(index);
			if (!(nextChar == '\s' || nextChar == '=' || nextChar == ':' || nextChar == '\n')) 
				break;
		}
	}
	
	private void jumpToStartIndexOfValue(String key) {
		StringBuilder sb = new StringBuilder();
		boolean isCloser = true;
		if (key.isEmpty())
			return;
		for (; index < length; index++) {
			char nextChar = jsonStr.charAt(index);
			if (nextChar == JsonUtil.QUOTATION && (index == 0 ||
					jsonStr.charAt(index - 1) != JsonUtil.BACK_SLASH)) {
				isCloser = !isCloser; 
			}
			if (isCloser) {
				if (JsonUtil.equalsKeyAndList(key, sb.toString())) {
					index++;
					skipToValue();
					return;
				}
				sb = new StringBuilder();
				continue;
			}
			sb.append(nextChar);
		}
	}
	
	private <T> Optional<T> getValue(String key, boolean isCloser, Predicate<StringBuilder> predicate,
			IntPredicate intPredicate, Function<StringBuilder, T> function) {
		StringBuilder sb = new StringBuilder();
		jumpToStartIndexOfValue(key);
		for (; index < length; index++) {
			char nextChar = jsonStr.charAt(index);
			if (intPredicate.test(nextChar) && (index == 0 ||
					jsonStr.charAt(index - 1) != JsonUtil.BACK_SLASH)) {
				isCloser = !isCloser; 
			}
			if (isCloser) {
				if (predicate.test(sb)) {
					T val = function.apply(sb);
					index++;
					return Optional.of(val);
				} else {
					jumpToStartIndexOfValue(key);
					sb = new StringBuilder();
				}
			}
			sb.append(nextChar);
			
		}
		return Optional.empty();
	}
	
	private <T> Optional<T> getBigValue(String key, Predicate<StringBuilder> predicate,
			IntFunction<Integer> intFunction, Function<StringBuilder, T> function) {
		boolean isStr = false;
		int scope = 0;
		if (key == null)
			key = "";
		StringBuilder sb = new StringBuilder();
		jumpToStartIndexOfValue(key);
		for (; index < length; index++) {
			char nextChar = jsonStr.charAt(index);
			if (nextChar == JsonUtil.QUOTATION && 
					jsonStr.charAt(index - 1) != JsonUtil.BACK_SLASH) {
				isStr = !isStr;
			}
			if (!isStr) 
				scope += intFunction.apply(nextChar);
			if (scope == 0) {
				if (predicate.test(sb)) {
					T val = function.apply(sb);
					index++;
					return Optional.of(val);
				} else {
					jumpToStartIndexOfValue(key);
					sb = new StringBuilder();
				}
			}
			sb.append(nextChar);
		}
		return Optional.empty();
	}
	
	public Optional<String> nextString(String key) {
		return getValue(key, true, JsonUtil::isString, 
				i -> i == JsonUtil.QUOTATION, str -> str.substring(1, str.length()));
	}
	
	public Optional<Number> nextNumber(String key) {
		return getValue(key, false, JsonUtil::isNum,
				i -> i == JsonUtil.COMMA, JsonUtil::convertToNumber);
	}
	
	public Optional<Boolean> nextBoolean(String key) {
		return getValue(key, false, JsonUtil::isBoolean,
				i -> i == JsonUtil.COMMA, JsonUtil::convertToBoolean);
	}

	public Optional<List<?>> nextList(String key) {
		return getBigValue(key, JsonUtil::isList, i -> i == JsonUtil.SQUARE_BRACKET_OPEN ? 1 :
			(i == JsonUtil.SQUARE_BRACKET_CLOSE ? -1 : 0), JsonUtil::convertToList);
	}
	
	public Optional<JsonObject> nextObject(String key) {
		return getBigValue(key, JsonUtil::isObject, i -> i == JsonUtil.CURLY_BRACKETS_OPEN ? 1 :
			(i == JsonUtil.CURLY_BRACKETS_CLOSE ? -1 : 0), JsonUtil::convertToObject);
	}
	
}
