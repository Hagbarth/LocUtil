class ModelKNN {
	private int k;

	public static void main(String[] args) {
		int k = args[0];
		ModelKNN knn = new ModelKNN(k);
	}

	public ModelKNN(int k){
		this.k = k;
	}

	public String doKNN(){
		//TODO implement modelKNN algorithm and return results in a string
	}

	public void printKnnToFile(){
		//Todo print results fomr doKNN to a file in ../output
	}
}