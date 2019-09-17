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
	long serverTime;
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
	int[] photoNums;
	int[] scenarios;
	int[] serverNums;
	Report[][][] reports;
	
	public ReportSummary(int[] photoNums, int[] scenarios, int[] serverNums) {
		this.photoNums = photoNums;
		this.scenarios = scenarios;
		this.serverNums = serverNums;
		this.reports = new Report[photoNums.length][scenarios.length][serverNums.length];
	}
	
	public int getIndex(int[] array, int element) {
		for (int i=0; i<array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}
	
	public void CreateReport(String FolderName) throws IOException {
		for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					reports[i][j][k] = new Report();
				}
			}
		}
			
		File folder = new File(FolderName);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	String[] fileNamePart = file.getName().split("-");
		    	int part1 = Integer.parseInt(fileNamePart[0].split("_")[1]);
		    	int part2 = Integer.parseInt(fileNamePart[1].split("_")[1]);
		    	int part3 = Integer.parseInt(fileNamePart[2].split("_")[1]);
		    	
		    	int photoNumber, scenario, serverNumber;
		    	
		    	if (serverNums.length == 1) { //actually it is >1
		    		serverNumber = getIndex(serverNums, part1);
		    		photoNumber = getIndex(photoNums, part2);
			    	scenario = getIndex(scenarios, part3);
		    	}
		    	else {
		    		serverNumber = getIndex(serverNums, 2);
		    		photoNumber = getIndex(photoNums, part1);
			    	scenario = getIndex(scenarios, part2);
		    	}
		    	
				FileInputStream fstream = new FileInputStream(FolderName+"/"+file.getName());
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				
				String line;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("Gained Coverage")) {
						reports[photoNumber][scenario][serverNumber].coverage = Double.parseDouble(line.split("\t")[1]);
					}
					else if (line.startsWith("Gained Redundancy")) {
						reports[photoNumber][scenario][serverNumber].redundancy = Double.parseDouble(line.split("\t")[1]);
					}
					else if (line.startsWith("Total Transferred Metadata")) {
						reports[photoNumber][scenario][serverNumber].metadataTransferred = Integer.parseInt(line.split("\t")[1]);
					}
					else if (line.startsWith("Total Transferred Photo")) {
						reports[photoNumber][scenario][serverNumber].photoTransferred = Integer.parseInt(line.split("\t")[1]);
					}
					else if (line.startsWith("Total Photo at Destination")) {
						reports[photoNumber][scenario][serverNumber].photoAtDestination = Integer.parseInt(line.split("\t")[1]);
					}
					else if (line.startsWith("additionalPoiTime")) {
						reports[photoNumber][scenario][serverNumber].additionalPoiTime = Double.valueOf(line.split("\t")[1]).longValue(); //Long.parseLong(line.split("\t")[1]);
					}
					else if (line.startsWith("algorithmTime")) {
						reports[photoNumber][scenario][serverNumber].algorithmTime = Double.valueOf(line.split("\t")[1]).longValue(); //Long.parseLong(line.split("\t")[1]);
					}
					else if (line.startsWith("serverTime")) {
						reports[photoNumber][scenario][serverNumber].serverTime = Double.valueOf(line.split("\t")[1]).longValue(); //Long.parseLong(line.split("\t")[1]);
					}
					else if (line.startsWith("Nodes Above Average")) {
						if (reports[photoNumber][scenario][serverNumber].avgNodeAboveAvg == 0) {
							String[] data = line.split("\t");
							int sum = 0;
							int count = 0;
							for (int i=1; i<data.length; i++) {
								if(Integer.parseInt(data[i]) != 0) {
									sum += Integer.parseInt(data[i]);
									count++;
								}
							}
							reports[photoNumber][scenario][serverNumber].avgNodeAboveAvg = (1.0*sum) / count;
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
							reports[photoNumber][scenario][serverNumber].avgNetworkRedundancy = (1.0*sum) / count;
					}
					else if (line.startsWith("SD Distribution")) {
						String[] data = line.split("\t");
						String sd = "";
						for (int i=1; i<data.length; i++) {
							sd += data[i] + "\t";
						}
						reports[photoNumber][scenario][serverNumber].sdDistribution = sd;
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
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].coverage + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nGained Redundancy %\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].redundancy + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nTotal Transferred Metadata\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].metadataTransferred + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nTotal Transferred Photos\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].photoTransferred + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nTotal Photo at Destination\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].photoAtDestination + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nadditionalPoiTime\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].additionalPoiTime + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nalgorithmTime\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].algorithmTime + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nserverTime\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].serverTime + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nAverage Node Above Average\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].avgNodeAboveAvg + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nAverage Network Redundancy\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].avgNetworkRedundancy + "\t";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
        string = "\nSD Distribution\n";
        for (int i=0; i<photoNums.length; i++) {
			for (int j=0; j<scenarios.length; j++) {
				for (int k=0; k<serverNums.length; k++) {
					string += reports[i][j][k].sdDistribution + "\n";
				}
				string += "\n";
			}
		}       
        oneWriter.write(string);
        
    	oneWriter.close();
	}
	
	public static void main(String[] args) throws IOException {

		//int[] photoNums = {1250, 2500, 5000, 10000};
		int[] photoNums = {5000};
		int[] scenarios = {4, 5, 6, 7, 8};
		int[] serverNums= {2};
		//int[] serverNums= {1, 3, 5, 7, 9};
		String FolderName = "src/reports/asturies_data/Actual_Hidden";
		
		ReportSummary reportSummary = new ReportSummary(photoNums, scenarios, serverNums);
		reportSummary.CreateReport(FolderName);
	}
}
