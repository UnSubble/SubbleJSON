import java.util.ArrayList;
import java.util.List;

public final class JsonUtil {
	
	static final char QUOTATION = '"';
	static final char BACK_SLASH = '\\';
	static final char COMMA = ',';
	static final char SQUARE_BRACKET_OPEN = '[';
	static final char SQUARE_BRACKET_CLOSE = ']';
	static final char CURLY_BRACKETS_OPEN = '{';
	static final char CURLY_BRACKETS_CLOSE = '}';
	
	private JsonUtil() {	
	}
	
	private static boolean isNull(List<Integer> intList) {
		final StringBuilder sb = new StringBuilder();
		intList.forEach(x -> sb.append((char)x.intValue()));
		return sb.toString().equals("null");
	}
	
	static boolean equalsKeyAndList(String key, List<Integer> intList) {
		key = "\"" + key;
		if (key.length() != intList.size())
			return false;
		byte[] keyArray = key.getBytes();
		for (int i = 0; i < intList.size(); i++) {
			if (keyArray[i] != intList.get(i))
				return false;
		}
		return true;
	}
	
	static boolean isList(List<Integer> intList) {
		return !intList.isEmpty() && intList.get(0) == SQUARE_BRACKET_OPEN;
	}
	
	static boolean isObject(List<Integer> intList) {
		return !intList.isEmpty() && intList.get(0) == CURLY_BRACKETS_OPEN;
	}
	
	static boolean isNum(List<Integer> intList) {
		for (int c : intList) {
			if (!isNumeric(c))
				return false;
		}
		return true;
	}
	
	static boolean isBoolean(List<Integer> intList) {
		final StringBuilder sb = new StringBuilder();
		intList.forEach(x -> sb.append((char)x.intValue()));
		return sb.toString().equals("true") || sb.toString().equals("false");
	}
	
	static boolean isNumeric(int val) {
		return (val >= '0' && val <= '9') || val == '.' || val == 'e' || val == '-' || val == 'E';
	}
	
	static boolean isString(List<Integer> intList) {
		return !intList.isEmpty() && intList.get(0) == QUOTATION;
	}
	
	static List<Integer> trim(List<Integer> intList) {
		int startIndex = 0;
		for (int b : intList) {
			if (b <= 32)
				startIndex++;
			else
				break;
		}
		intList = intList.subList(startIndex, intList.size());
		int end = intList.size();
		for (int i = end - 1; i >= 0; i--) {
			int b = intList.get(i);
			if (b <= 32)
				end--;
			else 
				break;
		}
		return intList.subList(0, end);
	}
	
	static Object getElementAsObject(List<Integer> intList) {
		if (isNull(intList))
			return null;
		else if (isObject(intList)) 
			return convertToObject(intList.subList(1, intList.size() - 1));
		else if (isString(intList)) 
			return convertToString(intList.subList(1, intList.size() - 1));
		else if (isNum(intList)) 
			return convertToNumber(intList);
		else if (isBoolean(intList)) 
			return convertToBoolean(intList);
		else if (isList(intList))
			return convertToList(intList.subList(0, intList.size() - 1));
		return null;
	}
	
	static String convertToString(List<Integer> intList) {
		final StringBuilder sb = new StringBuilder();
		intList.forEach(x -> sb.append((char)x.intValue()));
		return sb.toString();
	}
	
	static Number convertToNumber(List<Integer> intList) {
		final StringBuilder sb = new StringBuilder();
		intList.forEach(x -> sb.append((char)x.intValue()));
		return Double.parseDouble(sb.toString());
	}
	
	static boolean convertToBoolean(List<Integer> intList) {
		final StringBuilder sb = new StringBuilder();
		intList.forEach(x -> sb.append((char)x.intValue()));
		return Boolean.valueOf(sb.toString());
	}
	
	static List<Object> convertToList(List<Integer> intList) {
		List<Object> list = new ArrayList<>();
		List<Integer> element = new ArrayList<>();
		boolean isString = false;
		int scope = 0;
		for (int i = 1; i < intList.size(); i++) {
			int b = intList.get(i);
			if (b == QUOTATION && intList.get(i - 1) != BACK_SLASH)
				isString = !isString;
			if (!isString) {
				if (b == CURLY_BRACKETS_OPEN)
					scope++;
				else if (b == CURLY_BRACKETS_CLOSE)
					scope--;
			}
			if (b == COMMA && !isString && scope == 0) {
				List<Integer> rawElement = trim(element);
				list.add(getElementAsObject(rawElement));
				element.clear();
			} else
				element.add(b);
		}
		List<Integer> rawElement = trim(element);
		if (!rawElement.isEmpty()) {
			list.add(getElementAsObject(rawElement));
		}
		return list;
	}
	
	static JsonObject convertToObject(List<Integer> intList) {
		intList = trim(intList);
		JsonObject object = new JsonObject();
		List<Integer> element = new ArrayList<>();
		int isArray = 0;
		boolean isString = false;
		int scope = 0;
		String key = null;
		Object value = new Object();
		for (int i = 0; i < intList.size(); i++) {
			int b = intList.get(i);
			if (b == QUOTATION && (i == 0 || intList.get(i - 1) != BACK_SLASH))
				isString = !isString;
			if (!isString) {
				if (b == CURLY_BRACKETS_OPEN) 
					scope++;
				if (b == CURLY_BRACKETS_CLOSE) 
					scope--;
				if (b == SQUARE_BRACKET_OPEN)
					isArray++;
				if (b == SQUARE_BRACKET_CLOSE)
					isArray--;
			}
			if ((b == ':' || b == COMMA) && isArray == 0 && !isString && scope == 0) {
				List<Integer> rawElement = trim(element);	
				if (b == ':' && !rawElement.isEmpty()) 
					key = convertToString(rawElement.subList(1, rawElement.size() - 1));
				else if (b == COMMA) {
					value = getElementAsObject(rawElement);
					if (key != null) {
						object.add(key, value);
						key = null;
						value = null;
					}
				}
				element.clear();
			} else
				element.add(b);
		}
		if (!element.isEmpty() && key != null) {
			element = trim(element);
			value = getElementAsObject(element);
			object.add(key, value);
		}
		return object;
	}
	
}
