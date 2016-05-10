import java.util.ArrayList;

public class Node {
	String doc;
	Double rank;
	ArrayList<String[]> path;
	Node next;
	
	Node(String doc, Double rank, ArrayList<String[]> path){
		this.doc = doc;
		this.rank = rank;
		this.path = path;
		next = null;
	}
	
	Node(String doc, Double rank){
		this.doc = doc;
		this.rank = rank;
		this.path = null;
		next = null;
	}
}
