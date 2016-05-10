import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SearchEngine {
	static String user;
	
	SearchEngine(String user){
		this.user = user;
	}
	
	ArrayList<ArrayList<String[]>> search(String querry){
		HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> master = new HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>>();
		String[] tokens = querry.split(" ");
		for(String token: tokens){
			HashMap<String, ArrayList<HashMap<String, String>>> paths = rootPaths(token);
			master = fold(master, paths);
		}
		return rank(master);
	}
	
	static HashMap<String, ArrayList<HashMap<String, String>>> rootPaths(String value){
		HashMap<String, ArrayList<HashMap<String, String>>> ans = new HashMap<String, ArrayList<HashMap<String,String>>>();
		List<String> keys = Indexer.getReverseIndex(value);
		List<String> empty = new ArrayList<String>();
		if(keys.equals(empty)){
			ans.put(value,new ArrayList<HashMap<String, String>>());
		} else {
			HashMap<String,HashMap<String, ArrayList<HashMap<String, String>>>> cum = new HashMap<String,HashMap<String, ArrayList<HashMap<String, String>>>>();
			for(String key: keys){
				HashMap<String, ArrayList<HashMap<String, String>>> partial = rootPaths(key);
				cum.put(key,partial);
			}
			Iterator<String> iter = cum.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				HashMap<String, ArrayList<HashMap<String, String>>> docs = cum.get(key);

				Iterator<String> iter2 = docs.keySet().iterator();
				while(iter2.hasNext()){
					String doc = iter2.next();
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
	
	static HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> fold(HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> master, HashMap<String, ArrayList<HashMap<String, String>>> paths){
		Set<Integer> set = master.keySet();
		Iterator<Integer> iter = set.iterator();
		while(iter.hasNext()){
			Integer num = iter.next();
			HashMap<String, ArrayList<ArrayList<String[]>>> established = master.get(num);
			HashMap<String, ArrayList<ArrayList<String[]>>> clone = (HashMap<String, ArrayList<ArrayList<String[]>>>) established.clone();
			Iterator<String> iter2 = clone.keySet().iterator();
			while(iter2.hasNext()){
				String doc= iter2.next();
				if(paths.containsKey(doc)){
					ArrayList<ArrayList<String[]>> merged = mergePaths(established.get(doc),paths.get(doc));
					established.remove(doc);
					master.put(num, established);
					HashMap<String, ArrayList<ArrayList<String[]>>> reformed = master.get(num+1);
					if(reformed == null){
						reformed = new HashMap<String, ArrayList<ArrayList<String[]>>>();
					}
					reformed.put(doc, merged);
					master.put(num+1, reformed);
					paths.remove(doc);
				}
			}
		}
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
					channels2.add(temp);
				}
				recent.put(doc, channels2);
			}
			master.put(1, recent);
		}
		return master;
	}
	
	static ArrayList<ArrayList<String[]>> mergePaths(ArrayList<ArrayList<String[]>> master, ArrayList<HashMap<String, String>> paths){
		ArrayList<ArrayList<String[]>> new_master = new ArrayList<ArrayList<String[]>>();
		while(!master.isEmpty()){
			ArrayList<String[]> old_path = master.remove(0);
			for(HashMap<String, String> new_path : paths){
				ArrayList<String[]> combined = combine((ArrayList<String[]>) old_path.clone(),new_path);
				new_master.add(combined);
			}
		}
		return new_master;
	}
	
	static ArrayList<String[]> combine(ArrayList<String[]> old_path, HashMap<String, String> new_path){
		Iterator<String> keys = new_path.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			String[] entry = new String[2];
			entry[0] = key;
			entry[1] = new_path.get(key);
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
	
	static ArrayList<String[]> combine2(ArrayList<String[]> old_path, ArrayList<String[]> new_path){
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
	
	static ArrayList<ArrayList<String[]>> rank(HashMap<Integer, HashMap<String, ArrayList<ArrayList<String[]>>>> master){
		int constant = 2;
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
					combine2(old_path,path);
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
	
	static ArrayList<ArrayList<String[]>> sort(HashMap<String, Double> rank, HashMap<String, ArrayList<String[]>> merged){
		Sorter sorter = new Sorter();
		Iterator<String> docs = rank.keySet().iterator(); 
		while(docs.hasNext()){
			String doc = docs.next();
			sorter.addDoc(doc,rank.get(doc),merged.get(doc));
		}
		return sorter.returnPaths(10);
	}
}
