import java.io.File;

class JsonBuilder {
	private File file;
	
	private JsonBuilder(File file) {
		this.file = file;
	}
	
	protected static JsonBuilder getBuilder(File file) {
		return new JsonBuilder(file);
	}
}
