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
		FingerPrintingKNN fpKNN = new FingerPrintingKNN(1);
	}
}
