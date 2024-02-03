import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class JsonBuilder {
	private Path path;
	private Charset charset;
	private StringBuilder sb;
	
	private JsonBuilder(Path path, Charset charset) {
		this.path = path;
		this.charset = charset;
		this.sb = new StringBuilder();
	}
	
	protected static JsonBuilder getBuilder(Path path, Charset charset) {
		return new JsonBuilder(path, charset);
	}

	private void write() {
		try {
			BufferedWriter writer = Files.newBufferedWriter(path, charset);
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void clearFile() {
		try (PrintWriter pw = new PrintWriter(path.toFile())) {
			pw.write("");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void writeKey(String key) throws IOException {
		for (char c : key.toCharArray()) {
			sb.append(c);
		}
		sb.append(':').append(' ');
	}
	
	private void writeValue(String value, boolean isLast) throws IOException {
		for (char c : value.toCharArray()) {
			sb.append(c);
		}
		if (!isLast)
			sb.append(JsonUtil.COMMA);
	}
	
	private void writeSpaces(int space) throws IOException{
		for (int i = 0; i < space; i++) {
			sb.append(' ');
		}
	}
	
	private void writeObject(String key, JsonObject object, boolean isMain, int space) {
		List<String> keys = object.getKeys();
		List<?> values = object.getValues();
		try {
			if (key != null)
				writeKey(key);	
			sb.append('{').append('\n');
			for (int i = 0; i < keys.size(); i++) {
				writeSpaces(space + 2);
				String k = "\"" + keys.get(i) + "\"";
				Object v = values.get(i);
				if (v instanceof String) {
					writeKey(k);
					writeValue(v == null ? "null" : "\"" + v.toString() + "\"", i == keys.size() - 1);
				} else if (v instanceof JsonObject) {
					writeObject(k, (JsonObject)v, i == keys.size() - 1, space + 4);
				} else if (v instanceof List<?>)
					writeList(k, (List<?>)v, i == keys.size() - 1, space + 2);
				else  {
					writeKey(k);
					writeValue(v == null ? "null" : v.toString(), i == keys.size() - 1);
				}
				sb.append('\n');
			}
			writeSpaces(space);
			sb.append('}');
			if (!isMain)
				sb.append(',');
		} catch (IOException e) {
		}
	}
	
	private void writeList(String key, List<?> list, boolean isMain, int space) {
		try {
			if (key != null)
				writeKey(key);
			sb.append('[');	
			writeSpaces(1);
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if (obj instanceof String) {
					writeValue("\"" + obj.toString() + "\"", i == list.size() - 1);
					writeSpaces(1);
				} else if (obj instanceof JsonObject) {
					sb.append('\n');
					writeSpaces(space + 4);
					writeObject(null, (JsonObject)obj, i == list.size() - 1, space + 4);
					sb.append('\n');
					writeSpaces(space);
				} else if (obj instanceof List<?>) {
					writeList(null, (List<?>)obj, i == list.size() - 1, space);
					writeSpaces(1);
				} else {
					writeValue(obj == null ? "null" : obj.toString(), i == list.size() - 1);
					writeSpaces(1);
				}
			}
			sb.append(']');
			if (!isMain) 
				sb.append(',').append(' ');		
		} catch (IOException e) {
		}
	}
	
	public void writeObject(JsonObject object) {
		writeObject(null, object, true, 0);
		write();
	}
	
	public void writeList(List<?> list) {
		writeList(null, list, true, 0);
		write();
	}
}
