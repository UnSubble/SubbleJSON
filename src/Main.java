import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Main {

	public static void main(String[] args) {
		SubbleJson json = new SubbleJson(Paths.get("users_100.json"));
			JsonBuilder builder = json.getBuilder();
			JsonParser parser = json.getParser();
			long start = System.nanoTime();
			Optional<List<?>> opt = parser.nextList(null);
			long end = System.nanoTime();
			System.out.println("------Reading------");
			System.out.println((end - start) + " nanoseconds");
			System.out.println(((end - start) / 1e6) + " milliseconds");
			System.out.println(((end - start) / 1e9) + " seconds");
			start = System.nanoTime();
			builder.clearFile();
			builder.writeList(opt.get());
			end = System.nanoTime();
			System.out.println();
			System.out.println("------Writing------");
			System.out.println((end - start) + " nanoseconds");
			System.out.println(((end - start) / 1e6) + " milliseconds");
			System.out.println(((end - start) / 1e9) + " seconds");
	}
}
