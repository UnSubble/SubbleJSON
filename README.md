# SubbleJson Library

SubbleJson is a simple Java library for handling JSON files, providing functionalities for both reading and writing JSON data.

## Features

- **JsonParser**: Allows parsing JSON files to extract specific values using a fluent API.
- **JsonBuilder**: Provides the ability to create or modify JSON files using a builder pattern.
- **JsonObject**: Represents a JSON object and supports key-value pairs with multiple values for the same key.

## Getting Started

To use SubbleJson, follow these steps:

1. **Include SubbleJson in your project**: Add the SubbleJson JAR file to your Java project.
    
2. **Create SubbleJson instance**:
    
```
	File file = new File("example.json");
	try (SubbleJson json = new SubbleJson(file, Charset.forName("UTF-8"))) {
		JsonBuilder builder = json.getBuilder();     
		JsonParser parser = json.getParser();     
		// Your code here... 
	} catch (IOException e) {     
		e.printStackTrace(); 
    }
```

3. **Read from JSON**:

```
	Optional<List<?>> list = parser.nextList("hobbies")).get(); 
	System.out.println(list); 
```
    
4. **Write to JSON**:
    
    ```
    // Your JSON object or list 
    List<?> obj = /* your data */; 
    builder.writeList(obj);
    ```
    
5. **Close SubbleJson instance**:
    
    Ensure to close the `SubbleJson` instance to release resources:
    
    ```
    try (SubbleJson json = new SubbleJson(file, Charset.forName("UTF-8"))) {     
	    // Your code here... 
    } catch (IOException e) {     
	    e.printStackTrace(); 
    }
    ```
    

## Performance

The library provides efficient JSON parsing and building capabilities, making it suitable for handling large JSON files.

## Example Usage

Check the `Main` class for an example of how to use the SubbleJson library to read and write JSON data efficiently.

## License

SubbleJson is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
