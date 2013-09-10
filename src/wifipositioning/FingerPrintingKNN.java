/**
*
* Implementation of the K Nearest Neighbors Algorithm
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;
import org.pi4.locutil.MACAddress;

class FingerPrintingKNN {

	private TraceGenerator trace;
	private Map<TraceEntry, Double> distList;
	private int offlineSize, onlineSize, k;

	public static void main(String[] args) {
		
		//Parsing the imput parameter to an integer
		int k = Integer.parseInt(args[0]);

		//Instantiating a FingerPringingKNN object with k
		FingerPrintingKNN fingerPrintingKNN = new FingerPrintingKNN(k);
		fingerPrintingKNN.printKNNToFile();
	}

	//Constructor
	public FingerPrintingKNN(int k){
		//Setting the size for the stracks
		offlineSize = 25;
		onlineSize = 5;
		this.k = k;
	}

	public String doKNN(){
		String returnString = "";

		//Load the trace from files and the TraceGenerator
		LoadTrace traceLoader = new LoadTrace(offlineSize, onlineSize);
		trace = traceLoader.getTrace();
		trace.generate();
		
		//Save traces for off- and online in lists
		List<TraceEntry> offlineTrace = trace.getOffline();
		List<TraceEntry> onlineTrace = trace.getOnline();

		double ss1, ss2, ss3;
		double dest;

		//Instantiate list whith calculated distance
		distList = new HashMap<TraceEntry, Double>();

		//Loop online traces
		for(TraceEntry onTE : onlineTrace){

			//Get nearest 3 acess points
			List<MACAddress> ap = onTE.getSignalStrengthSamples().getSortedAccessPoints().subList(0, 3);

			//Get the 3 signal strengths
			ss1 = onTE.getSignalStrengthSamples().getAverageSignalStrength(ap.get(0)); 
			ss2 = onTE.getSignalStrengthSamples().getAverageSignalStrength(ap.get(1));
			ss3 = onTE.getSignalStrengthSamples().getAverageSignalStrength(ap.get(2));
			
			for(TraceEntry offTE : offlineTrace) {
				if(offTE.getSignalStrengthSamples().keySet().containsAll(ap)){
					//Get nearest 3 acess ponts for offline
					double m1 = offTE.getSignalStrengthSamples().getAverageSignalStrength(ap.get(0));
					double m2 = offTE.getSignalStrengthSamples().getAverageSignalStrength(ap.get(1));
					double m3 = offTE.getSignalStrengthSamples().getAverageSignalStrength(ap.get(2));

					//Calculate distance
					dest = Math.sqrt(Math.pow(ss1 - m1,2) + Math.pow(ss2 - m2,2) + Math.pow(ss3 - m3, 2));

					//Add the entry and distance to list
					distList.put(offTE, dest);
				}
			}
		
			//Instantiation of comarator and value to compare
			ValueComparator comparator =  new ValueComparator(distList);
			TreeMap<TraceEntry, Double> distListSorted = new TreeMap<TraceEntry, Double>(comparator);

			//Sorting the map
			distListSorted.putAll(distList);

			//Get the true posistion
		    GeoPosition truePos = new GeoPosition(onTE.getGeoPosition().getX() , onTE.getGeoPosition().getY(), onTE.getGeoPosition().getZ());

		    //Calculate estimate position
		    double estimateX = 0, estimateY = 0, estimateZ = 0;

		    Iterator it = distListSorted.keySet().iterator();
		    for (int i = 0; i < k; i++) {
		    	TraceEntry entry = (TraceEntry)it.next();
		        
		        estimateX = estimateX + entry.getGeoPosition().getX();
		        estimateY = estimateY + entry.getGeoPosition().getY();
		        estimateZ = estimateZ + entry.getGeoPosition().getZ();

		        it.remove();
		    }

		    //Calculate the average position
		    estimateX = estimateX/k; 
		    estimateY = estimateY/k; 
		    estimateZ = estimateZ/k;
			GeoPosition estimatePos = new GeoPosition(estimateX, estimateY, estimateZ);

			//Return the results
			returnString += "True: " + truePos.toString() + " - Estimate: " + estimatePos.toString() + "\n";
		}

		return returnString;
	}

	public void printKNNToFile(){
		try {
			 
			String content = this.doKNN();
 
			File file = new File("../output/fingerPrinting" + k + "NN.txt");
			File dir = new File("../output");
		    if (!dir.exists() && !dir.mkdirs()) {
		        throw new IOException("Unable to create " + dir.getAbsolutePath());
		    }
 
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//Comparator to compare the values in signal strength
class ValueComparator implements Comparator<TraceEntry> {

    Map<TraceEntry, Double> base;
    public ValueComparator(Map<TraceEntry, Double> base) {
        this.base = base;
    }

    public int compare(TraceEntry a, TraceEntry b) {
        if (base.get(a) <= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}