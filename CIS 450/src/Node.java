import java.util.ArrayList;

public class Node {
	String doc;
	Double value;
	ArrayList<String[]> path;
	Node next;
	
	Node(String doc, Double value, ArrayList<String[]> path){
		this.doc = doc;
		this.value = value;
		this.path = path;
		next = null;
	}
}
