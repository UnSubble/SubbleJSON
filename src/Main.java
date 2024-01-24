import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonParser parser = json.getParser();
			//List<?> s = parser.nextList("servlet").get();
			//System.out.println(s);
			//System.out.println(parser.nextObject("init-param").get());
			System.out.println(parser.nextObject("testt"));
			/*Optional<List<?>> op = parser.nextList("test");
			while (!op.isEmpty()) {
				List<?> s = op.get();
				for (Object obj : s) {
					if(obj instanceof JsonObject)
						System.out.println(((JsonObject)obj).getValues());
					if (obj instanceof String)
						System.out.println(obj);
				}
				op = parser.nextList("test");		
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
