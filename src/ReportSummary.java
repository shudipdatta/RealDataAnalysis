import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

class Report {
	double coverage;
	double redundancy;
	int metadataTransferred;
	int photoTransferred;
	int photoAtDestination;
	long additionalPoiTime;
	long algorithmTime;
	double avgNodeAboveAvg;
	double avgNetworkRedundancy;
	String sdDistribution;
	
	Report() {
		coverage = 0;
		redundancy = 0;
		metadataTransferred = 0;
		photoTransferred = 0;
		photoAtDestination = 0;
		additionalPoiTime = 0;
		algorithmTime = 0;
		avgNodeAboveAvg = 0;
		avgNetworkRedundancy = 0;
		sdDistribution = "";
	}
}

public class ReportSummary {
	int[] photoNums = {1250, 2500, 5000, 10000};
	int[] scenarios = {4, 5, 6, 7, 8};
	Report[][] reports = new Report[photoNums.length][scenarios.length];
	
	public int getIndex(int[] array, int element) {
		for (int i=0; i<array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}
	
	public void CreateReport() throws IOException {
		for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				reports[i][j] = new Report();
			}
		}
		
		String FolderName = "src/reports/geolife_data/Actual_Hidden";
	
		File folder = new File(FolderName);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	String[] fileNamePart = file.getName().split("-");
		    	int photoNumber = Integer.parseInt(fileNamePart[0].split("_")[1]);
		    	int scenario = Integer.parseInt(fileNamePart[1].split("_")[1]);
		    	
		    	photoNumber = getIndex(photoNums, photoNumber);
		    	scenario = getIndex(scenarios, scenario);
		    	
				FileInputStream fstream = new FileInputStream(FolderName+"/"+file.getName());
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				
				String line;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("Gained Coverage")) {
						reports[photoNumber][scenario].coverage = Double.parseDouble(line.split("\t")[1]);
					}
					else if (line.startsWith("Gained Redundancy")) {
						reports[photoNumber][scenario].redundancy = Double.parseDouble(line.split("\t")[1]);
					}
					else if (line.startsWith("Total Transferred Metadata")) {
						reports[photoNumber][scenario].metadataTransferred = Integer.parseInt(line.split("\t")[1]);
					}
					else if (line.startsWith("Total Transferred Photo")) {
						reports[photoNumber][scenario].photoTransferred = Integer.parseInt(line.split("\t")[1]);
					}
					else if (line.startsWith("Total Photo at Destination")) {
						reports[photoNumber][scenario].photoAtDestination = Integer.parseInt(line.split("\t")[1]);
					}
					else if (line.startsWith("additionalPoiTime")) {
						reports[photoNumber][scenario].additionalPoiTime = Double.valueOf(line.split("\t")[1]).longValue(); //Long.parseLong(line.split("\t")[1]);
					}
					else if (line.startsWith("algorithmTime")) {
						reports[photoNumber][scenario].algorithmTime = Double.valueOf(line.split("\t")[1]).longValue(); //Long.parseLong(line.split("\t")[1]);
					}
					else if (line.startsWith("Nodes Above Average")) {
						if (reports[photoNumber][scenario].avgNodeAboveAvg == 0) {
							String[] data = line.split("\t");
							int sum = 0;
							int count = 0;
							for (int i=1; i<data.length; i++) {
								if(Integer.parseInt(data[i]) != 0) {
									sum += Integer.parseInt(data[i]);
									count++;
								}
							}
							reports[photoNumber][scenario].avgNodeAboveAvg = (1.0*sum) / count;
						}
					}
					else if (line.startsWith("Network Redundancy")) {
							String[] data = line.split("\t");
							int sum = 0;
							int count = 0;
							for (int i=1; i<data.length; i++) {
								sum += Integer.parseInt(data[i]);
								count++;
							}
							reports[photoNumber][scenario].avgNetworkRedundancy = (1.0*sum) / count;
					}
					else if (line.startsWith("SD Distribution")) {
						String[] data = line.split("\t");
						String sd = "";
						for (int i=1; i<data.length; i++) {
							sd += data[i] + "\t";
						}
						reports[photoNumber][scenario].sdDistribution = sd;
					}
				}
				br.close();
		    }
		}
		
		String oneFileName = "src/report_summary.txt";
        BufferedWriter oneWriter = new BufferedWriter(new FileWriter(oneFileName));
        
        String string = "Gained Coverage %\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].coverage + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nGained Redundancy %\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].redundancy + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nTotal Transferred Metadata\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].metadataTransferred + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nTotal Transferred Photos\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].photoTransferred + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nTotal Photo at Destination\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].photoAtDestination + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nadditionalPoiTime\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].additionalPoiTime + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nalgorithmTime\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].algorithmTime + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nAverage Node Above Average\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].avgNodeAboveAvg + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nAverage Network Redundancy\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].avgNetworkRedundancy + "\t";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
        string = "\nSD Distribution\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				string += reports[i][j].sdDistribution + "\n";
			}
			string += "\n";
		}       
        oneWriter.write(string);
        
    	oneWriter.close();
	}
	
	public static void main(String[] args) throws IOException {
		ReportSummary reportSummary = new ReportSummary();
		reportSummary.CreateReport();
	}
}
