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

public class JsonParser {
	private BufferedReader reader;
	
	private JsonParser(File file) {
		try {
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
	
	public Optional<String> getString(String key) {
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

}
