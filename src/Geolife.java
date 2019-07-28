import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Geolife {
	
	public static void NodeTimeSpan() throws NumberFormatException, IOException {
		int[] nodes = new int[250];
		
		for(int i=0; i<250; i++) {
			nodes[i] = 0;
		}
		
		FileInputStream fstream = new FileInputStream("src/ActivityTraceFile.path.onesim.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  String[] strs = strLine.split(" ");

		  int n1 = Integer.parseInt(strs[0]);
		  int n2 = Integer.parseInt(strs[2]);
		  
		  nodes[n1] = n2;

		}

		//Close the input stream
		br.close();
		
		int maxTime = -1;
		int maxIndex = -1;
		
		for(int i=0; i<248; i++) {
			int sumTime = nodes[i] + nodes[i+1] + nodes[i+2];
			if(sumTime > maxTime) {
				maxTime = sumTime;
				maxIndex = i;
			}
		}
			
		System.out.println((maxIndex+1));
	}
	
	public static void NodeMobility() throws NumberFormatException, IOException {
		HashMap<Integer, Integer> nodes = new HashMap<Integer, Integer>();
		
		FileInputStream fstream = new FileInputStream("src/TraceFile.path.onesim.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		br.readLine();
		while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  String[] strs = strLine.split(" ");

		  int n = Integer.parseInt(strs[0]);
		  nodes.put(n, strs.length-1);
		}

		//Close the input stream
		br.close();
		
		//sort the nodes in ascending order
		List<Entry<Integer, Integer>> sortedNodes = new ArrayList<Entry<Integer, Integer>>(nodes.entrySet());
	    Collections.sort(sortedNodes, 
            new Comparator<Entry<Integer, Integer>>() {
                @Override
                public int compare(Entry<Integer, Integer> e1, Entry<Integer, Integer> e2) {
                    return e2.getValue().compareTo(e1.getValue());
                }
            }
	    );
		
		for(Entry<Integer, Integer> e: sortedNodes) {
			//System.out.println(e.getKey() + "\t" + e.getValue());
			System.out.println(e.getKey());
		}
	}
	
	public static void main(String[] args) throws IOException {
		//Geolife.NodeTimeSpan();
		Geolife.NodeMobility();
	}
}
