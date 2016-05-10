
public class ConstructTree {
	TreeNode head;
	
	ConstructTree(){
		head = null;
	}
	
	boolean addNode(String[] pair){
		TreeNode key = new TreeNode(pair[1]);
		TreeNode value = new TreeNode(pair[0]);
		if(head == null){
			key.addChild(value);
			head = key;
			return true;
		} else if (value.key.equals(head.key)){
			key.addChild(head);
			head = key;
			return true;
		} else {
			key.addChild(value);
			return add(key, head);
		}
	}
	
	boolean add(TreeNode node,TreeNode current){
		if(current.key.equals(node.key)){
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
