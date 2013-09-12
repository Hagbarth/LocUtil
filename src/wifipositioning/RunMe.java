package wifipositioning;

class RunMe {
	public static void main(String[] args) {
		CSVGenerator csv = new CSVGenerator();
		//csv.cumulativeErrorFunction("fingerPrinting", 3);
		
		csv.valuesMedianAccuracyAndCumulativeErrorFunction("fingerPrinting");
		System.out.println("\n\nWifi fingerprinting KNN run with k values from 1-5, results in output/fingerprinting{K}NN.txt\ncsv file with median error distances in output/csv/vma_fingerprint\n\n");
		csv.valuesMedianAccuracyAndCumulativeErrorFunction("model");
		System.out.println("\n\nWifi model KNN run with k values from 1-5, results in output/model{K}NN.txt\ncsv file with median error distances in output/csv/vma_model\n\n");

		
	}
}