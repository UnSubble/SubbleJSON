import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class JsonParser {
	private File file;
	private BufferedReader reader;
	
	private JsonParser(File file) {
		try {
			this.file = file;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static JsonParser getParser(File file) {
		return new JsonParser(file);
	}
	
	protected void close() throws IOException {
		reader.close();
	}
	
	public void resetCursor() throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	}
	
	private void skipToValue() {
		long index = 0L;
		int nextInt = -1;
		try {
			reader.mark(0);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == '\s' || nextInt == '=' || nextInt == '{' || nextInt == ':')
					index++;
				else
					break;
			}
			reader.reset();
			reader.skip(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean jumpToStartIndexOfValue(String key) {
		int nextInt = -1;
		List<Byte> byteList = new ArrayList<>(); 
		boolean isCloser = true;
		try {
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.QUOTATION && (byteList.isEmpty() ||
						byteList.get(byteList.size() - 1) != JsonUtil.BACK_SLASH)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.equalsKeyAndList(key, byteList)) {
						skipToValue();
						return true;
					}
					byteList.clear();
					continue;
				}
				byteList.add((byte)nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Optional<String> nextString(String key) {
		List<Byte> byteList = new ArrayList<>();
		boolean isCloser = true;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.QUOTATION && (byteList.isEmpty() ||
						byteList.get(byteList.size() - 1) != JsonUtil.BACK_SLASH)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isString(byteList))
						break;
					else {
						jumpToStartIndexOfValue(key);
						byteList.clear();
					}
				}
				byteList.add((byte)nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!JsonUtil.isString(byteList))
			return Optional.empty();
		String val = JsonUtil.convertToString(byteList.subList(1, byteList.size()));
		return Optional.of(val);
	}
	
	public Optional<Number> nextNumber(String key) {
		List<Byte> byteList = new ArrayList<>();
		boolean isCloser = false;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (!JsonUtil.isNumeric(nextInt)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isNum(byteList)) {
						Number num = JsonUtil.convertToNumber(byteList);
						return Optional.of(num);
					} else {
						jumpToStartIndexOfValue(key);
						byteList.clear();
					}
				}
				byteList.add((byte)nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public Optional<Boolean> nextBoolean(String key) {
		List<Byte> byteList = new ArrayList<>();
		boolean isCloser = false;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.COMMA) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isBoolean(byteList)) {
						boolean bool = JsonUtil.convertToBoolean(byteList);
						return Optional.of(bool);
					} else {
						jumpToStartIndexOfValue(key);
						byteList.clear();
					}
				}
				byteList.add((byte)nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public Optional<List<?>> nextList(String key) {
		List<Byte> byteList = new ArrayList<>();
		boolean isCloser = true;
		boolean isStr = false;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.QUOTATION && 
						byteList.get(byteList.size() - 1) != JsonUtil.BACK_SLASH) {
					isStr = !isStr;
				}
				if (!isStr && (nextInt == JsonUtil.SQUARE_BRACKET_OPEN || 
						nextInt == JsonUtil.SQUARE_BRACKET_CLOSE)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isList(byteList)) {
						List<?> list = JsonUtil.convertToList(byteList);
						return Optional.of(list);
					} else {
						jumpToStartIndexOfValue(key);
						byteList.clear();
					}
				}
				byteList.add((byte)nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
}
