package wifipositioning;

class CSVGenerator {
    public static void main(String[] args) {
    	CSVGenerator gen = new CSVGenerator();
    	try{
    		String method = args[0];
    		if (method.equals("ssd")){
    			gen.signalStrengthForDistance();
    		} else if (method.equals("cef")) {

    		} else if (method.equals("vma")) {

    		} else {
    			System.out.println("This program takes one parameter: Type of function, which can be one of three:\nssd: Signalstrength for distance to AP\ncef: cumulative error function\nvma: relate different values of K to median accuracy");
    		}
    	}
    	catch (ArrayIndexOutOfBoundsException e){
    			System.out.println("This program takes one parameter: Type of function, which can be one of three:\nssd: Signalstrength for distance to AP\ncef: cumulative error function\nvma: relate different values of K to median accuracy");
    	}
    }

    public void signalStrengthForDistance(){
    	
    }
}