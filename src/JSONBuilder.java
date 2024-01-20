import java.io.File;

class JSONBuilder {
	private File file;
	
	private JSONBuilder(File file) {
		this.file = file;
	}
	
	protected static JSONBuilder getBuilder(File file) {
		return new JSONBuilder(file);
	}
}
