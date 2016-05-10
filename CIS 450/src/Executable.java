import java.io.FileWriter;

import javax.json.JsonObject;

public class Executable {
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("invalid number of arguments");
		} else {
			AmazonDynamoDB.createTable();
			SearchEngine engine = new SearchEngine(args[0]);
			JsonObject obj = JsonEncode.encode(engine.search(args[1]));
			try (FileWriter file = new FileWriter("querry.json")) {
				file.write(obj.toString());
				System.out.println("Successfully Copied JSON Object to File...");
				System.out.println("\nJSON Object: " + obj);
			}
		}
	}
}
