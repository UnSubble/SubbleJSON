
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
	private StringBuilder sb;
	
	private JsonParser(Path path, Charset charset) {
		try {
			jsonStr = Files.readString(path, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		length = jsonStr.length();
		sb = new StringBuilder();
	}
	
	protected static JsonParser getParser(Path path, Charset charset) {
		return new JsonParser(path, charset);
	}
	
	public void reset() throws IOException {
		index = 0;
	}
	
	private void skipToValue() {		
		while (index < length) {
			char nextChar = jsonStr.charAt(index);
			if (!(nextChar <= 32 || nextChar == '=' || nextChar == ':')) 
				return;
			index++;
		}
	}
	
	private void jumpToStartIndex(String key) {
		sb.setLength(0);
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
					break;
				}
				sb.setLength(0);;
				continue;
			}
			sb.append(nextChar);
		}
		sb.setLength(0);
	}
	
	private <T> Optional<T> getValue(String key, boolean isCloser, Predicate<String> predicate,
			IntPredicate intPredicate, Function<String, T> function) {
		jumpToStartIndex(key);
		if (!sb.isEmpty())
			sb.setLength(0);
		for (; index < length; index++) {
			char nextChar = jsonStr.charAt(index);
			if (intPredicate.test(nextChar) && (index == 0 ||
					jsonStr.charAt(index - 1) != JsonUtil.BACK_SLASH)) {
				isCloser = !isCloser; 
			}
			if (isCloser) {
				if (predicate.test(sb.toString())) {
					T val = function.apply(sb.toString());
					index++;
					return Optional.of(val);
				} else {
					jumpToStartIndex(key);
					sb.setLength(0);;
				}
			}
			sb.append(nextChar);
			
		}
		return Optional.empty();
	}
	
	private <T> Optional<T> getComplexValue(String key, Predicate<String> predicate,
			IntFunction<Integer> intFunction, Function<String, T> function) {
		boolean isStr = false;
		int scope = 0;
		if (key == null)
			key = "";
		jumpToStartIndex(key);
		if (!sb.isEmpty())
			sb.setLength(0);
		for (; index < length; index++) {
			char nextChar = jsonStr.charAt(index);
			if (nextChar == JsonUtil.QUOTATION && 
					jsonStr.charAt(index - 1) != JsonUtil.BACK_SLASH) {
				isStr = !isStr;
			}
			if (!isStr) 
				scope += intFunction.apply(nextChar);
			if (scope == 0) {
				if (predicate.test(sb.toString())) {
					T val = function.apply(sb.toString());
					index++;
					return Optional.of(val);
				} else {
					jumpToStartIndex(key);
					sb.setLength(0);;
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
		return getComplexValue(key, JsonUtil::isList, i -> i == JsonUtil.SQUARE_BRACKET_OPEN ? 1 :
			(i == JsonUtil.SQUARE_BRACKET_CLOSE ? -1 : 0), JsonUtil::convertToList);
	}
	
	public Optional<JsonObject> nextObject(String key) {
		return getComplexValue(key, JsonUtil::isObject, i -> i == JsonUtil.CURLY_BRACKETS_OPEN ? 1 :
			(i == JsonUtil.CURLY_BRACKETS_CLOSE ? -1 : 0), JsonUtil::convertToObject);
	}
	
}
