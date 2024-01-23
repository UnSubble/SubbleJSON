import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonObject {
	private String name;
	private Map<String, LinkedList<JsonPair<String, ?>>> map;
	
	public JsonObject() {
		map = new LinkedHashMap<>();
	}
	
	public void add(String key, Object val) {
		List<JsonPair<String, ?>> pairs = map.computeIfAbsent(key, k -> new LinkedList<>());
		pairs.add(new JsonPair<>(key, val));
	}
	
	public List<String> getKeys() {
		List<String> keyList = new ArrayList<>();
		for (LinkedList<JsonPair<String, ?>> pairs : map.values()) {
			for (Iterator<JsonPair<String, ?>> pairIter = pairs.iterator(); pairIter.hasNext();) {
				keyList.add(pairIter.next().getKey());
			}
		}
		return keyList;
	}
	
	public List<?> getAll(String key) {
		List<Object> values = new ArrayList<>();
		List<JsonPair<String, ?>> pairs = map.get(key);
		for (Iterator<JsonPair<String, ?>> pairIter = pairs.iterator(); pairIter.hasNext();) {
			values.add(pairIter.next().getValue());
		}	
		return values; 
	}
	
	public List<?> getValues() {
		List<Object> valueList = new ArrayList<>();
		for (LinkedList<JsonPair<String, ?>> pairs : map.values()) {
			for (Iterator<JsonPair<String, ?>> pairIter = pairs.iterator(); pairIter.hasNext();) {
				valueList.add(pairIter.next().getValue());
			}
		}
		return valueList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name + " = {");
		for (LinkedList<JsonPair<String, ?>> pairs : map.values()) {
			for (Iterator<JsonPair<String, ?>> pairIter = pairs.iterator(); pairIter.hasNext();) {
				JsonPair<String, ?> pair = pairIter.next();
				sb.append(pair.getKey() + ": " + pair.getValue().toString());
			}
		}
		sb.append("\n}");
		return sb.toString();
	}
	
	public static class JsonPair<K,V> {
		private K key;
		private V value;
		
		public JsonPair(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		public K getKey() {
			return key;
		}
		
		public V getValue() {
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
			JsonPair<?, ?> other = (JsonPair<?,?>) obj;
			return key.equals(other.key) && value.equals(other.value);
		}

		@Override
		public String toString() {
			return "[key: " + key + ", value: " + value + "]";
		}
		
	}
}
