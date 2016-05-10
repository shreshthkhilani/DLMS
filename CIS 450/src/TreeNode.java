import java.util.ArrayList;

//Creates a node that can be used to construct trees
public class TreeNode {
	String key;
	ArrayList<TreeNode> children;
	Integer id;
	
	TreeNode(String key){
		this.key = key;
		children = new ArrayList<TreeNode>();
		this.id = null;
	}
	
	void addChild(TreeNode value){
		children.add(value);
	}
	
	ArrayList<TreeNode> getChildren(){
		return children;
	}
	
	void setId(int id){
		this.id = id;
	}
	
	Integer getId(){
		return id;
	}
	String getkey(){
		return key;
	}
}
