import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchEngine {
	static final int scale = 6;
	static final int constant = 3;
	static String user;
	static String querry;
	
	//initializes a search engine for a specific user
	SearchEngine(String user){
		this.user = user;
	}
	
	//parses the query, passes individual word paths to a merge, and passes doc trees into the ranker
	ArrayList<ArrayList<String[]>> search(String querry){
		HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> master = new HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>>();
		this.querry = querry.toLowerCase();
		//split query
		String[] tokens = this.querry.split(" ");
		for(String token: tokens){
			//find root paths for each search token
			HashMap<String, ArrayList<HashMap<String, String>>> paths = rootPaths(token);
			//merge root paths between tokens to create query paths
			master = fold(master, paths);
		}
		//rank query paths
		return rank(master);
	}
	
	//pulls all the root paths associated with the given keyword
	static HashMap<String, ArrayList<HashMap<String, String>>> rootPaths(String value){
		HashMap<String, ArrayList<HashMap<String, String>>> ans = new HashMap<String, ArrayList<HashMap<String,String>>>();
		List<String> keys = Indexer.getReverseIndex(value);
		List<String> empty = new ArrayList<String>();
		if(keys.equals(empty)){
			ans.put(value,new ArrayList<HashMap<String, String>>());
		} else {
			HashMap<String,HashMap<String, ArrayList<HashMap<String, String>>>> cumm = new HashMap<String,HashMap<String, ArrayList<HashMap<String, String>>>>();
			for(String key: keys){
				HashMap<String, ArrayList<HashMap<String, String>>> partial = rootPaths(key);
				cumm.put(key,partial);
			}
			Iterator<String> iter = cumm.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				HashMap<String, ArrayList<HashMap<String, String>>> docs = cumm.get(key);

				Iterator<String> iter2 = docs.keySet().iterator();
				while(iter2.hasNext()){
					String doc = iter2.next();
					//only search doc if user has permission to access it
					if(AmazonDynamoDB.returnPermission(user,doc)){
						ArrayList<HashMap<String, String>> paths = docs.get(doc);
						if(paths.equals(empty)){
							paths = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> path = new HashMap<String, String>();
							paths.add(path);
							path.put(value,key);
						} else {
							for(HashMap<String, String> path: paths){
								path.put(value,key);
							}	
						}
						ArrayList<HashMap<String, String>> update = ans.get(doc);
						if(update == null){
							update = new ArrayList<HashMap<String, String>>();
						}
						update.addAll(paths); 
						ans.put(doc, update);
					}
				}
			}
		}
		return ans;		
	}
	
	//find all query paths by folding root paths on to each other
	static HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> fold(HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> master, HashMap<String, ArrayList<HashMap<String, String>>> paths){
		Set<Integer> set = master.keySet();
		Iterator<Integer> iter = set.iterator();
		//successful folds increases search tier (denoted by the number of unique query tokens in the path)
		while(iter.hasNext()){
			Integer num = iter.next();
			HashMap<String, ArrayList<ArrayList<String[]>>> established = master.get(num);
			HashMap<String, ArrayList<ArrayList<String[]>>> clone = (HashMap<String, ArrayList<ArrayList<String[]>>>) established.clone();
			Iterator<String> iter2 = clone.keySet().iterator();
			while(iter2.hasNext()){
				String doc= iter2.next();
				if(paths.containsKey(doc)){
					ArrayList<ArrayList<String[]>> merged = mergePaths(num, established.get(doc),paths.get(doc));
					if(merged != null){
						established.remove(doc);
						master.put(num, established);
						HashMap<String, ArrayList<ArrayList<String[]>>> reformed = master.get(num+1);
						if(reformed == null){
							reformed = new HashMap<String, ArrayList<ArrayList<String[]>>>();
						}
						reformed.put(doc, merged);
						master.put(num+1, reformed);
					}
					paths.remove(doc);
				}
			}
		}
		//for the root paths that could not be folded add them to the lowest search accuracy tier
		if(!paths.isEmpty()){
			Iterator<String> iter3 = paths.keySet().iterator();
			HashMap<String, ArrayList<ArrayList<String[]>>> recent = master.get(1);
			while(iter3.hasNext()){
				String doc = iter3.next();
				ArrayList<HashMap<String, String>> channels = paths.get(doc);
				ArrayList<ArrayList<String[]>> channels2;
				if(recent == null){
					recent = new HashMap<String, ArrayList<ArrayList<String[]>>>();
					channels2 = new ArrayList<ArrayList<String[]>>();
				} else {
					channels2 = recent.get(doc);
					if(channels2 == null){
						channels2 = new ArrayList<ArrayList<String[]>>();
					}
				}
				Iterator<HashMap<String, String>> iter4 = channels.iterator();
				while(iter4.hasNext()){
					ArrayList<String[]> temp = new ArrayList<String[]>();
					HashMap<String, String> map = iter4.next();
					Iterator<String> iter5 = map.keySet().iterator();
					while(iter5.hasNext()){
						String key = iter5.next();
						String[] pair = new String[2];
						pair[0] = key;
						pair[1] = map.get(key);
						temp.add(pair);
					}
					if(temp != null){
						channels2.add(temp);
					}
				}
				recent.put(doc, channels2);
			}
			master.put(1, recent);
		}
		return master;
	}
	
	//setups up merge structure for combine methods
	static ArrayList<ArrayList<String[]>> mergePaths(Integer num, ArrayList<ArrayList<String[]>> master, ArrayList<HashMap<String, String>> paths){
		ArrayList<ArrayList<String[]>> new_master = new ArrayList<ArrayList<String[]>>();
		while(!master.isEmpty()){
			ArrayList<String[]> old_path = master.remove(0);
			for(HashMap<String, String> new_path : paths){
				ArrayList<String[]> combined = mapCombine(num, (ArrayList<String[]>) old_path.clone(),new_path);
				if(combined != null){
					new_master.add(combined);
				}
			}
		}
		return new_master;
	}
	
	//merges root paths to get query paths (represented as an array and a map)
	static ArrayList<String[]> mapCombine(Integer num, ArrayList<String[]> old_path, HashMap<String, String> new_path){
		//max path length scales with the number of uniue query tokens present in path
		Integer length = (num+1)*scale;
		Iterator<String> keys = new_path.keySet().iterator();
		Integer size = old_path.size() + new_path.size();
		while(keys.hasNext()){
			String key = keys.next();
			String[] entry = new String[2];
			entry[0] = key;
			entry[1] = new_path.get(key);
			boolean flag = true;
			for(String[] pair: old_path){
				if(Arrays.equals(pair, entry)){
					size = size - 2;
					flag = false;
				}
			}
			if(flag){
				old_path.add(entry);
			}
		}
		//if path size is larger than max allowed path length drop the path
		if(size > length){
			return null;
		} else {
			return old_path;
		}
	}
	
	//merges query paths to get trees (represented as an array and an array)
	static ArrayList<String[]> arrayCombine(ArrayList<String[]> old_path, ArrayList<String[]> new_path){
		Iterator<String[]> keys = new_path.iterator();
		while(keys.hasNext()){
			String[] entry = keys.next();
			boolean flag = true;
			for(String[] pair: old_path){
				if(Arrays.equals(pair, entry)){
					flag = false;
				}
			}
			if(flag){
				old_path.add(entry);
			}
		}
		return old_path;
	}
	
	//creates index ranks to be used in sort
	static ArrayList<ArrayList<String[]>> rank(HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> master){
		HashMap<String, Double> rank = new HashMap<String,Double>();
		HashMap<String, ArrayList<String[]>> merged = new HashMap<String, ArrayList<String[]>>();
		Iterator<Integer> levels = master.keySet().iterator();
		while(levels.hasNext()){
			int strength = levels.next();
			HashMap<String, ArrayList<ArrayList<String[]>>> docs = master.get(strength);
			Iterator<String> iter = docs.keySet().iterator();
			while(iter.hasNext()){
				String doc = iter.next();
				ArrayList<ArrayList<String[]>> paths = docs.get(doc);
				ArrayList<String[]> old_path = new ArrayList<String[]>();
				for(ArrayList<String[]> path: paths){
					arrayCombine(old_path,path);
					if(rank.containsKey(doc)){
						Double prev = rank.get(doc);
						rank.put(doc, prev + Math.pow(constant,strength)/(double) path.size());
					} else {
						rank.put(doc, Math.pow(constant,strength)/(double) path.size());
					}
				}
				merged.put(doc,old_path);
			}
		}
		return sort(rank,merged);	
	}
	
	//sorts the documents based on index ranks and introduces link ranks to connect documents
	static ArrayList<ArrayList<String[]>> sort(HashMap<String, Double> rank, HashMap<String, ArrayList<String[]>> merged){
		ArrayList<ArrayList<String[]>> search = new ArrayList<ArrayList<String[]>>();
		Set<String> seen = new HashSet<String>();
		IndexSorter sorter = new IndexSorter();
		Iterator<String> docs = rank.keySet().iterator(); 
		while(docs.hasNext()){
			String doc = docs.next();
			sorter.addDoc(doc,rank.get(doc),merged.get(doc));
		}
		Node start = sorter.returnPaths(1).get(0);
		search.add(start.getPath());
		String current = start.getDoc();
		seen.add(current);
		
		boolean endSearch = false;
		while(!endSearch){
			Map<String,Double> near = AmazonDynamoDB.linkQuery(querry,current);
			LinkSorter sorter2 = new LinkSorter();
			Iterator<String> documents = near.keySet().iterator(); 
			while(documents.hasNext()){
				String doc = documents.next();
				sorter2.addDoc(doc,near.get(doc));
			}
			Iterator<Node> ranking = sorter2.returnPaths().iterator();
			endSearch = true;
			//determines hich document to hop to next
			while(endSearch && ranking.hasNext()){
				Node next = ranking.next();
				if(!seen.contains(next.getDoc())){
					current = next.getDoc();
					search.add(sorter.returnDoc(current));
					endSearch = false;
				}
			}
		}
		return search;
	}
}
