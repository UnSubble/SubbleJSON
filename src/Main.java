import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		File file = new File("users_10k.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonBuilder builder = json.getBuilder();
			JsonParser parser = json.getParser();
			List<?> obj = parser.nextList(null).get();
			builder.clearFile();
			builder.writeList(obj );
			System.out.println(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
