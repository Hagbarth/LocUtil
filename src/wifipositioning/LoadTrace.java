package wifipositioning;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;

class LoadTrace {

	private int onlineSize;
	private int offlineSize;
	
	public LoadTrace(int onlineSize, int offlineSize) {

		//Set the variables
		this.onlineSize = onlineSize;
		this.offlineSize = offlineSize;
	}

	public TraceGenerator getTrace() {
		
		//Path to the trace files
		String offlinePath = "data/MU.1.5meters.offline.trace", onlinePath = "data/MU.1.5meters.online.trace";
		
		try{

			//Construct parsers
			File offlineFile = new File(offlinePath);
			Parser offlineParser = new Parser(offlineFile);
			System.out.println("Offline File: " +  offlineFile.getAbsoluteFile());
			
			File onlineFile = new File(onlinePath);
			Parser onlineParser = new Parser(onlineFile);
			System.out.println("Online File: " + onlineFile.getAbsoluteFile());

			//Generate and return traces from parsed files
			return new TraceGenerator(offlineParser, onlineParser,offlineSize,onlineSize);
		}
		catch(Exception e){
			System.out.println(e);
		}
		return null;
	}
}