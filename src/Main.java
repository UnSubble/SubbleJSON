import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		File file = new File("test.json");
		try (SubbleJson json = new SubbleJson(file)) {
			JsonBuilder builder = json.getBuilder();
			JsonParser parser = json.getParser();
			long start = System.nanoTime();
			List<?> obj = parser.nextList(null).get();
			long end = System.nanoTime();
			System.out.println("------Reading------");
			System.out.println((end - start) + " nanoseconds");
			System.out.println(((end - start) / 1e6) + " milliseconds");
			System.out.println(((end - start) / 1e9) + " seconds");
			builder.clearFile();
			start = System.nanoTime();
			builder.writeList(obj);
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
