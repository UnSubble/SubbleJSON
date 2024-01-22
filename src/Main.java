import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		SubbleJson json = new SubbleJson(file);
		JsonParser parser = json.getParser();
		System.out.println(parser.getString("tbag").get());
		try {
			json.close();
		} catch (IOException e) {
		}
	}
}
