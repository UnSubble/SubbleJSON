import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class SubbleJson implements Closeable {
	private File targetFile;
	private JsonParser parser;
	private JsonBuilder builder;

	public SubbleJson(String path) {
		this(new File(path));
	}
	
	public SubbleJson(Path path) {
		this(path.toFile());
	}
	
	public SubbleJson(File file) {
		targetFile = file;
		parser = JsonParser.getParser(targetFile);
		builder = JsonBuilder.getBuilder(targetFile);
	}
	
	public JsonParser getParser() {
		return parser;
	}
	
	public JsonBuilder getBuilder() {
		return builder;
	}

	@Override
	public void close() throws IOException {
		parser.close();
	}
	
}