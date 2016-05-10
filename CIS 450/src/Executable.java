import java.io.FileWriter;

import javax.json.JsonObject;

public class Executable {
	//runs the search engine
	public static void main(String[] args) throws Exception {
		AmazonDynamoDB.createTable();
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
			try (FileWriter file = new FileWriter("query.json")) {
				file.write(obj.toString());
				System.out.println("Successfully Copied JSON Object to File...");
				System.out.println("\nJSON Object: " + obj);
			}
		}
	}
}
