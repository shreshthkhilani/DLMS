import java.util.ArrayList;

//Sorts documents based on index generated weights
public class IndexSorter {
	static Node start;
	
	IndexSorter(){
		start = null;
	}
	
	//adds a document to the sorter
	void addDoc(String doc, Double value, ArrayList<String[]> path){
		Node node = new Node(doc,value,path);
		if(start ==  null){
			start = node;
		} else if(node.getRank() > start.getRank()){
			node.setNext(start);
			start = node;
		} else {
			place(node, start); 
		}
	}
	
	//places the node relative to its peers 
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
	
	//returns k best docs in rank order
	ArrayList<Node> returnPaths(int k){
		ArrayList<Node> paths = new ArrayList<Node>();
		Node node = start;
		int i = 0;
		while(i < k && node != null){
			paths.add(node);
			node = node.getNext();
			i++;
		}
		return paths;
	}
	
	//returns the paths associated ith a given doc
	ArrayList<String[]> returnDoc(String doc){
		return traverse(doc,start);	
	}
	
	//searches for a given doc in the sorter
	ArrayList<String[]> traverse(String doc, Node node){
		if(node.getDoc().equals(doc)){
			return node.getPath();
		} else {
			return traverse(doc, node.getNext());
		}	
	}
}
