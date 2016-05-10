import java.util.ArrayList;

//sorts doc based on link generated weights
public class LinkSorter {
	static Node start;
	
	LinkSorter(){
		start = null;
	}
	
	//adds a doc to the sorter
	void addDoc(String doc, Double rank){
		Node node = new Node(doc,rank);
		if(start ==  null){
			start = node;
		} else if(node.getRank() > start.getRank()){
			node.setNext(start);
			start = node;
		} else {
			place(node, start); 
		}
	}
	
	//places a node relative to its peers
	void place(Node node, Node current){
		if(node.getRank() > current.getRank() || node.getRank() == current.getRank()){
			node.setNext(current.getNext());
			current.setNext(node);
		} else if(current.getNext() == null){
			current.setNext(node);
		} else {
			place(node,current.getNext());
		}
	}
	
	//returns the strongest related documents in rank order
	ArrayList<Node> returnPaths(){
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node node = start;
		while(node != null){
			nodes.add(node);
			node = node.getNext();
		}
		return nodes;
	}
}
