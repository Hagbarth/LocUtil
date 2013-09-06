package wifipositioning;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;

public class FingerPrintingNN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String offlinePath = "data/MU.1.5meters.offline.trace", onlinePath = "data/MU.1.5meters.online.trace";
		double[][] positions = {{13.0, 13.0, 0.0, 0.0}, {14.0, 14.0, 0.0, 0.0}}; 
		
		File offlineFile = new File(offlinePath);
		Parser offlineParser = new Parser(offlineFile);
		File onlineFile = new File(onlinePath);
		Parser onlineParser = new Parser(onlineFile);
		
		
		//Construct trace generator
		for(double[] position: positions){
			System.out.println("" + position[0]);
		}
		
		TraceGenerator tg;
		try {
			int offlineSize = 25;
			int onlineSize = 5;
			tg = new TraceGenerator(offlineParser, onlineParser,offlineSize,onlineSize);
			
			//Generate traces from parsed files
			tg.generate();
			
			//Iterate the trace generated from the offline file
			List<TraceEntry> offlineTrace = tg.getOffline();			
			for(TraceEntry entry: offlineTrace) {
				//Print out coordinates for the collection point and the number of signal strength samples
				System.out.println(entry.getGeoPosition().toString() + " - " + entry.getSignalStrengthSamples().size());				
			}
			
			//Iterate the trace generated from the online file
			List<TraceEntry> onlineTrace = tg.getOnline();			
			for(TraceEntry entry: onlineTrace) {
				//Print out coordinates for the collection point and the number of signal strength samples
				System.out.println(entry.getGeoPosition().toString() + " - " + entry.getSignalStrengthSamples().size());
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		try {
			 
			String content = "This is the content to write into file";
 
			File file = new File("output/filename.txt");
			File dir = new File("output");
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
		*/
	}
}
