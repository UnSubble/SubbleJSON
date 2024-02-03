import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SubbleJson {
	private JsonParser parser;
	private JsonBuilder builder;
	
	public SubbleJson(String path) {
		this(Paths.get(path), Charset.defaultCharset());
	}
	
	public SubbleJson(Path path) {
		this(path, Charset.defaultCharset());
	}

	public SubbleJson(String path, Charset charset) {
		this(Paths.get(path), charset);
	}
	
	public SubbleJson(Path path, Charset charset) {
		parser = JsonParser.getParser(path, charset);
		builder = JsonBuilder.getBuilder(path, charset);
	}
	
	public JsonParser getParser() {
		return parser;
	}
	
	public JsonBuilder getBuilder() {
		return builder;
	}
	
}