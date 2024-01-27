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
			writer.append(c);
		}
		writer.append(':');
		writer.append(' ');
	}
	
	private void writeValue(String value) throws IOException {
		for (char c : value.toCharArray()) {
			writer.append(c);
		}
		writer.append(JsonUtil.COMMA);
	}
	
	private void writeSpaces(int space) throws IOException{
		for (int i = 0; i < space; i++) {
			writer.append(' ');
		}
	}
	
	private void writeString(String key, String value) {
		try {
			writeKey(key);
			writeValue(value);
		} catch (IOException e) {
		}
	}
	
	private void writeObject(String key, JsonObject object, boolean isMain, int space) {
		List<String> keys = object.getKeys();
		List<?> values = object.getValues();
		try {
			if (key != null)
				writeKey(key);	
			writer.append('{');
			writer.append('\n');
			space += 4;
			for (int i = 0; i < keys.size(); i++) {
				writeSpaces(space);
				String k = "\"" + keys.get(i) + "\"";
				Object v = values.get(i);
				if (v instanceof String) 
					writeString(k, "\"" + v.toString() + "\"");
				else if (v instanceof JsonObject) {
					writeObject(k, (JsonObject)v, false, space + 4);
				} else if (v instanceof List<?>)
					writeList(k, (List<?>)v, false, space);
				else
					writeString(k, v.toString());
				writer.append('\n');
			}
			writeSpaces(space - 4);
			writer.append('}');
			if (!isMain)
				writer.append(',');
		} catch (IOException e) {
		}
	}
	
	private void writeList(String key, List<?> list, boolean isMain, int space) {
		try {
			if (key != null)
				writeKey(key);
			writer.append('[');	
			writeSpaces(1);
			for (Object obj : list) {
				if (obj instanceof String) {
					writeValue("\"" + obj.toString() + "\"");
					writeSpaces(1);
				} else if (obj instanceof JsonObject) {
					writer.append('\n');
					writeSpaces(space + 4);
					writeObject(null, (JsonObject)obj, false, space + 4);
					writer.append('\n');
					writeSpaces(space);
				} else if (obj instanceof List<?>) {
					writeList(null, (List<?>)obj, false, space);
					writeSpaces(1);
				} else {
					writeValue(obj.toString());
					writeSpaces(1);
				}
			}
			writer.append(']');
			if (!isMain) {
				writer.append(',');
				writer.append(' ');
			}
		} catch (IOException e) {
		}
	}
	
	public void writeObject(JsonObject object) {
		writeObject(null, object, true, 0);
	}
	
	public void writeList(List<?> list) {
		writeList(null, list, true, 0);
	}
}
