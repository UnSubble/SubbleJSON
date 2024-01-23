import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonParser parser = json.getParser();
			JsonObject obj = parser.nextObject("init-param").get();
			obj.getValues().forEach(System.out::println);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
