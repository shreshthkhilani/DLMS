


public class Extractor {
	
	Extractor() {}
	
	public static void main(String args[]) throws Exception {
		AmazonDynamoDB.createTable();
		
	    if(args[0] == null)
	    {
	        System.out.println("Proper Usage is: java program filename");
	        System.exit(0);
	    }
	    
		//what type of class is it?
		XML x = new XML();
		
		x.parse(args[0]);
		
	}
	
}
