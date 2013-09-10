/**
*
* Usage of the K Nearest Neigbor alogrithm to find the Nearest Neighbor
*
* @author Fluo (Søren Lundtof, Laurits Langberg and Emil Rasmussen)
* @version 1.0.1
* 
*/

package wifipositioning;

//import wifiposFingerPrintingKNN;

public class FingerPrintingNN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FingerPrintingKNN knn = new FingerPrintingKNN(1);
		knn.printKNNToFile();
	}
}
