import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class GeolifeReal {
	
	public static class OneData {
		String node1;
		String node2;
		String updown;
		long ts;
		
		public OneData(String node1, String node2, String updown, long ts) {
			this.node1 = node1;
			this.node2 = node2;
			this.updown = updown;
			this.ts = ts;
		}
	}
	
	@SuppressWarnings("resource")
	public static void FindInconsistentData() throws IOException {		
		
		for(int i=0; i<182; i++) {
			String user = String.format("%03d", i);
			String FolderName = "src/Geolife Trajectories 1.3/Data/"+user+"/Trajectory";

			File folder = new File(FolderName);
			File[] listOfFiles = folder.listFiles();
			
			for (File file : listOfFiles) {
			    if (file.isFile()) {
					FileInputStream fstream = new FileInputStream(FolderName+"/"+file.getName());
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			
					String strLine;
					int skipLine = 6;
					int datePart = 0;
					//Read File Line By Line
					while ((strLine = br.readLine()) != null) {
						if(skipLine-- > 0) {
							continue;
						}
						if(datePart == 0) {
							datePart = (int)Double.parseDouble(strLine.split(",")[4]);
						}
						else if (datePart != (int)Double.parseDouble(strLine.split(",")[4])) {
							System.out.println(user + "_" + file.getName());
							break;
						}
					}
					br.close();
			    }
			}
		}
	}
	
	public static long StringToTimestamp (String dateTimeInString) throws ParseException {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		Date dateTime = dateTimeFormat.parse(dateTimeInString);
		return dateTime.getTime() / 1000;
	}
	
	public static double distanceMeasure (double lat1, double lon1, double lat2, double lon2) {
		double R = 6378.137; // Radius of earth in KM
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) 
				+ Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) 
				* Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
	    return d * 1000; // meters
	}
	
	@SuppressWarnings("resource")
	public static void FindTimeOverlap() throws IOException, ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
		
		for(int i=10; i<15; i++) {
			//reading a file
			String srcuser = String.format("%03d", i);
			String srcFolderName = "src/Geolife Trajectories 1.3/Data/"+srcuser+"/Trajectory";

			File srcfolder = new File(srcFolderName);
			File[] srclistOfFiles = srcfolder.listFiles();
			
			for (File srcfile : srclistOfFiles) {
			    if (srcfile.isFile()) {
					FileInputStream srcfstream = new FileInputStream(srcFolderName+"/"+srcfile.getName());
					BufferedReader srcbr = new BufferedReader(new InputStreamReader(srcfstream));
			        
					//read all src data into this hashmap
			        HashMap<String, ArrayList<String>> srcDataMap = new HashMap<String, ArrayList<String>>();
			        //read all comp data into this hashmap
			        HashMap<String, ArrayList<String>> compDataMap = new HashMap<String, ArrayList<String>>();
			
					String srcLine;
					int skipLine = 6;
					long prevTimestamp = 0;
					//Read File Line By Line
					while ((srcLine = srcbr.readLine()) != null) {
						if(skipLine > 0) {
							skipLine--;
							continue;
						}
						else if (skipLine == 0) { //first data line
							skipLine--;
							prevTimestamp = StringToTimestamp(srcLine.split(",")[5] + " " + srcLine.split(",")[6]);
							continue;
						}
						
						String[] srcData = srcLine.split(",");
						if(srcDataMap.containsKey(srcData[5]) == false) { //srcData[5] == date part
							srcDataMap.put(srcData[5], new ArrayList<String>());
							compDataMap.put(srcData[5], new ArrayList<String>());
						}
						
						long currentTimestamp = StringToTimestamp(srcData[5] + " " + srcData[6]);
						String data = srcuser + "," + srcData[0] + "," + srcData[1] + "," + prevTimestamp + "," + currentTimestamp;
						srcDataMap.get(srcData[5]).add(data);
						prevTimestamp = currentTimestamp;
					}
					srcbr.close();
					
					//necessary file names needed to open now
					String fileDatePart = srcfile.getName().substring(0, 8);
					Calendar calendar = Calendar.getInstance();
			        calendar.setTime(dateFormat.parse(fileDatePart));
			        calendar.add(Calendar.DAY_OF_YEAR, -1);
			        Date previousDate = calendar.getTime();
			        String filePrevDatePart = dateFormat.format(previousDate);
//			        calendar.setTime(dateFormat.parse(fileDatePart));
//			        calendar.add(Calendar.DAY_OF_YEAR, 1);
//			        Date nextDate = calendar.getTime();
//			        String fileNextDatePart = dateFormat.format(nextDate);
			        
			        //reading all other files of same configuration for comparison
			        for(int j=i+1; j<182; j++) {
						String compuser = String.format("%03d", j);
						String compFolderName = "src/Geolife Trajectories 1.3/Data/"+compuser+"/Trajectory";

						File compfolder = new File(compFolderName);
						File[] complistOfFiles = compfolder.listFiles();
						
						for (File compfile : complistOfFiles) {
						    if (compfile.isFile() && (compfile.getName().startsWith(filePrevDatePart) || compfile.getName().startsWith(fileDatePart))) {
								FileInputStream compfstream = new FileInputStream(compFolderName+"/"+compfile.getName());
								BufferedReader compbr = new BufferedReader(new InputStreamReader(compfstream));
								
								String compLine;
								int skpLine = 6;
								long prevTs = 0;
								//Read File Line By Line
								while ((compLine = compbr.readLine()) != null) {
									if(skpLine > 0) {
										skpLine--;
										continue;
									}
									String[] compData = compLine.split(",");
									if(compDataMap.containsKey(compData[5]) == true) {
										long currentTs = StringToTimestamp(compData[5] + " " + compData[6]);
										if(prevTs == 0) {
											prevTs = currentTs;
											continue;
										}
										String data = compuser + "," + compData[0] + "," + compData[1] + "," + prevTs + "," + currentTs;
										compDataMap.get(compData[5]).add(data);
										prevTs = currentTs;
									}
								}
								compbr.close();
						    }
						}
			        }
			        
			        
			        //now start bangla comparing
			        String intermediateDirectory = "src/IntermediateData/" + srcuser;
			        String intermediateFileName = intermediateDirectory + "/" + srcfile.getName();
			        File directory = new File(intermediateDirectory);
			        if (! directory.exists()){
			            directory.mkdir();
			        }
			        BufferedWriter intermediateWriter = new BufferedWriter(new FileWriter(intermediateFileName));
			        
			        for(Entry<String, ArrayList<String>> entry: srcDataMap.entrySet()) {
			        	String key = entry.getKey();
			        	
			        	for(String srcDataString: entry.getValue()) {
			        		String data = "";
			        		
			        		String[] srcData = srcDataString.split(",");
		        			long m1 = Long.parseLong(srcData[3]);
		        			long m2 = Long.parseLong(srcData[4]);
		        			
			        		ArrayList<String> compDataList = compDataMap.get(key);
			        		for(String compDataString: compDataList) {
			        			String[] compData = compDataString.split(",");
			        			long n1 = Long.parseLong(compData[3]);
			        			long n2 = Long.parseLong(compData[4]);
			        			
			        			long up = 0, down = 0;
			        			
			        			if( (m1 <= n1 && n1 <= m2) && (m1 <= n2 && n2 <= m2) ) {
			        				up = n1;
			        				down = n2;
			        			}
			        			else if ( (n1 <= m1 && m1 <= n2) && (n1 <= m2 && m2 <= n2) ) {
			        				up = m1;
			        				down = m2;
			        			}
			        			else if ( (m1 <= n1 && n1 <= m2) && (n1 <= m2 && m2 <= n2) ) {
			        				up = n1;
			        				down = m2;
			        			}
			        			else if ( (n1 <= m1 && m1 <= n2) && (m1 <= n2 && n2 <= m2) ) {
			        				up = m1;
			        				down = n2;
			        			}
			        			else {
			        				continue;
			        			}
			        			//double distance = Math.sqrt( 
			        			//		Math.pow(Double.parseDouble(srcData[1]) - Double.parseDouble(compData[1]), 2) +
			        			//		Math.pow(Double.parseDouble(srcData[2]) - Double.parseDouble(compData[2]), 2) );
			        			double distance = distanceMeasure(Double.parseDouble(srcData[1]), Double.parseDouble(srcData[2]), 
			        					Double.parseDouble(compData[1]), Double.parseDouble(compData[2]));
			        			
			        			if (distance > 1000) {
			        				continue;
			        			}
			        			
			        			data += srcData[0] + "," + compData[0] + "," + distance + "," + up + "," + down + "\n";
			        		}
			        		intermediateWriter.write(data);
			        	}
			        }
			        intermediateWriter.close();
			        
			        //System.out.println("For Debug");
			    }
			}
		}
	}
	
	public static void CreateOneData (double distance) throws IOException {
		//generate result file
        String oneFileName = "src/geolife_" + (int)distance + ".one";
        BufferedWriter oneWriter = new BufferedWriter(new FileWriter(oneFileName));
    	int count = 0;
    	String resultData = "";
        
        for(int i=0; i<181; i++) {
			//reading a file
			String user = String.format("%03d", i);
			String FolderName = "src/IntermediateData/"+user;

			File folder = new File(FolderName);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile()) {
					FileInputStream fstream = new FileInputStream(FolderName+"/"+file.getName());
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
					
					String line;
					while ((line = br.readLine()) != null) {
						String[] data = line.split(",");
//						if(Double.parseDouble(data[2]) <= distance && data[3].equals(data[4]) == false) {
//							String node1 = data[0].replaceFirst("^0+(?!$)", "");
//							String node2 = data[1].replaceFirst("^0+(?!$)", "");
//							resultData += Long.parseLong(data[3]) + "\tCONN\t" + node1 + "\t" + node2 + "\t" + "up" + "\n";
//							resultData += Long.parseLong(data[4]) + "\tCONN\t" + node1 + "\t" + node2 + "\t" + "down" + "\n";
//							if (++count == 100) {
//								oneWriter.write(resultData);
//								resultData = "";
//								count = 0;
//							}
//						}
						if(Double.parseDouble(data[2]) <= distance && Long.parseLong(data[3])+1 < Long.parseLong(data[4])) {
							String node1 = data[0].replaceFirst("^0+(?!$)", "");
							String node2 = data[1].replaceFirst("^0+(?!$)", "");
							resultData += (Long.parseLong(data[3])+1) + "\tCONN\t" + node1 + "\t" + node2 + "\t" + "up" + "\n";
							resultData += Long.parseLong(data[4]) + "\tCONN\t" + node1 + "\t" + node2 + "\t" + "down" + "\n";
							if (++count == 100) {
								oneWriter.write(resultData);
								resultData = "";
								count = 0;
							}
						}
					}
					br.close();
			    }
			}
			System.out.println(user);
		}
        oneWriter.write(resultData);
        oneWriter.close();
	}
	
	public static void FinalizeData () throws IOException {
		String[][] nodes = new String[182][182];
		for (int i=0; i<182; i++) 
			for (int j=0; j<182; j++)
				nodes[i][j] = "";
		
		//generate result file
        String oneFileName = "src/geolife.one";
        BufferedWriter oneWriter = new BufferedWriter(new FileWriter(oneFileName));
    	int count = 0;
    	String resultData = "";
    	
    	FileInputStream fstream = new FileInputStream("src/part-r-00000");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String line;
		while ((line = br.readLine()) != null) {
			String[] data = line.split("\t");
			int node1 = Integer.parseInt(data[2]);
			int node2 = Integer.parseInt(data[3]);
			
			if(nodes[node1][node2].equals(data[4]) || nodes[node2][node1].equals(data[4])) {
				continue;
			}
			else {
				resultData += line + "\n";
				nodes[node1][node2] = data[4];
				nodes[node2][node1] = data[4];
				
				if (++count == 100) {
					oneWriter.write(resultData);
					resultData = "";
					count = 0;
				}
			}
		}
		br.close();
    	
		oneWriter.write(resultData);
    	oneWriter.close();
	}

	public static void ProcessByDB () {
		/*
		 * Use Pig to process data. See the commands
		 	pig -x local;

			Lines = LOAD '/home/training/Downloads/GeoLife/geolife_1000.one' Using PigStorage('\t') AS (ts: long, conn: chararray, node1: int, node2: int, updown: chararray);
			
			DistinctLines = Distinct Lines;
			
			GroupLines = Group DistinctLines All;
			
			MinTS = foreach GroupLines Generate MIN(DistinctLines.ts) as ts;
			
			ReduceTS = foreach DistinctLines generate ts-MinTS.ts as ts, conn, node1, node2, updown;
			
			SortTS = Order ReduceTS By ts;
			
			Store SortTS INTO '/home/training/Downloads/GeoLife/Output' Using PigStorage('\t');
		 */
	}
	
	public static void NodeMobility() throws NumberFormatException, IOException {
		HashMap<Integer, Integer> nodes = new HashMap<Integer, Integer>();
		
		FileInputStream fstream = new FileInputStream("src/geolife.one");
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
//			  nodes.put(n1, nodes.get(n1)+1);
//			  nodes.put(n2, nodes.get(n2)+1);
			  
			  //this is for getting highest timestamp contacts
			  nodes.put(n1, ts);
			  nodes.put(n2, ts);
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
	
	
	public static void main(String[] args) throws IOException, ParseException {	
		//GeolifeReal.FindInconsistentData();
		//GeolifeReal.FindTimeOverlap();
		//GeolifeReal.CreateOneData(1000);
		//GeolifeReal.FinalizeData();
		
		GeolifeReal.NodeMobility();
	}
}
