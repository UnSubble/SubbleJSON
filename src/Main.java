import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

public class Main {

	public static void main(String[] args) {
		File file = new File("users_100k.json");
		try (SubbleJson json = new SubbleJson(file, Charset.forName("UTF-8"))) {
			JsonBuilder builder = json.getBuilder();
			JsonParser parser = json.getParser();
			long start = System.nanoTime();
			Optional<List<?>> num = null;
			while (!(num = parser.nextList("hobbies")).isEmpty()) {
				System.out.println(num.get());
			}
			long end = System.nanoTime();
			System.out.println("------Reading------");
			System.out.println((end - start) + " nanoseconds");
			System.out.println(((end - start) / 1e6) + " milliseconds");
			System.out.println(((end - start) / 1e9) + " seconds");
			//builder.clearFile();
			start = System.nanoTime();
			//builder.writeList(obj);
			end = System.nanoTime();
			System.out.println();
			System.out.println("------Writing------");
			System.out.println((end - start) + " nanoseconds");
			System.out.println(((end - start) / 1e6) + " milliseconds");
			System.out.println(((end - start) / 1e9) + " seconds");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
