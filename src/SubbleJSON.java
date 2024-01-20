import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class SubbleJSON implements Closeable {
	private File targetFile;
	private JSONParser parser;
	private JSONBuilder builder;

	public SubbleJSON(String path) {
		targetFile = new File(path);
		parser = JSONParser.getParser(targetFile);
		builder = JSONBuilder.getBuilder(targetFile);
	}
	
	public SubbleJSON(Path path) {
		targetFile = path.toFile();
	}
	
	public SubbleJSON(File file) {
		targetFile = file;
	}
	
	public JSONParser getParser() {
		return parser;
	}
	
	public JSONBuilder getBuilder() {
		return builder;
	}

	@Override
	public void close() throws IOException {
		parser.close();
	}
	
}