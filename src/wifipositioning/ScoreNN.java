/**
*
* Calculate score of calculations made by KNN classes
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.*;

class ScoreNN {
	private String filename;
	private File fileInput;
	private ArrayList<ArrayList> trues, estimates;

	public static void main(String[] args) {
		try {
			ScoreNN scoreNN = new ScoreNN(args[0]);
			scoreNN.readFile();
			scoreNN.writeFile();
		}
		catch ( ArrayIndexOutOfBoundsException e ) {
		 	System.out.println("Du skal skrive filnavn som første argument");
		}
		
	}

	public ScoreNN(String filename){
		this.filename = filename;
		fileInput = new File("../output/" + filename);
		trues = new ArrayList<ArrayList>();
		estimates = new ArrayList<ArrayList>();
	}

	public double getDistance(double x1, double x2, double y1, double y2, double z1, double z2){
        return Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2) + Math.pow(z1 - z2, 2));	
	}


	public void readFile(){
		String sCurrentLine;
		boolean trueLine = true;

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileInput));
			while ((sCurrentLine = br.readLine()) != null) {
				Pattern pattern = Pattern.compile("\\((.*?)\\)");
				Matcher matcher = pattern.matcher(sCurrentLine);
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
		ArrayList<Double> values = new ArrayList<Double>();
		int i = 0;

		for(ArrayList<Double> position : trues){
			ArrayList<Double> estimate = estimates.get(i);
			double value = getDistance(position.get(0), estimate.get(0), position.get(1), estimate.get(1), position.get(2), estimate.get(2));
			values.add(value);
			i++;
		}

		Collections.sort(values, new ScoreComparator());
		//System.out.println(values.toString());
		double allValues = values.size();
		i = 1;
		for (double value : values) {
			double valuesCloser = allValues - i;
			if (valuesCloser > 0) {
				double percentCloser = valuesCloser / allValues * 100;
				content += Double.toString(percentCloser) + "\n";
			}
			else {
				content += "0\n";
			}
			i++;
		}		

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

	private void pushPositionToArrayList(String trace, ArrayList<ArrayList> list){
		String[] stringPos = trace.split(",");
		ArrayList<Double> doublePos = new ArrayList<Double>();
		for (String pos : stringPos) {
			double dPos = Double.parseDouble(pos);
			doublePos.add(dPos);
		}
		list.add(doublePos);
	}
}

//Comparator to compare the values
class ScoreComparator implements Comparator<Double> {

    @Override
    public int compare(Double a, Double b) {
        if(a < b){
            return 1;
        } else {
            return -1;
        }
    }

}