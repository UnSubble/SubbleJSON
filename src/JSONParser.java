import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonParser {
	private File file;
	private BufferedReader reader;
	
	private JsonParser(File file, Charset charset) {
		try {
			this.file = file;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static JsonParser getParser(File file, Charset charset) {
		return new JsonParser(file, charset);
	}
	
	protected void close() throws IOException {
		reader.close();
	}
	
	public void resetCursor() throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	}
	
	private void skipToValue() {
		int nextInt = -1;
		try {			
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == '\s' || nextInt == '=' || nextInt == ':') {
					reader.mark(1);
				}
				else
					break;
			}
			reader.reset();
			reader.skip(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void jumpToStartIndexOfValue(String key) {
		int nextInt = -1;
		List<Integer> intList = new ArrayList<>(); 
		boolean isCloser = true;
		if ("".equals(key))
			return;
		try {
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.QUOTATION && (intList.isEmpty() ||
						intList.get(intList.size() - 1) != JsonUtil.BACK_SLASH)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.equalsKeyAndList(key, intList)) {
						skipToValue();
						return;
					}
					intList.clear();
					continue;
				}
				intList.add(nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public Optional<String> nextString(String key) {
		List<Integer> intList = new ArrayList<>();
		boolean isCloser = true;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.QUOTATION && (intList.isEmpty() ||
						intList.get(intList.size() - 1) != JsonUtil.BACK_SLASH)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isString(intList)) {
						String val = JsonUtil.convertToString(intList.subList(1, intList.size()));
						return Optional.of(val);
					} else {
						jumpToStartIndexOfValue(key);
						intList.clear();
					}
				}
				intList.add(nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public Optional<Number> nextNumber(String key) {
		List<Integer> intList = new ArrayList<>();
		boolean isCloser = false;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (!JsonUtil.isNumeric(nextInt)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isNum(intList)) {
						Number num = JsonUtil.convertToNumber(intList);
						return Optional.of(num);
					} else {
						jumpToStartIndexOfValue(key);
						intList.clear();
					}
				}
				intList.add(nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public Optional<Boolean> nextBoolean(String key) {
		List<Integer> intList = new ArrayList<>();
		boolean isCloser = false;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.COMMA) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					if (JsonUtil.isBoolean(intList)) {
						boolean bool = JsonUtil.convertToBoolean(intList);
						return Optional.of(bool);
					} else {
						jumpToStartIndexOfValue(key);
						intList.clear();
					}
				}
				intList.add(nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public Optional<List<?>> nextList(String key) {
		List<Integer> intList = new ArrayList<>();
		boolean isStr = false;
		int scope = 0;
		if (key == null)
			key = "";
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.QUOTATION && 
						intList.get(intList.size() - 1) != JsonUtil.BACK_SLASH) {
					isStr = !isStr;
				}
				if (!isStr) {
					if (nextInt == JsonUtil.SQUARE_BRACKET_OPEN)
						scope++;
					if (nextInt == JsonUtil.SQUARE_BRACKET_CLOSE)
						scope--;
				}
				if (scope == 0) {
					if (JsonUtil.isList(intList)) {
						List<?> list = JsonUtil.convertToList(intList);
						return Optional.of(list);
					} else {
						jumpToStartIndexOfValue(key);
						intList.clear();
					}
				}
				intList.add(nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public Optional<JsonObject> nextObject(String key) {
		if (key == null)
			key = "";
		List<Integer> intList = new ArrayList<>();
		int scope = 0;
		try {
			int nextInt = -1;
			jumpToStartIndexOfValue(key);
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JsonUtil.CURLY_BRACKETS_OPEN) 
					scope++; 
				if (nextInt == JsonUtil.CURLY_BRACKETS_CLOSE)
					scope--;
				if (scope == 0) {
					if (JsonUtil.isObject(intList)) {
						JsonObject obj = JsonUtil.convertToObject(intList.subList(1, intList.size()));
						return Optional.of(obj);
					} else {
						jumpToStartIndexOfValue(key);
						intList.clear();
					}
				}
				intList.add(nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
}
