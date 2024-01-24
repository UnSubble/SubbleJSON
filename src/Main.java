import java.io.File;
import java.io.IOException;
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
			Optional<List<?>> op = parser.nextList("options");
			while (!op.isEmpty()) {
				System.out.println(op.get());
				op = parser.nextList("options");		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
