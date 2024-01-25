import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonBuilder builder = json.getBuilder();
			JsonParser parser = json.getParser();
			List<?> list = parser.nextList("projeler").get();
			builder.clearFile();
			builder.writeList(null, list);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
