import java.io.FileWriter;

import javax.json.JsonObject;

public class Executable {
	//runs the search engine
	public static void main(String[] args) throws Exception {
		AmazonDynamoDB.createTable();
//		Indexer.add("X1bzpmQhb", "head1");
//		Indexer.add("X1bzpmQhb", "Penn");
//		Indexer.add("head1", "Graduation");
//		Indexer.add("head1", "elem1");
//		Indexer.add("elem1", "head1");
//		Indexer.add("XJ_LPBzh-", "list1");
//		Indexer.add("list1", "Penn");
//		Indexer.add("list1", "University");
//		Indexer.add("list1", "Gala");
//		Indexer.add("71Zi0U7hb", "header1");
//		Indexer.add("header1", "Penn");
//		Indexer.add("71Zi0U7hb", "body1");
//		Indexer.add("body1", "Gala");
//		Indexer.add("body1", "is");
//		Indexer.add("body1", "here");
		//takes in user and query args
		if(args.length != 2){
			System.out.println("invalid number of arguments");
		} else {
			//initializes DynamoDB connection
			AmazonDynamoDB.createTable();
			//creates search engine
			SearchEngine engine = new SearchEngine(args[0]);
			//encodes search engine results as jason file
			JsonObject obj = JsonEncode.encode(engine.search(args[1]));
			try (FileWriter file = new FileWriter("querry.json")) {
				file.write(obj.toString());
				System.out.println("Successfully Copied JSON Object to File...");
				System.out.println("\nJSON Object: " + obj);
			}
		}
	}
}
