import java.util.Map;

public class Linker {

	Linker(){}
	
	static void add(String key, String value) {
		AmazonDynamoDB.newLink(key, value);
	}
	
	static Map<String, Double> search(String key, String value) {
		return AmazonDynamoDB.linkQuery(key, value);
	}
}
