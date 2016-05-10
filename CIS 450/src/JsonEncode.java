import java.util.ArrayList;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class JsonEncode {
	static Integer yposition;
	static Integer edge;
	static Integer id;
	
	JsonEncode(){}
	
	public static JsonObject encode(ArrayList<ArrayList<String[]>> results){
		ArrayList<JsonObject> nodeObj = new ArrayList<JsonObject>();
		ArrayList<JsonObject> edgeObj = new ArrayList<JsonObject>();
		
		yposition = 0;
		edge = 0;
		id = 0;
		for(ArrayList<String[]> tree: results){
			ConstructTree con = new ConstructTree();
			while(!tree.isEmpty()){
				String[] node = tree.remove(0);
				if(!con.addNode(node)){
					tree.add(node);
				}
			}
		nodeObj.addAll(nodeToJson(con.getHead(),0));
		edgeObj.addAll(edgeToJson(con.getHead()));
		}
		
		JsonArrayBuilder jarr = Json.createArrayBuilder();
		for(JsonObject jsonNode: nodeObj){
			jarr.add(jsonNode);
		}
		
		JsonArrayBuilder jarr2 = Json.createArrayBuilder();
		for(JsonObject edgeNode: edgeObj){
			jarr2.add(edgeNode);
		}
		JsonObject obj = Json.createObjectBuilder()
				.add("nodes", jarr)
				.add("edges", jarr2)
				.build();

	    return obj;
	}
	
	static ArrayList<JsonObject> nodeToJson(TreeNode node, int xposition){
		ArrayList<JsonObject> ans = new ArrayList<JsonObject>();
		JsonObject obj = Json.createObjectBuilder()
				.add("id", "n"+Integer.toString(id))
				.add("label", node.getkey())
				.add("x", xposition)
				.add("y", yposition)
				.add("size", 1)
				.build();
		node.setId(id);
		id++;
		ans.add(obj);
		yposition++;
		xposition = 0;
		for(TreeNode child : node.getChildren()){
			ans.addAll(nodeToJson(child,xposition));
			xposition += child.getChildren().size();
		}		
		return ans;
	}
	
	static ArrayList<JsonObject> edgeToJson(TreeNode node){
		ArrayList<JsonObject> ans = new ArrayList<JsonObject>();
		for(TreeNode child : node.getChildren()){
			JsonObject obj = Json.createObjectBuilder()
					.add("id", "e"+Integer.toString(edge))
					.add("source", "n"+node.getId())
					.add("target", "n"+child.getId())
					.build();
			ans.add(obj);
			edge++;
			ans.addAll(edgeToJson(child));
		}
		return ans;
	}
}
