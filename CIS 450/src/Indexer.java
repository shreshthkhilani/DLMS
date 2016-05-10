import java.util.List;

//basic indexer interaction
public class Indexer {
	Indexer(){}
	
	static void add(String key, String value){
		AmazonDynamoDB.newPair(key, value);
		AmazonDynamoDB.newReversePair(key, value);
	}
	
	static List<String> getIndex(String key){
		return AmazonDynamoDB.values(key);
	}
	
	static List<String> getReverseIndex(String key){
		return AmazonDynamoDB.reverseValues(key);
	}
}
