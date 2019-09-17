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

public class Infocom {
	
	public static void NodeTimeSpan() throws NumberFormatException, IOException {
		int[] nodes = new int[98];
		int[] tstamps = new int[98];
		
		for(int i=0; i<98; i++) {
			nodes[i] = 0;
			tstamps[i] = 0;
		}
		
		FileInputStream fstream = new FileInputStream("src/haggle-one-infocom2006-complete.tsv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  String[] strs = strLine.split("\t");
		  if(strs.length == 5) {
			  int n1 = Integer.parseInt(strs[2]);
			  int n2 = Integer.parseInt(strs[3]);
			  int ts = Integer.parseInt(strs[0]);
			  if(ts > 168709) {
				  nodes[n1]++;
				  nodes[n2]++;
				  tstamps[n1] = ts;
				  tstamps[n2] = ts;
			  }
		  }
		}

		//Close the input stream
		br.close();
		
		for(int i=0; i<98; i++) {
			System.out.println((i+1) + "\t" + nodes[i] + "\t" + tstamps[i]);
		}
	}
	
	public static void NodeMobility() throws NumberFormatException, IOException {
		HashMap<Integer, Integer> nodes = new HashMap<Integer, Integer>();
		
		FileInputStream fstream = new FileInputStream("src/haggle-one-infocom2006-complete.tsv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  String[] strs = strLine.split("\t");
		  if(strs.length == 5) {
			  int n1 = Integer.parseInt(strs[2]);
			  int n2 = Integer.parseInt(strs[3]);
			  int ts = (int)Double.parseDouble(strs[0]);
			  
			  if(nodes.containsKey(n1) == false) {
				  nodes.put(n1, 0);
			  }
			  
			  if(nodes.containsKey(n2) == false) {
				  nodes.put(n2, 0);
			  }
			  
			  //this for getting highest number of contacts
			  nodes.put(n1, nodes.get(n1)+1);
			  nodes.put(n2, nodes.get(n2)+1);
			  
			  //this is for getting highest timestamp contacts
//			  nodes.put(n1, ts);
//			  nodes.put(n2, ts);
		  }
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
			//System.out.println(e.getKey());
			System.out.println(e.getKey() + "\t" + e.getValue());
		}
	}

	public static void main(String[] args) throws IOException {
		//Infocom.NodeTimeSpan();
		Infocom.NodeMobility();
	}

}
