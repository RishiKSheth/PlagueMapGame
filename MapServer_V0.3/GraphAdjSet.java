import java.util.*;
import java.io.*;

public class GraphAdjSet{
	private Map<String, Set<String>> g;
	private Map<String, Set<String>> bordersMap;
	private Map<String, Long> populationMap;
	private Long totalWorldPopulation;
	public GraphAdjSet(){
		 g = new HashMap<String, Set<String>>();
		 bordersMap = new HashMap<>();
		 populationMap = new HashMap<>();
		 totalWorldPopulation = 0L;
	 }

	public void add(String n1, String n2){
		if(!g.containsKey(n1)){
			g.put(n1, new HashSet<String>());
		}

		if(!g.containsKey(n2)){
			g.put(n2, new HashSet<String>());
		}

		g.get(n1).add(n2);
		g.get(n2).add(n1);
	}

	public Set<String> getNeighbors(String country) {
	    return g.getOrDefault(country, new HashSet<>());
	}

	public void loadData(String fileName){
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = br.readLine()) != null){
				String[] tokens = line.split(" ");
				add(tokens[0], tokens[1]);
			}
		} catch(IOException e){
			System.err.println("Caught IO Exception: " + e);
		}
	}

	//depth first traversal - go through entire graph starting at a given node
	public void dft(String node){
		HashSet<String> visited = new HashSet<String>();
		dftRecur(node, visited);
		System.out.println("# nodes = " + visited.size());
	}

	private void dftRecur(String node, HashSet<String> visited){
		if(!visited.contains(node)){
			//print and mark as visited
			System.out.print(node + " ");
			visited.add(node);
			//recursive visit neighbors
			for(String n: g.get(node)){
				dftRecur(n, visited);
			}
		}
	}


	public void BFT(String Node){
		HashSet<String> visited = new HashSet<>();
		Queue<String> q = new LinkedList<String>();

		while(!q.isEmpty()){
			//poll node from q
			String next = q.poll();
			//if already visited continue to next q element
			if(visited.contains(Node)){
				continue;
			}
			//visit node: print it and add to visited set
			System.out.println(Node + " -> ");
			visited.add(Node);
			//add neighbors of current node to q
			for(String neighbor: g.get(Node)){
				if(!visited.contains(neighbor)){
					q.add(neighbor);
				}
			}
		}
	}

	public String dfs(String start, String end){
		Stack<String> stack = new Stack<>();
		HashSet<String> visited = new HashSet<>();
		ArrayList<String> path = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		stack.add(start);
		while(stack.size() > 0){
			String node = stack.pop();
			if(!visited.contains(node)){
				visited.add(node);
				path.add(node);
			}

			if(node.equals(end)){
				break;
			}
			for(String n: g.get(node)){
				if(!visited.contains(n)){
					stack.add(n);
				}
			}
		}
		System.out.println("Before Loop "+path);
		sb.append("[");
		if(path.contains(end)){
			for(int i = path.size() - 1; i >= 0; i--){
				sb.append(path.get(i) + " ");
			}
		}
		sb.append("]");
		return "THE PATH:"+ sb.toString();
	}

	public String bfs(String start, String end){
		if(!g.containsKey(start)){
			return "Start node not found.";
		}

		Queue<String> queue = new LinkedList<>();
		HashSet<String> visited = new HashSet<>();
		Map<String, String> nodeBefore = new HashMap<>();

		queue.add(start);
		visited.add(start);

		while(!queue.isEmpty()){
			String node = queue.poll();
			if(node.equals(end)){
				String result = bfsPath(nodeBefore, start, end);
				System.out.println("Shortest path: " + result);

				// Calculate path length
				int pathLength = result.split(",").length - 1;
				System.out.println("Path length (distance): " + pathLength);
				return result;
			}
			for(String neighbor: g.get(node)){
				if(!visited.contains(neighbor)){
					queue.add(neighbor);
					visited.add(neighbor);
					nodeBefore.put(neighbor, node);
				}
			}
		}

		return "NO path found";
	}



	public ArrayList<String> bfsPath(String start, String end) {
	    ArrayList<String> path = new ArrayList<>();
	    if (!g.containsKey(start)) return path;

	    Queue<String> queue = new LinkedList<>();
	    HashSet<String> visited = new HashSet<>();
	    Map<String, String> nodeBefore = new HashMap<>();

	    queue.add(start);
	    visited.add(start);

	    while (!queue.isEmpty()) {
	        String current = queue.poll();
	        if (current.equals(end)) {
	            // Build path
	            LinkedList<String> fullPath = new LinkedList<>();
	            String step = end;
	            while (step != null) {
	                fullPath.addFirst(step);
	                step = nodeBefore.get(step);
	            }
	            path.addAll(fullPath);
	            return path;
	        }

	        for (String neighbor : g.getOrDefault(current, new HashSet<>())) {
	            if (!visited.contains(neighbor)) {
	                visited.add(neighbor);
	                nodeBefore.put(neighbor, current);
	                queue.add(neighbor);
	            }
	        }
	    }

	    return path; // empty if no path
	}

	//NEW METHODS
	// and population
	public void readThroughBorders(){
		String fileName = "CountryBorders.csv";
		try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			String line;
			while((line = reader.readLine()) != null){
				String[] tokens = line.split(",");
				if(tokens.length >= 5){
					String country = tokens[1].trim();
					String border = tokens[3].trim();

					//gets world population
					String countryPopulation = tokens[4].trim();
					Long countryVal = Long.parseLong(countryPopulation);
					totalWorldPopulation += countryVal;
					populationMap.put(country,countryVal);

					bordersMap.putIfAbsent(country, new HashSet<>());
					bordersMap.putIfAbsent(border, new HashSet<>());

					bordersMap.get(country).add(border);
					bordersMap.get(border).add(country);
					//bordersMap.get(countryVal).add(country);
				}
			}
		} catch (IOException e){
			System.out.println("Error reading file: " + e.getMessage());
		}
	}

	public Long returnTotalWorldPopulation(){
		return totalWorldPopulation;
	}


	public Long getCountryPopulation(String country){
		Long population = 0L;
		for(Map.Entry<String, Long> entry : populationMap.entrySet()){
			if(entry.getKey().equals(country)){
				population = entry.getValue();
			}
		}
		return population;
	}

	public void setCountryPopulation(String country, Long newPopulation){
		for(Map.Entry<String, Long> entry: populationMap.entrySet()){
			if(entry.getKey().equals(country)){
				entry.setValue(newPopulation);
			}
		}
	}

	public Set<String> getBorders(String countryYouWant){
		return bordersMap.getOrDefault(countryYouWant, new HashSet<>());
	}

	public void printBorders() {
	   for (String country : bordersMap.keySet()) {
	      System.out.println(country + ": " + bordersMap.get(country));
	   }
    }


	public void removeNode(String node) {
		if (g.containsKey(node)) {
			// First, remove this node from all its neighbors
			for (String neighbor : g.get(node)) {
				g.get(neighbor).remove(node);
			}
			// Then, remove the node itself
			g.remove(node);
		}
	}




	private String bfsPath(Map<String, String> nodebefore, String start, String end){
		List<String> path = new LinkedList<>();
		        String current = end;
		        while (current != null) {
		            path.add(0, current);
		            current = nodebefore.get(current);
		        }
        return path.toString();

	}

	public void print() {
		System.out.println("Map with " + g.size() + " vertices: ");
		for (String node : g.keySet()) {
			System.out.print(node + ": ");
			for (String neighbor : g.get(node)) {
				System.out.print(neighbor + " ");
			}
			System.out.println();
		}
	}



	public static void main(String[] args){
		GraphAdjSet gas = new GraphAdjSet();
		gas.loadData("continental_us_borders.txt");
		System.out.println("Shortest path from NJ to CA:");
		String path = gas.bfs("NJ", "CA");
    	System.out.println(path);

    	gas.readThroughBorders();  // Read borders from the CSV file
        gas.printBorders();
	}
}