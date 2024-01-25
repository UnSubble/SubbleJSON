import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonBuilder builder = json.getBuilder();
			JsonParser parser = json.getParser();
			JsonObject obj = parser.nextObject(null).get();
			builder.clearFile();
			builder.writeObject(obj);
			System.out.println(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
