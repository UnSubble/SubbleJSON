import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonParser parser = json.getParser();
			System.out.println(parser.nextObject("objectVal").get());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
