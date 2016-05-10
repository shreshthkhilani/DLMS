
public class ConstructTree {
	TreeNode head;
	
	ConstructTree(){
		head = null;
	}
	
	boolean addNode(String[] pair){	
		TreeNode key = new TreeNode(pair[0]);
		TreeNode value = new TreeNode(pair[1]);
		key.addChild(value);
		if(head == null){
			head = key;
			return true;
		} else {
			return add(key, head);
		}
	}
	
	boolean add(TreeNode node,TreeNode current){
		if(current.key == node.key){
			current.children.add(node.getChildren().get(0));
			return true;
		} else {
			for(TreeNode child: current.getChildren()){
				if(add(node,child)){
					return true;
				}
			}
		}
		return false;
	}
	
	TreeNode getHead(){
		return head;
	}
}
