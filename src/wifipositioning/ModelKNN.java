/**
*
* Implementatidasason of the K Nearest Neighbors Algorithm
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;

import java.io.*;
import java.util.*;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.MACAddress;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;

class ModelKNN {
	private int k;
	private double pD0, n, d0, d;
	private Map<TraceEntry, Double> distList;


	public static void main(String[] args) {
		ModelKNN kNN;

		if(args.length == 1){
			kNN = new ModelKNN(Integer.parseInt(args[0]));
		}else{
			kNN = new ModelKNN(Integer.parseInt(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
		}
		

		kNN.printKNNToFile();

		// int k = Integer.getInteger(args[0]);
		// ModelKNN knn = new ModelKNN(k);
	}

	public ModelKNN(int k){
		this.k = k;
		
		//Feed information from the slides
		this.pD0 = -33.77;
		this.n = 3.415;
		this.d0 = 1;
	}

	public ModelKNN(int k, double pD0, double n, double d0){
		
		this.k = k;
		this.pD0 = pD0;
		this.n = n;
		this.d0 = d0;
	}

	public String doKNN(){
		//TODO implement modelKNN algorithm and return results in a string
		int offlineSize = 25;
		int onlineSize = 5;
		LoadTrace traceLoader = new LoadTrace(offlineSize, onlineSize);
		TraceGenerator trace = traceLoader.getTrace();
		trace.generate();
		String returnString = "";
		
		HashMap<MACAddress, Double> macSignalMap = new HashMap<MACAddress, Double>();
		
		// //Instantiation of comarator and value to compare
		// ValueComparatorModel comparator =  new ValueComparatorModel(macAddressDistMap);
		// TreeMap<MACAddress, Double> macAddressDistMapSorted = new TreeMap<MACAddress, Double>(comparator);
		
		List<TraceEntry> offlineTrace = trace.getOffline();
		List<TraceEntry> onlineTrace = trace.getOnline();

		
		for (TraceEntry offTE : offlineTrace) {
			for (MACAddress macAddress : offTE.getSignalStrengthSamples().keySet()) {
				if(getAPPosistion(macAddress) != null){
					d = offTE.getGeoPosition().distance(getAPPosistion(macAddress));
					double pDdBm = pD0 - 10 * n * Math.log10(d / d0);
					macSignalMap.put(macAddress, pDdBm);
				}
			}
			for (MACAddress macAddress : macSignalMap.keySet()){
				//Change the signalstrength in offline to the calculated
				offTE.getSignalStrengthSamples().remove(macAddress);
				offTE.getSignalStrengthSamples().put(macAddress, macSignalMap.get(macAddress));
			}
			macSignalMap.clear();
		}
		
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
 
			File file = new File("../output/model" + k + "NN.txt");
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

	//Get the GeoPosition of a given macAddress
    public GeoPosition getAPPosistion(MACAddress macAddress)
    {
        try{
        	BufferedReader br = new BufferedReader(new FileReader("../data/MU.AP.positions"));
        	String line;
        	while ((line = br.readLine()) != null) {
        		String comment = line.substring(0, 1);
        		if(! comment.equals("#")){
        			String[] splitArray = line.split("\\s+");
        			if(macAddress.toString().equals(splitArray[0]) ){
        				return new GeoPosition(Double.parseDouble(splitArray[1]), Double.parseDouble(splitArray[2]));
        			}
        		}
        	}
        	br.close();
        }
        catch(Exception e){
        	System.out.println(e);
        }
        return null;
    }
}

//Comparator to compare the values in signal strength
class ValueComparatorModel implements Comparator<MACAddress> {

    Map<MACAddress, Double> base;
    public ValueComparatorModel(Map<MACAddress, Double> base) {
        this.base = base;
    }

    public int compare(MACAddress a, MACAddress b) {
        if (base.get(a) <= base.get(b)) {
            return 1;
        } else {
            return -1;
        }
    }
}