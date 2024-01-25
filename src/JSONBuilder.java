import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

class JsonBuilder {
	private File file;
	private BufferedWriter writer;
	
	private JsonBuilder(File file) {
		this.file = file;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected static JsonBuilder getBuilder(File file) {
		return new JsonBuilder(file);
	}
	
	protected void close() throws IOException {
		writer.close();
	}
	
	public void clearFile() {
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.write("");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void writeKey(String key) throws IOException {
		for (char c : key.toCharArray()) {
			writer.write(c);
		}
		writer.write(':');
		writer.write(' ');
	}
	
	private void writeValue(String value) throws IOException {
		for (char c : value.toCharArray()) {
			writer.write(c);
		}
		writer.write(JsonUtil.COMMA);
	}
	
	public void writeString(String key, String value) {
		try {
			writeKey(key);
			writeValue(value);
		} catch (IOException e) {
		}
	}
	
	public void writeObject(String key, JsonObject object) {
		List<String> keys = object.getKeys();
		List<?> values = object.getValues();
		try {
			if (key != null)
				writeKey(key);
			writer.append('{');
			for (int i = 0; i < keys.size(); i++) {
				String k = keys.get(i);
				Object v = values.get(i);
				if (v instanceof String) 
					writeString(k, "\"" + v.toString() + "\"");
				else
					writeString(k, v.toString());
			}
			writer.append('}');
			writer.append(',');
		} catch (IOException e) {
		}
	}
	
	public void writeList(String key, List<?> list) {
		try {
			if (key != null)
				writeKey(key);
			writer.write('[');
			for (Object obj : list) {
				if (obj instanceof String)
					writeValue("\"" + obj.toString() + "\"");
				else if (obj instanceof JsonObject)
					writeObject(null, (JsonObject)obj);
				else if (obj instanceof List<?>)
					writeList(null, (List<?>)obj);
				else
					writeValue(obj.toString());
			}
			writer.write(']');
			writer.write(',');
		} catch (IOException e) {
		}
	}
}
