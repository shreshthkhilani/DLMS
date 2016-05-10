import java.util.List;

//basic indexer interaction
public class Indexer {
	Indexer(){}
	
	static void add(String key, String value){
		AmazonDynamoDB.newPair(key.toLowerCase(), value.toLowerCase());
		AmazonDynamoDB.newReversePair(key.toLowerCase(), value.toLowerCase());
	}
	
	static List<String> getIndex(String key){
		return AmazonDynamoDB.values(key);
	}
	
	static List<String> getReverseIndex(String key){
		return AmazonDynamoDB.reverseValues(key);
	}
}
