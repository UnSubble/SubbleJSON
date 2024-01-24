import java.util.ArrayList;
import java.util.Iterator;
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
	
	static boolean equalsKeyAndList(String key, List<Byte> byteList) {
		key = "\"" + key;
		if (key.length() != byteList.size())
			return false;
		byte[] keyArray = key.getBytes();
		for (int i = 0; i < byteList.size(); i++) {
			if (keyArray[i] != byteList.get(i))
				return false;
		}
		return true;
	}
	
	static boolean isList(List<Byte> byteList) {
		return !byteList.isEmpty() && byteList.get(0) == SQUARE_BRACKET_OPEN;
	}
	
	static boolean isObject(List<Byte> byteList) {
		return !byteList.isEmpty() && byteList.get(0) == CURLY_BRACKETS_OPEN;
	}
	
	static boolean isNum(List<Byte> byteList) {
		for (byte c : byteList) {
			if (!isNumeric(c))
				return false;
		}
		return true;
	}
	
	static boolean isBoolean(List<Byte> byteList) {
		final StringBuilder sb = new StringBuilder();
		byteList.forEach(x -> sb.append((char)x.intValue()));
		return sb.toString().equals("true") || sb.toString().equals("false");
	}
	
	static boolean isNumeric(int val) {
		return (val >= '0' && val <= '9') || val == '.';
	}
	
	static boolean isString(List<Byte> byteList) {
		return !byteList.isEmpty() && byteList.get(0) == QUOTATION;
	}
	
	static List<Byte> trim(List<Byte> byteList) {
		int startIndex = 0;
		int end = byteList.size();
		for (byte b : byteList) {
			if (b <= 32)
				startIndex++;
			else
				break;
		}
		for (int i = end - 1; i >= 0; i--) {
			byte b = byteList.get(i);
			if (b <= 32 || b == CURLY_BRACKETS_CLOSE)
				end--;
			else 
				break;
		}
		return byteList.subList(startIndex, end);
	}
	
	static Object getElementAsObject(List<Byte> byteList) {
		if (isObject(byteList)) 
			return convertToObject(byteList.subList(1, byteList.size()));
		else if (isString(byteList)) 
			return convertToString(byteList.subList(1, byteList.size() - 1));
		else if (isNum(byteList)) 
			return convertToNumber(byteList);
		else if (isBoolean(byteList)) 
			return convertToBoolean(byteList);
		return null;
	}
	
	static String convertToString(List<Byte> byteList) {
		final StringBuilder sb = new StringBuilder();
		byteList.forEach(x -> sb.append((char)x.intValue()));
		return sb.toString();
	}
	
	static Number convertToNumber(List<Byte> byteList) {
		final StringBuilder sb = new StringBuilder();
		byteList.forEach(x -> sb.append((char)x.intValue()));
		return Double.parseDouble(sb.toString());
	}
	
	static boolean convertToBoolean(List<Byte> byteList) {
		final StringBuilder sb = new StringBuilder();
		byteList.forEach(x -> sb.append((char)x.intValue()));
		return Boolean.valueOf(sb.toString());
	}
	
	static List<Object> convertToList(List<Byte> byteList) {
		List<Object> list = new ArrayList<>();
		List<Byte> element = new ArrayList<>();
		boolean isString = false;
		int scope = 0;
		for (int i = 1; i < byteList.size(); i++) {
			byte b = byteList.get(i);
			if (b == QUOTATION && byteList.get(i - 1) != BACK_SLASH)
				isString = !isString;
			if (!isString) {
				if (b == CURLY_BRACKETS_OPEN)
					scope++;
				else if (b == CURLY_BRACKETS_CLOSE)
					scope--;
			}
			if (b == COMMA && !isString && scope == 0) {
				List<Byte> rawElement = trim(element);
				list.add(getElementAsObject(rawElement));
				element.clear();
			} else
				element.add(b);
		}
		if (!element.isEmpty()) {
			List<Byte> rawElement = trim(element);
			list.add(getElementAsObject(rawElement));
		}
		return list;
	}
	
	static JsonObject convertToObject(List<Byte> byteList) {
		byteList = trim(byteList);
		JsonObject object = new JsonObject();
		List<Byte> element = new ArrayList<>();
		boolean isArray = false;
		boolean isString = false;
		int scope = 0;
		String key = null;
		Object value = null;
		for (int i = 0; i < byteList.size(); i++) {
			byte b = byteList.get(i);
			if (b == QUOTATION && (i == 0 || byteList.get(i - 1) == BACK_SLASH))
				isString = !isString;
			if (!isString) {
				if (b == CURLY_BRACKETS_OPEN) 
					scope++;
				if (b == CURLY_BRACKETS_CLOSE) 
					scope--;
				if (b == SQUARE_BRACKET_OPEN || b == SQUARE_BRACKET_CLOSE)
					isArray = b == SQUARE_BRACKET_OPEN;
			}
			if ((b == ':' || b == COMMA) && !isArray && !isString && scope == 0) {
				List<Byte> rawElement = trim(element);
				if (key == null) 
					key = convertToString(rawElement.subList(1, rawElement.size() - 1));
				else 
					value = getElementAsObject(rawElement);
				element.clear();
			} else
				element.add(b);
			if (key != null && value != null) {
				object.add(key, value);
				key = null;
				value = null;
			}
		}
		element = trim(element);
		value = getElementAsObject(element);
		object.add(key, value);
		return object;
	}
	
}
