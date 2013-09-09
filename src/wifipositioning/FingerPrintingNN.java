/**
*
* Usage of the K Nearest Neigbor alogrithm to find the Nearest Neighbor
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;
package wifipositioningM

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;

import wifipositioning.LoadTrace;

public class FingerPrintingNN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
<<<<<<< HEAD
		
		double[][] positions = {{13.0, 13.0, 0.0, 0.0}, {14.0, 14.0, 0.0, 0.0}}; 
		
		//Construct trace generator
		for(double[] position: positions){
			System.out.println("" + position[0]);
		}
		
		TraceGenerator tg;
		try {
			int offlineSize = 25;
			int onlineSize = 5;
			
			//Load data
			LoadTrace lt = new LoadTrace(onlineSize, offlineSize);
			tg = lt.getTrace();
			
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
			List<TraceEntry> onlineTrace = tg.getOnline();	
			for(TraceEntry entry: onlineTrace) {
				//Print out coordinates for the collection point and the number of signal strength samples
				System.out.println(entry.getGeoPosition().toString() + " - " + entry.getSignalStrengthSamples().size());
			}
			
		} catch (NumberFormatException e) {
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
=======
		FingerPrintingKNN fpKNN = new FingerPrintingKNN(1);
>>>>>>> 3790f7badd3adb136196f55fd0feb3e417102330
	}
}
