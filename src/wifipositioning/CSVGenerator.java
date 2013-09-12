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
import java.util.regex.*;
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
    		} else if (method.equals("vmamodel")) {
    			gen.makeFile("vma_model", gen.valuesMedianAccuracyAndCumulativeErrorFunction("model"));
    		} else if (method.equals("vmafinger")) {
    			gen.makeFile("vma_fingerprint", gen.valuesMedianAccuracyAndCumulativeErrorFunction("fingerprinting"));
    		} else {
    			System.out.println("This program takes one parameter: Type of function, which can be one of three:\nssd: Signalstrength for distance to AP\nvmamodel: relate different values of K to median accuracy for model\nvmafinger: relate different values of K to median accuracy for fingerprinting");
    		}
    	}
    	catch (ArrayIndexOutOfBoundsException e){
    			System.out.println("This program takes one parameter: Type of function, which can be one of three:\nssd: Signalstrength for distance to AP\nvmamodel: relate different values of K to median accuracy for model\nvmafinger: relate different values of K to median accuracy for fingerprinting");
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

    public String valuesMedianAccuracyAndCumulativeErrorFunction(String type){
    	//Setup variables
    	String returnString = "";
    	HashMap<Integer, Double> results = new HashMap<Integer, Double>();
    	HashMap<Double, Double> errorDist = new HashMap<Double, Double>();
    	ScoreNN scoreNN = null;

    	for (int k = 1; k <= 5 ; k++) {
    		System.out.println("K = " + k);
			double totDist = 0;
			double totPercentage = 0;    		

    		for (int i = 1; i <= 100; i++ ) {
	    		System.out.println(i + ". Iteration");

	    		File scoreFile = null;
	    		if (type.equals("fingerPrinting")) {
	    			FingerPrintingKNN fknn = new FingerPrintingKNN((int) k);
	    			scoreNN = new ScoreNN("fingerPrinting", (int) k);
	    			scoreFile = new File("../output/scores/fingerPrinting" + k + "NN.txt");
	    			fknn.printKNNToFile();
	    		} else {
					ModelKNN mknn = new ModelKNN((int) k);
	    			scoreNN = new ScoreNN("model", (int) k);
	    			scoreFile = new File("../output/scores/model" + k + "NN.txt");
	    			mknn.printKNNToFile();
	    		}
	    		
	    		scoreNN.readFile();
				scoreNN.writeFile();
	    		
	    		try{
		    		BufferedReader br = new BufferedReader(new FileReader(scoreFile));
		    		String sCurrentLine;
		    		int currentLine = 1;
		    		while ((sCurrentLine = br.readLine()) != null) { 
		    			if (currentLine == 575) {
		    				Pattern pattern = Pattern.compile("Distance\\((.*?)\\)");
							Matcher matcher = pattern.matcher(sCurrentLine);
							while (matcher.find()) {
					    		String trace = matcher.group(1);
					    		double medianDist = Double.parseDouble(trace);
					    		totDist += medianDist;
					    	}
		    			}

		    			Pattern pattern = Pattern.compile("\\((.*?)\\)");
						Matcher matcher = pattern.matcher(sCurrentLine);
						boolean dist = true;
						while (matcher.find()) {
						    String trace = matcher.group(1);
							if (dist) {
								dist = false;
							}
							else {
								totPercentage += Double.parseDouble(trace);
								dist = true;
							} 
						}
		    			currentLine++;
		    		}
	    		}
	    		catch(IOException e){
	    			e.printStackTrace();
	    		}
	    	}

	    	double avPerc = totPercentage / 100;
	    	double avDist = totDist / 100;
	    	errorDist.put(avDist, totDist);
	    	results.put(k, avDist);
    	}

    	scoreNN.writeFile(errorDist);
    	returnString = results.toString();
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