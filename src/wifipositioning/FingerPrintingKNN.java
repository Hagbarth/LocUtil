package wifipositioning;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.pi4.locutil.GeoPosition;
import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.TraceEntry;

class FingerPrintingKNN {

	private TraceGenerator trace;

	public static void main(String[] args) {
		
		//Parsing the imput parameter to an integer
		int k = Integer.parseInt(args[0]);

		//Instantiating a FingerPringingKNN object with k
		FingerPrintingKNN fingerPrintingKNN = new FingerPrintingKNN(k);
	}

	//Constructor
	public FingerPrintingKNN(int k){

		//Setting the size for the stracks
		int offlineSize = 25;
		int onlineSize = 5;

		//Load the trace from files and the TraceGenerator
		LoadTrace traceLoader = new LoadTrace(offlineSize, onlineSize);
		trace = traceLoader.getTrace();
		trace.generate();
		
		//Save traces for off- and online in lists
		List<TraceEntry> offlineTrace = trace.getOffline();
		List<TraceEntry> onlineTrace = trace.getOnline();

		//This is just a test DELETE THIS!!
		for(TraceEntry te : onlineTrace){
			System.out.println(te.getGeoPosition().toString());
		}
	}
}