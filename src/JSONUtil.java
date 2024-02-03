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
	
	private static boolean isNull(String str) {
		return str.equals("null");
	}
	
	static boolean equalsKeyAndList(String key, String oth) {
		return oth.equals("\"" + key);
	}
	
	static boolean isList(String str) {
		return !str.isEmpty() && str.charAt(0) == SQUARE_BRACKET_OPEN;
	}
	
	static boolean isObject(String str) {
		return !str.isEmpty() && str.charAt(0) == CURLY_BRACKETS_OPEN;
	}
	
	static boolean isNum(String str) {
		return str.chars().allMatch(JsonUtil::isNumeric);
	}
	
	static boolean isBoolean(String str) {
		return str.equals("true") || str.equals("false");
	}
	
	static boolean isNumeric(int val) {
		return Character.isDigit(val) || val == '.' || val == 'e' || val == '-' || val == 'E';
	}
	
	static boolean isString(String str) {
		return !str.isEmpty() && str.charAt(0) == QUOTATION;
	}
	
	static Object getElementAsObject(String str) {
		if (isNull(str))
			return null;
		else if (isObject(str)) 
			return convertToObject(str.substring(0, str.length() - 1));
		else if (isString(str)) 
			return str.substring(1, str.length() - 1);
		else if (isNum(str)) 
			return convertToNumber(str);
		else if (isBoolean(str)) 
			return convertToBoolean(str);
		else if (isList(str))
			return convertToList(str.substring(0, str.length() - 1));
		return null;
	}
	
	static String convertToString(StringBuilder sb) {
		return sb.toString();
	}
	
	static Number convertToNumber(String str) {
		return Double.parseDouble(str);
	}
	
	static boolean convertToBoolean(String str) {
		return Boolean.valueOf(str);
	}
	
	static List<Object> convertToList(String str) {
		List<Object> list = new ArrayList<>();
		StringBuilder element = new StringBuilder();
		boolean isString = false;
		int scope = 0;
		for (int i = 1; i < str.length(); i++) {
			char c = str.charAt(i);
			int prevIndex = i - 1;
			if (c == QUOTATION && str.charAt(prevIndex) != BACK_SLASH)
				isString = !isString;
			if (!isString) {
				if (c == CURLY_BRACKETS_OPEN)
					scope++;
				else if (c == CURLY_BRACKETS_CLOSE)
					scope--;
			}
			if (c == COMMA && !isString && scope == 0) {
				String rawElement = element.toString().trim();
				list.add(getElementAsObject(rawElement));
				element = new StringBuilder();
			} else
				element.append(c);
		}
		String rawElement = element.toString().trim();
		if (!rawElement.isEmpty()) {
			list.add(getElementAsObject(rawElement));
		}
		return list;
	}
	
	static JsonObject convertToObject(String str) {
		str = str.trim();
		JsonObject object = new JsonObject();
		StringBuilder element = new StringBuilder();
		int isArray = 0;
		boolean isString = false;
		int scope = 0;
		String key = null;
		Object value = new Object();
		for (int i = 1; i < str.length(); i++) {
			char c = str.charAt(i);
			int prevIndex = i - 1;
			if (c == QUOTATION && (i == 0 || str.charAt(prevIndex) != BACK_SLASH))
				isString = !isString;
			if (!isString) {
				if (c == CURLY_BRACKETS_OPEN) 
					scope++;
				if (c == CURLY_BRACKETS_CLOSE) 
					scope--;
				if (c == SQUARE_BRACKET_OPEN)
					isArray++;
				if (c == SQUARE_BRACKET_CLOSE)
					isArray--;
			}
			if ((c == ':' || c == COMMA) && isArray == 0 && !isString && scope == 0) {
				String rawElement = element.toString().trim();
				if (c == ':' && !rawElement.isEmpty()) 
					key = rawElement.substring(1, rawElement.length() - 1);
				else if (c == COMMA) {
					value = getElementAsObject(rawElement);
					if (key != null) {
						object.add(key, value);
						key = null;
						value = null;
					}
				}
				element = new StringBuilder();
			} else
				element.append(c);
		}
		if (!element.isEmpty() && key != null) {
			String rawElement = element.toString().trim();
			value = getElementAsObject(rawElement);
			object.add(key, value);
		}
		return object;
	}
	
}
