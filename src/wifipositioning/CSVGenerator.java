/**
*
* Generate CSV file for plotting in Excel or other
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;

import java.util.*;
import java.io.*;

import org.pi4.locutil.*;
import org.pi4.locutil.io.*;
import org.pi4.locutil.trace.*;

class CSVGenerator {
    public static void main(String[] args) {
    	CSVGenerator gen = new CSVGenerator();
    	try{
    		String method = args[0];
    		if (method.equals("ssd")){
    			gen.makeFile("ssd", gen.signalStrengthForDistance());
    		} else if (method.equals("cef")) {

    		} else if (method.equals("vma")) {

    		} else {
    			System.out.println("This program takes one parameter: Type of function, which can be one of three:\nssd: Signalstrength for distance to AP\ncef: cumulative error function\nvma: relate different values of K to median accuracy");
    		}
    	}
    	catch (ArrayIndexOutOfBoundsException e){
    			System.out.println("This program takes one parameter: Type of function, which can be one of three:\nssd: Signalstrength for distance to AP\ncef: cumulative error function\nvma: relate different values of K to median accuracy");
    	}
    }

    public String signalStrengthForDistance(){
    	//TODO implement modelKNN algorithm and return results in a string
    	ModelKNN mKNN = new ModelKNN(1);
		int offlineSize = 25;
		int onlineSize = 5;
		LoadTrace traceLoader = new LoadTrace(offlineSize, onlineSize);
		TraceGenerator trace = traceLoader.getTrace();
		trace.generate();
		int i = 1;
		String returnString = "";
		
		List<TraceEntry> offlineTrace = trace.getOffline();

		for (TraceEntry offTE : offlineTrace) {
			//Get id and position of trace entry
			GeoPosition teGeoPos = offTE.getGeoPosition();

			//Get access points
			SignalStrengthSamples samples = offTE.getSignalStrengthSamples();
			List<MACAddress> macs = samples.getSortedAccessPoints();

			//Get distances and signal strengths to accespoints
			for(MACAddress mac : macs){

				//Get access point position
				GeoPosition apGeoPos = mKNN.getAPPosistion(mac);

				if (apGeoPos != null) {
					double dist = teGeoPos.distance(apGeoPos);
					double ss = samples.getAverageSignalStrength(mac);
					returnString += i + "," + mac + "," + ss + "," + dist + "\n";
				}
			}
			i++;	
		}
		return returnString;
    }

    public void makeFile(String name, String content){
    	try {
			File file = new File("../output/csv/" + name + ".csv");
			File dir = new File("../output/csv");
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