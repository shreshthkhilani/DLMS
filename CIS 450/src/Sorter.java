import java.util.ArrayList;

public class Sorter {
	static Node start;
	
	Sorter(){
		start = null;
	}
	
	void addDoc(String doc, Double value, ArrayList<String[]> path){
		Node node = new Node(doc,value,path);
		if(start ==  null){
			start = node;
		} else if(node.rank > start.rank){
			node.next = start;
			start = node;
		} else {
			place(node, start); 
		}
	}
	
	void place(Node node, Node current){
		if(node.rank > current.rank || node.rank == current.rank){
			node.next = current.next;
			current.next = node;
		} else if(current.next == null){
			current.next = node;
		} else {
			place(node,current.next);
		}
	}
	
	ArrayList<Node> returnPaths(int num){
		ArrayList<Node> paths = new ArrayList<Node>();
		Node node = start;
		int i = 0;
		while(i < num && node != null){
			paths.add(node);
			node = node.next;
			i++;
		}
		return paths;
	}
	
	ArrayList<String[]> returnDoc(String doc){
		Node node = start;
		return traverse(doc,start);	
	}
	
	ArrayList<String[]> traverse(String doc, Node node){
		if(node.doc.equals(doc)){
			return node.path;
		} else {
			return traverse(doc, node.next);
		}	
	}
}
