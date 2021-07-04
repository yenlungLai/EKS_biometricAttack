import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import javax.swing.SwingWorker;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class atk {

	static String[] vector1;
	static String[] vector2;

	public static void main(String[] args) {
		
		
		//********************Load x1 and x2 as different biometric feature vector****************//
		try {

			File file = new File("src\\x1.txt"); // creates a new file instance
			FileReader fr = new FileReader(file); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line); 
				sb.append(","); 
			}
			fr.close(); 
			

			String vec1 = sb.toString();
			vector1 = vec1.split(",");


			File file2 = new File("src\\x2.txt"); // creates a new file instance
			FileReader fr2 = new FileReader(file2); // reads the file
			BufferedReader br2 = new BufferedReader(fr2); // creates a buffering character input stream
			StringBuffer sb2 = new StringBuffer(); // constructs a string buffer with no characters
			String line2;
			while ((line2 = br2.readLine()) != null) {
				sb2.append(line2); 
				sb2.append(","); 
			}
			fr2.close(); 
 
			String vec2 = sb2.toString();
			vector2 = vec2.split(",");

		} catch (IOException e) {
			e.printStackTrace();
		}
		//**********************************************************************************************//
		
		double[] input_vector1 = vec_String2Double(vector1, 30);
		double[] input_vector2 = vec_String2Double(vector2, 30);

		int m = 100;
		double epsilon = 0.15;
		double Converge_rate = 0;
		double[] set_S = ini_setS(m, input_vector2);
		double reference_score = distance(input_vector1, input_vector2);
		
		

		double[] Synthetic_template = get_best_TemplateOfFit(input_vector1, input_vector2, epsilon, set_S, reference_score,
				Converge_rate);
		System.out.println(Arrays.toString(Synthetic_template));


	}
	
	
	
	
	
	
	
	
	
	
	


	public static double distance(double[] a, double[] b) {
		double diff_square_sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			diff_square_sum += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(diff_square_sum) / a.length;
	}

	public static double[] get_best_TemplateOfFit(double[] vector, double[] vector2, double epsilon, double[] set_S,
	    double reference_score, double Converge_rate) {
		
		//*********************initialize graphchart*******************************//
		double[] initdata1 = { 1 };
		double[] initdata2 = { 1 };
		// Create Chart
		final XYChart chart = QuickChart.getChart("Converging Graph", "#Batch Itaration", "Batch Minimal", "Distance Score", initdata1,
				initdata2);

		// Show it
		final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
		sw.displayChart();
		//*************************************************************************//
		
		double[] xData = new double[1];
		double[] yData = new double[1];
		int count = 0;

		double lowest_converge_score = 1;
		double[] synthetic_TemplateOfFit = null;
		for (int j = 0; j < set_S.length; j++) {
			double S_element = set_S[j];

			int n_purt = (int) Math.ceil(epsilon * (vector.length));
			int[] Purturb_location = new int[vector.length];

			for (int i = 0; i < vector.length; i++) {
				if (i < vector.length - n_purt) {
					Purturb_location[i] = 0;
				} else {
					Purturb_location[i] = 1;
				}
			}


			do {
				double[] synthetic_Template = ini_syntheticTemplate(vector2, Purturb_location, S_element);
				double synthetic_score = distance(synthetic_Template, vector);
				
				
		        //*********************updata data series for graphchart*******************************//	
//				if (count == 0) {
//					xData[0] = count;
//					yData[0] = synthetic_score;
//					count++;
//				}count++;
//				
//				if (count %  1000000==0)
//				{xData = addtoArray(xData, count);
//				yData = addtoArray(yData, synthetic_score);
//				count++;
//
//				chart.updateXYSeries("Distance Score", xData, yData, null);
//				sw.repaintChart();}
				//*********************initialize graphchart*******************************//	

				if (reference_score - synthetic_score >= Converge_rate && synthetic_score <= lowest_converge_score) {
					synthetic_TemplateOfFit = synthetic_Template;
					lowest_converge_score = synthetic_score;
					System.out.print("Minimized Synthetic Score: ");
					System.out.println(lowest_converge_score);

					//*********************updata data series for graphchart*******************************//	
					if (count == 0) {
						xData[0] = count;
						yData[0] = lowest_converge_score;
						count++;
					}
					xData = addtoArray(xData, count);
					yData = addtoArray(yData, lowest_converge_score);
					count++;
					
					chart.updateXYSeries("Distance Score", xData, yData, null);
					sw.repaintChart();
					//**************************************************************************//	
				}

			} while (nextPermutation(Purturb_location));

		}
		System.out.print("original distance Score: ");
		System.out.println(reference_score);
		System.out.print("lowest synthetic score obtained: ");
		System.out.println(lowest_converge_score);
		return synthetic_TemplateOfFit;
	}

	public static Double maxValue(double array[]) {
		List<Double> a = new ArrayList<Double>();
		for (int i = 0; i < array.length; i++) {
			a.add(array[i]);
		}
		return Collections.max(a);
	}

	public static Double minValue(double array[]) {
		List<Double> a = new ArrayList<Double>();
		for (int i = 0; i < array.length; i++) {
			a.add(array[i]);
		}
		return Collections.min(a);
	}

	public static double[] addtoArray(double[] arr, double x) {
		int i;
		int newlength = arr.length + 1;

		// create a new array of size n+1
		double newarr[] = new double[newlength];

		// insert the elements from
		// the old array into the new array
		// insert all elements till n
		// then insert x at n+1
		for (i = 0; i < arr.length; i++) {
			newarr[i] = arr[i];
		}

		newarr[newlength - 1] = x;

		return newarr;
	}

	public static double[] ini_setS(int m, double[] vector1) {
		double[] set_S = new double[m];
		double max = maxValue(vector1), min = minValue(vector1);

		double step = (max - min) / m;

		for (int i = 0; i < m; i++) {
			set_S[i] = min + (step * i);
		}
		return set_S;
	}

	public static double[] vec_String2Double(String[] vector1, int length) {
		double[] new_vec = new double[length];

		for (int i = 0; i < length; i++) {
			new_vec[i] = Double.valueOf(vector1[i]);
		}
		return new_vec;
	}

	public static String[] vec_Double2String(double[] vector1, int length) {
		String[] new_vec = new String[length];

		for (int i = 0; i < length; i++) {
			new_vec[i] = Double.toString(vector1[i]);
		}
		return new_vec;
	}

	public static boolean nextPermutation(int[] array) {
		// Find longest non-increasing suffix
		int i = array.length - 1;
		while (i > 0 && array[i - 1] >= array[i])
			i--;
		// Now i is the head index of the suffix

		// Are we at the last permutation already?
		if (i <= 0)
			return false;

		// Let array[i - 1] be the pivot
		// Find rightmost element greater than the pivot
		int j = array.length - 1;
		while (array[j] <= array[i - 1])
			j--;
		// Now the value array[j] will become the new pivot
		// Assertion: j >= i

		// Swap the pivot with j
		int temp = array[i - 1];
		array[i - 1] = array[j];
		array[j] = temp;

		// Reverse the suffix
		j = array.length - 1;
		while (i < j) {
			temp = array[i];
			array[i] = array[j];
			array[j] = temp;
			i++;
			j--;
		}

		// Successfully computed the next permutation
		return true;
	}

	public static double[] ini_syntheticTemplate(double[] vector, int[] Purturb_location, double S_element) {
		double[] Synt_template = new double[vector.length];

//       printVector_int(Purturb_location);

		for (int i = 0; i < vector.length; i++)
			if (Purturb_location[i] == 1) {
				Synt_template[i] = S_element + vector[i];
			} else {
				Synt_template[i] = vector[i];
			}

		return Synt_template;
	}

	public static int[] Hammingdistance(int[] vector1, int[] vector2) {
		int[] delta = new int[vector1.length];
		for (int i = 0; i < vector1.length; i++) {
			if (vector1[i] == vector2[i]) {
				delta[i] = 0;
			} else {
				delta[i] = 1;
			}
		}

		return delta;
	}

	public static int getHammingWeight(int[] vector1) {
		int count = 0;
		for (int i = 0; i < vector1.length; i++) {
			if (vector1[i] == 1) {
				count++;
			}

		}

		return count;
	}

	public static void printMatrix_double(double[][] R_Pmatrix) {
		System.out.print("[");
		for (int i = 0; i < R_Pmatrix.length; i++) { // this equals to the row in our matrix.

			for (int j = 0; j < R_Pmatrix[i].length; j++) { // this equals to the column in each row.
				if (j == R_Pmatrix[i].length - 1) {
					System.out.print(R_Pmatrix[i][j] + "]");
				} else {
					System.out.print(R_Pmatrix[i][j] + ",");
				}
			}
			System.out.println(""); // change line on console as row comes to end in the matrix.
		}
	}

	public static void printMatrix_int(int[][] R_Pmatrix) {
		for (int i = 0; i < R_Pmatrix.length; i++) { // this equals to the row in our matrix.
			System.out.print("[");
			for (int j = 0; j < R_Pmatrix[i].length; j++) { // this equals to the column in each row.
				if (j == R_Pmatrix[i].length - 1) {
					System.out.print(R_Pmatrix[i][j] + "]");
				} else {
					System.out.print(R_Pmatrix[i][j] + ",");
				}
			}
			System.out.println(""); // change line on console as row comes to end in the matrix.
		}
	}

	public static void printVector_double(double[] R_Pmatrix) {
		System.out.print("[");
		for (int i = 0; i < R_Pmatrix.length; i++) { // this equals to the row in our matrix.

			{ // this equals to the column in each row.
				if (i == R_Pmatrix.length - 1) {
					System.out.print(R_Pmatrix[i] + "]");
				} else {
					System.out.print(R_Pmatrix[i] + ",");
				}

				// change line on console as row comes to end in the matrix.
			}
		}
		System.out.println("");

	}

	public static void printVector_int(int[] R_Pmatrix) {
		System.out.print("[");
		for (int i = 0; i < R_Pmatrix.length; i++) { // this equals to the row in our matrix.

			{ // this equals to the column in each row.
				if (i == R_Pmatrix.length - 1) {
					System.out.print(R_Pmatrix[i] + "]");
				} else {
					System.out.print(R_Pmatrix[i] + ",");
				}

				// change line on console as row comes to end in the matrix.
			}
		}
		System.out.println("");

	}

	public static int[] Quantize(double[] R_Pmatrix) {
		int[] newarray = new int[R_Pmatrix.length];

		// this equals to the row in our matrix.

		for (int i = 0; i < R_Pmatrix.length; i++) { // this equals to the column in each row.
			if (R_Pmatrix[i] > 0) {
				newarray[i] = 1;
			} else {
				newarray[i] = 0;
			}

		}
		return newarray;

	}
	 

}
