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
	
	private static boolean isNull(StringBuilder sb) {
		return sb.toString().equals("null");
	}
	
	static boolean equalsKeyAndList(String key, String oth) {
		key = "\"" + key;
		return key.equals(oth);
	}
	
	static boolean isList(StringBuilder sb) {
		return !sb.isEmpty() && sb.charAt(0) == SQUARE_BRACKET_OPEN;
	}
	
	static boolean isObject(StringBuilder sb) {
		return !sb.isEmpty() && sb.charAt(0) == CURLY_BRACKETS_OPEN;
	}
	
	static boolean isNum(StringBuilder sb) {
		return sb.chars().allMatch(JsonUtil::isNumeric);
	}
	
	static boolean isBoolean(StringBuilder sb) {
		return sb.toString().equals("true") || sb.toString().equals("false");
	}
	
	static boolean isNumeric(int val) {
		return (val >= '0' && val <= '9') || val == '.' || val == 'e' || val == '-' || val == 'E';
	}
	
	static boolean isString(StringBuilder sb) {
		return !sb.isEmpty() && sb.charAt(0) == QUOTATION;
	}
	
	static String trim(String str) {
		int startIndex = 0;
		for (char c : str.toCharArray()) {
			if (c <= 32)
				startIndex++;
			else
				break;
		}
		str = str.substring(startIndex, str.length());
		int end = str.length();
		for (int i = end - 1; i >= 0; i--) {
			char c = str.charAt(i);
			if (c <= 32)
				end--;
			else 
				break;
		}
		return str.substring(0, end);
	}
	
	static Object getElementAsObject(StringBuilder sb) {
		if (isNull(sb))
			return null;
		else if (isObject(sb)) 
			return convertToObject(new StringBuilder(sb.substring(0, sb.length() - 1)));
		else if (isString(sb)) 
			return convertToString(new StringBuilder(sb.substring(1, sb.length() - 1)));
		else if (isNum(sb)) 
			return convertToNumber(sb);
		else if (isBoolean(sb)) 
			return convertToBoolean(sb);
		else if (isList(sb))
			return convertToList(new StringBuilder(sb.substring(0, sb.length() - 1)));
		return null;
	}
	
	static String convertToString(StringBuilder sb) {
		return sb.toString();
	}
	
	static Number convertToNumber(StringBuilder sb) {
		return Double.parseDouble(sb.toString());
	}
	
	static boolean convertToBoolean(StringBuilder sb) {
		return Boolean.valueOf(sb.toString());
	}
	
	static List<Object> convertToList(StringBuilder sb) {
		List<Object> list = new ArrayList<>();
		StringBuilder element = new StringBuilder();
		boolean isString = false;
		int scope = 0;
		for (int i = 1; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == QUOTATION && sb.charAt(i - 1) != BACK_SLASH)
				isString = !isString;
			if (!isString) {
				if (c == CURLY_BRACKETS_OPEN)
					scope++;
				else if (c == CURLY_BRACKETS_CLOSE)
					scope--;
			}
			if (c == COMMA && !isString && scope == 0) {
				String rawElement = trim(element.toString());
				list.add(getElementAsObject(new StringBuilder(rawElement)));
				element = new StringBuilder();
			} else
				element.append(c);
		}
		String rawElement = trim(element.toString());
		if (!rawElement.isEmpty()) {
			list.add(getElementAsObject(new StringBuilder(rawElement)));
		}
		return list;
	}
	
	static JsonObject convertToObject(StringBuilder sb) {
		String str = trim(sb.toString());
		JsonObject object = new JsonObject();
		StringBuilder element = new StringBuilder();
		int isArray = 0;
		boolean isString = false;
		int scope = 0;
		String key = null;
		Object value = new Object();
		for (int i = 1; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == QUOTATION && (i == 0 || str.charAt(i - 1) != BACK_SLASH))
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
				String rawElement = trim(element.toString());
				if (c == ':' && !rawElement.isEmpty()) 
					key = rawElement.substring(1, rawElement.length() - 1);
				else if (c == COMMA) {
					value = getElementAsObject(new StringBuilder(rawElement));
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
			String rawElement = trim(element.toString());
			value = getElementAsObject(new StringBuilder(rawElement));
			object.add(key, value);
		}
		return object;
	}
	
}
