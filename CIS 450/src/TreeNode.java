import java.util.ArrayList;

public class TreeNode {
	String key;
	ArrayList<TreeNode> children;
	
	TreeNode(String key){
		this.key = key;
		children = new ArrayList<TreeNode>();
	}
	
	void addChild(TreeNode value){
		children.add(value);
	}
	
	ArrayList<TreeNode> getChildren(){
		return children;
	}
	
	String getkey(){
		return key;
	}
}
