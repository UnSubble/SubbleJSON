import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

class JsonBuilder {
	private BufferedWriter writer;
	
	private JsonBuilder(File file) {
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
	
	public void writeString() {
		
	}
}
