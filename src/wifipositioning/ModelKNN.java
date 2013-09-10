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
	private double pD0, n, d0;

	public static void main(String[] args) {
		ModelKNN kNN;

		if(args.length == 1){
			kNN = new ModelKNN(Integer.parseInt(args[0]));
		}else{
			kNN = new ModelKNN(Integer.parseInt(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
		}
		
		kNN.doKNN();

	}

	public ModelKNN(int k){
		this.k = k;
		this.pD0 = -33.77;
		this.n = 3.415;
		this.d0 = 1.00;
	}

	public ModelKNN(int k, double pD0, double n, double d0){
		
		this.k = k;
		this.pD0 = pD0;
		this.n = n;
		this.d0 = d0;
	}

	public void doKNN(){
		//TODO implement modelKNN algorithm and return results in a string
		int offlineSize = 25;
		int onlineSize = 5;
		LoadTrace traceLoader = new LoadTrace(offlineSize, onlineSize);
		TraceGenerator trace = traceLoader.getTrace();
		trace.generate();
		
		HashMap<MACAddress, Double> macAddressDistMap = new HashMap<MACAddress, Double>();
		
		//Instantiation of comarator and value to compare
		ValueComparator comparator =  new ValueComparator(macAddressDistMap);
		TreeMap<MACAddress, Double> macAddressDistMapSorted = new TreeMap<MACAddress, Double>(comparator);
		
		List<TraceEntry> offlineTrace = trace.getOffline();
		
		for (TraceEntry offTE : offlineTrace) {
			for (MACAddress macAddress : offTE.getSignalStrengthSamples().keySet()) {
				
				if(getAPPosistion(macAddress) != null){
					double dist = pD0 - 10 * n * Math.log10(offTE.getGeoPosition().distance(getAPPosistion(macAddress)) / d0);
					macAddressDistMap.put(macAddress, dist);
				}

			}
		}
		macAddressDistMapSorted.putAll(macAddressDistMap);

		//Calculate estimate position
		double estimateX = 0, estimateY = 0, estimateZ = 0;

		Iterator it = macAddressDistMapSorted.keySet().iterator();
		    for (int i = 0; i < k; i++) {
		    	MACAddress entry = (MACAddress) it.next();
		        
				estimateX = estimateX + getAPPosistion(entry).getX();
		        estimateY = estimateY + getAPPosistion(entry).getY();
		        estimateZ = estimateZ + getAPPosistion(entry).getZ();

		       	//System.out.println(entry.toString()+"    "+macAddressDistMap.get(entry));

		        it.remove();
		    }

		    //Calculate the average position
		    estimateX = estimateX/k; 
		    estimateY = estimateY/k; 
		    estimateZ = estimateZ/k;
			GeoPosition estimatePos = new GeoPosition(estimateX, estimateY, estimateZ);

			System.out.println("Estimate: " + estimatePos.toString());
	}

	public void printKnnToFile(){
		//Todo print results fomr doKNN to a file in ../output
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
class ValueComparator implements Comparator<MACAddress> {

    Map<MACAddress, Double> base;
    public ValueComparator(Map<MACAddress, Double> base) {
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