
public class Test {
	public static void main(String[] args) throws Exception {
		AmazonDynamoDB.createTable();
		//Indexer.add("test","1");
		//Indexer.add("test","2");
		//Indexer.add("test","3");
		//Indexer.add("test","test2");
		//Indexer.add("test2","2");
		//Indexer.add("different","3");
		//Indexer.add("different","1");
		//Indexer.add("test","test3");
		//Indexer.add("test3","1");
		//.add("outlier","3");
		//	Indexer.add("outlier2","1");
		//System.out.println(Indexer.getIndex("test"));
		//System.out.println(Indexer.getIndex("different"));
		//System.out.println(Indexer.getReverseIndex("1"));
		//System.out.println(Indexer.getReverseIndex("2"));
		System.out.println(SearchEngine.search("1 2"));
	}
}
