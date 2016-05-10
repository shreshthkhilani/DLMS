import java.util.ArrayList;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class JsonEncode {
	static Integer yposition;
	static Integer edge;
	
	JsonEncode(){}
	
	public static JsonObject encode(ArrayList<ArrayList<String[]>> results){
		ArrayList<JsonObject> nodeObj = new ArrayList<JsonObject>();
		ArrayList<JsonObject> edgeObj = new ArrayList<JsonObject>();
		
		for(ArrayList<String[]> tree: results){
			ConstructTree con = new ConstructTree();
			Iterator<String[]> nodes = tree.iterator();
			while(nodes.hasNext()){
				String[] node = nodes.next();
				con.addNode(node);
			}
		yposition = 0;
		edge = 0;
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
				.add("id", node.getkey())
				.add("label", node.getkey())
				.add("x", xposition)
				.add("y", yposition)
				.add("size", 1)
				.build();
		ans.add(obj);
		xposition = 0;
		yposition++;
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
					.add("id", edge)
					.add("source", node.key)
					.add("target", child.key)
					.build();
			ans.add(obj);
			edge++;
		}
		return ans;
	}
}
