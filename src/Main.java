import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJSON json = new SubbleJSON(file)) {
			JSONParser parser = json.getParser();
			
			JSONObject object = parser.getAsJSONObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
