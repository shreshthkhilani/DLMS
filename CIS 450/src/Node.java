import java.util.ArrayList;

//creates a node that stores doc info
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
		path = null;
		next = null;
	}
	
	void setDoc(String doc){
		this.doc = doc;
	}
	
	void setRank(Double rank){
		this.rank = rank;
	}
	
	void setPath(ArrayList<String[]> path){
		this.path = path;
	}
	
	void setNext(Node next){
		this.next = next;
	}
	
	String getDoc(){
		return doc;
	}
	
	Double getRank(){
		return rank;
	}
	
	ArrayList<String[]> getPath(){
		return path;
	}
	
	Node getNext(){
		return next;
	}
}
