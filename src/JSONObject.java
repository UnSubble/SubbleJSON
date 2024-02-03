import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonObject {
	private Map<String, List<JsonPair>> map;
	
	public JsonObject() {
		map = new LinkedHashMap<>();
	}
	
	public void add(String key, Object val) {
		Objects.requireNonNull(key);
		List<JsonPair> pairs = map.computeIfAbsent(key, k -> new ArrayList<>());
		pairs.add(new JsonPair(key, val));
	}
	
	public void addAll(Collection<JsonPair> collection) {
		for (JsonPair pair : collection) {
			add(pair.getKey(), pair.getValue());
		}
	}
	
	public List<String> getKeys() {
		return map.values().stream()
	            .flatMap(Collection::stream)
	            .map(JsonPair::getKey)
	            .collect(Collectors.toList());
	}
	
	public List<?> getAll(String key) {
		Objects.requireNonNull(key);
	    return map.getOrDefault(key, Collections.emptyList())
	            .stream()
	            .map(JsonPair::getValue)
	            .collect(Collectors.toList());
	}
	
	public List<?> getValues() {
		return map.values().stream()
	            .flatMap(Collection::stream)
	            .map(JsonPair::getValue)
	            .collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		Iterator<String> keyIter = map.keySet().iterator();
		while (keyIter.hasNext()) {
			List<JsonPair> pairs = map.get(keyIter.next());
			Iterator<JsonPair> pairIter = pairs.iterator();
			while (pairIter.hasNext()) {				
				JsonPair pair = pairIter.next();
				Object value = pair.getValue();
				sb.append(pair.getKey()).append("=").append(value == null ? "null" : value.toString());
				if (pairIter.hasNext())
					sb.append(", ");
			}
			if (keyIter.hasNext())
				sb.append(", ");
		}
		sb.append('}');
		return sb.toString();
	}
	
	public static class JsonPair {
		private String key;
		private Object value;
		
		public JsonPair(String key, Object value) {
			this.key = Objects.requireNonNull(key);
			this.value = Objects.requireNonNull(value);
		}
		
		public String getKey() {
			return key;
		}
		
		public Object getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JsonPair other = (JsonPair) obj;
			return key.equals(other.key) && value.equals(other.value);
		}

		@Override
		public String toString() {
			return "[key=" + key + ",value=" + value + "]";
		}
		
	}
}
