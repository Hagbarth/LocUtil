/**
*
* Calculate score of calculations made by KNN classes
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.math.BigDecimal;

import org.pi4.locutil.*;

class ScoreNN {

//================================================================================
// Le Properties
//================================================================================
	private String filename;
	private File fileInput;
	private ArrayList<GeoPosition> trues, estimates;

//================================================================================
// Le Main
//================================================================================
	public static void main(String[] args) {
		//Create new ScoreNN, read the file and write new file
		try {
			ScoreNN scoreNN = new ScoreNN(args[0]);
			scoreNN.readFile();
			scoreNN.writeFile();
		}
		catch ( ArrayIndexOutOfBoundsException e ) {
		 	System.out.println("Du skal skrive filnavn som første argument");
		}
		
	}

//================================================================================
// Le Constructors
//================================================================================
	public ScoreNN(String filename){
		this.filename = filename;
		fileInput = new File("../output/" + filename);
		trues = new ArrayList<GeoPosition>();
		estimates = new ArrayList<GeoPosition>();
	}

//================================================================================
// Le Math Methods
//================================================================================
	private double round(double unrounded, int precision, int roundingMode){
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}

//================================================================================
// Le IO Methods
//================================================================================
	public void readFile(){
		String sCurrentLine;
		boolean trueLine = true;

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileInput));

			//Find positions from txt file input
			while ((sCurrentLine = br.readLine()) != null) {
				Pattern pattern = Pattern.compile("\\((.*?)\\)");
				Matcher matcher = pattern.matcher(sCurrentLine);

				//If it is a true position push to trues array list
				//If it is an estimate position push to estimates list
				while (matcher.find()) {
				    String trace = matcher.group(1);
					if (trueLine) {
						pushPositionToArrayList(trace, trues);
						trueLine = false;
					} 
					else {
						pushPositionToArrayList(trace, estimates);
						trueLine = true;
					}
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void writeFile(){
		String content = "";
		ArrayList<PositioningError> errorList = new ArrayList<PositioningError>();

		//Push positioning errors from the two array lists into a PositioningError Array list
		int i = 0;
		for (GeoPosition pos: trues) {
			GeoPosition estimate = estimates.get(i);
			PositioningError posErr = new PositioningError(pos, estimate);
			errorList.add(posErr);
			i++;
		}

		//Sort error list from high distance to low
		Collections.sort(errorList);
		Collections.reverse(errorList);	

		double allErrors = errorList.size();
		double posErrsCloserOrEqual = 0;
		i = 1;
		for (PositioningError posErr : errorList) {
			//If it is not the first element in list, find out if previous element is th same distance and handle it accordingly.
			if (i > 1) {
				PositioningError prevErr = errorList.get(i-2);
				posErrsCloserOrEqual = posErr.getPositioningError() == prevErr.getPositioningError() ? posErrsCloserOrEqual : allErrors - i;
			} else {
				posErrsCloserOrEqual = allErrors - i;
			}

			//Calculate percentages and push to output string
			if (posErrsCloserOrEqual > 0) {
				double percentCloser = round(posErrsCloserOrEqual / allErrors * 100, 2, BigDecimal.ROUND_HALF_UP);
				content += i + ": Distance(" + posErr.getPositioningError() + "), Percent Closer or Equal(" + percentCloser + ")\n";
			}
			else {
				content += i + ": Distance(" + posErr.getPositioningError() + "), Percent Closer or Equal(0)\n";
			}
			i++;
		}		

		//Create new txt file from output string
		try { 
			File file = new File("../output/scores/" + filename);
			File dir = new File("../output/scores");
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

//================================================================================
// Le Random Private Methods
//================================================================================
	private void pushPositionToArrayList(String trace, ArrayList<GeoPosition> list){
		String[] stringPos = trace.split(",");
		int i = 1;
		GeoPosition geoPos = new GeoPosition(0.0, 0.0, 0.0);

		//Convert string trace to a GeoPosition and add to array list
		for (String pos : stringPos) {
			double dPos = Double.parseDouble(pos);
			switch(i){
				case 1:
					geoPos.setX(dPos);
					break;
				case 2:
					geoPos.setY(dPos);
					break;
				case 3:
					geoPos.setZ(dPos);
			}
			i++;
		}
		list.add(geoPos);
	}
}