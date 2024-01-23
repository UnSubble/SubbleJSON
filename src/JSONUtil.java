import java.util.List;

public final class JsonUtil {
	
	static final char QUOTATION = '"';
	static final char BACK_SLASH = '\\';
	static final char COMMA = ',';
	
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
		return !byteList.isEmpty() && byteList.get(0) == JsonUtil.QUOTATION;
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
	
}
