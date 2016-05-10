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
		} else if(node.value > start.value){
			node.next = start;
			start = node;
		} else {
			place(node, start); 
		}
	}
	
	void place(Node node, Node current){
		if(node.value > current.value || node.value == current.value){
			node.next = current.next;
			current.next = node;
		} else if(current.next == null){
			current.next = node;
		} else {
			place(node,current.next);
		}
	}
	
	ArrayList<ArrayList<String[]>> returnPaths(int num){
		ArrayList<ArrayList<String[]>> paths = new ArrayList<ArrayList<String[]>>();
		Node node = start;
		int i = 0;
		while(i < num && node != null){
			paths.add(node.path);
			node = node.next;
			i++;
		}
		return paths;
	}
}
