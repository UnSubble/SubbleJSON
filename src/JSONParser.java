import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JSONParser {
	private File file;
	private FileInputStream reader;
	
	private JSONParser(File file) {
		this.file = file;
		try {
			reader = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected static JSONParser getParser(File file) {
		return new JSONParser(file);
	}
	
	protected void close() throws IOException {
		reader.close();
	}

	public JSONObject getAsJSONObject() {
		return new JSONObject();
	}
	
	public Optional<JSONObject> getObject(String key) {
		int nextInt = -1;
		List<Byte> byteList = new ArrayList<>(); 
		boolean isCloser = true;
		try {
			while ((nextInt = reader.read()) != -1) {
				if (nextInt == JSONUtil.QUOTATION && (byteList.isEmpty() ||
						byteList.get(byteList.size() - 1) != JSONUtil.BACK_SLASH)) {
					isCloser = !isCloser; 
				}
				if (isCloser) {
					
					byteList.clear();
				}
				byteList.add((byte)nextInt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (byteList.isEmpty())
			return Optional.empty();
		
	}
}
