import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;


public class Run {

	
	
	
	public static void main(String[] args) {
		
		double[] vector= {0.0246570935579085,0.00276788485360965,-0.00229749535354773,-0.00872191764735175,-0.0221567503012765,0.0361709513528791,0.00266406015240267,-0.0138806727693724,0.00551299307228353,-0.00867537564279708};
		double[] vector2= {0.0123405854818621,-0.00624639632456958,-0.00957642024353378,-0.00207988448450042,-0.0308374662002230,0.0399277266787150,0.00430700523138372,-0.0182740044869186,0.0123392803928509,-0.0148710068235600};
		int[] key= {1, 0, 1, 0, 0, 0, 1, 1};  
		
		int n=11, k=8, t=4;//n>k, k= key.length, t=weight of key, i.e., no. of '1' in the key
		
		
	int Count=0, 
	Maximum_num_of_try=1000;  // number of (encoding,decoding) we tried for a pair of feature strings (vector1 and vector2)
	while (Count<Maximum_num_of_try) {
		
		double[][] GP=iniRandomGaussianMatrix(n,k,vector); //initialize GPMatrix for encoding and decoding
		
		//Encoding
        int[] y=Encoding(n, k, vector, key, GP);

        
        //Dncoding
       int[] Recovered_key= Decoding(n, k,vector2, GP, y, t);
       System.out.println("Tried "+Integer.toString(Count)+"th GP initialization");
       if (Recovered_key!=null) {
        Count=Maximum_num_of_try;
        System.out.println("Recovered Key: "+Arrays.toString(Recovered_key)); 
        }
       else {
    	   Count++;
       }
       
       if (Recovered_key==null && Count==Maximum_num_of_try) {System.out.println("No Solution found"); }

	}
  
	 
	 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int[] Decoding(int n, int k, double[] vector, double[][] R_Pmatrix, int[] y ,int t) {
	
		int[] recovered_key=null;
		   int [] first_key_try= new int[k];
//	        System.out.println(first_key_try.length);
			for(int i=0;i<first_key_try.length;i++)
				if (i<k-t) {
				first_key_try[i] = 0;}
				else {first_key_try[i] = 1;}
//			 System.out.println("1st try: "+Arrays.toString(first_key_try));
//			System.out.println(Arrays.toString(first_key_try));
		
		  do {  // Must start at lowest permutation
//			  System.out.println("Key tried: "+Arrays.toString(first_key_try));
			 if (D_T(n, k, vector, first_key_try, R_Pmatrix, y, t)==true){;
//			 System.out.println("Key Solution found: "+Arrays.toString(first_key_try)); 
			 recovered_key=first_key_try;
			 break;

//			 return Recovered_key;
			
			 }
			 
		  } while (nextPermutation(first_key_try)) ;
		   return recovered_key;
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

	public static boolean D_T(int n, int k, double[] vector, int[] key, double[][] R_Pmatrix, int[] y ,int t)
	{
		boolean DT_result=false;
		int[] y2=new int[n];
		int[][] Generator_Mat= iniGeneratorMatrix(n, k, vector, R_Pmatrix);
//        printMatrix_int(Generator_Mat);

		y2=multiply_int(Generator_Mat, key);
		int[] delta=Hammingdistance(y2, y);
//		System.out.print("delta: ");
//		printVector_int(delta);
		
		
		if (getHammingWeight(delta)<=t) {DT_result=true;}
		
		return DT_result;
	}
	
	public static int[] Hammingdistance(int[] vector1, int[] vector2) {
		int[] delta= new int[vector1.length];
		for (int i = 0; i < vector1.length; i++) {
			if (vector1[i]==vector2[i]) {
				delta[i]=0;
			} 
			else {delta[i]=1;}
		}
		
		
		return delta;
	}

	public static int getHammingWeight(int[] vector1) {
		int count=0;
		for (int i = 0; i < vector1.length; i++) {
			if (vector1[i]==1) {
				count++;
			} 
			
		}
		
		
		return count;
	}
	
	public static int[] Encoding(int n, int k, double[] vector, int[] key, double[][] R_Pmatrix)
	{
		int[] Codeword=new int[n];
		int[][] Generator_Mat= iniGeneratorMatrix(n, k, vector, R_Pmatrix);
//        printMatrix_int(Generator_Mat);

		Codeword=multiply_int(Generator_Mat, key);
//		printVector_int(Codeword);
		
		return Codeword;
	}
	
	public static int[][] iniGeneratorMatrix(int n, int k, double[] vector, double[][] R_Pmatrix)
	{
		int[][] Generator_Mat= new int [n][k]; 
		int ell = vector.length;
//		 printMatrix_double(R_Pmatrix);
		
//		 printMatrix_double(R_Pmatrix);
		for(int i=0; i<n; i++){
			double [] selectedRow = R_Pmatrix[i];
//			printVector_double(selectedRow);
			double [][] GP_Matrix=rowReshape(selectedRow, k, ell);
//			 printMatrix_double(GP_Matrix);
			double [] projected_v=multiply_double(GP_Matrix, vector);
			
//			printVector_double(projected_v);
			int[] RV=Quantize(projected_v);

		    for(int j=0; j<k; j++){
		    	Generator_Mat[i][j]=RV[j];
		    }
		   
		}
		
		return Generator_Mat;
	}
	
	public static double[][] iniRandomGaussianMatrix(int n, int k, double[] vector)
	{	int ell = vector.length;
		double[][] R_Pmatrix= new double[n][k*ell];  

		for(int i=0; i<n; i++){
		   
		    for(int j=0; j<k*ell; j++){
		    	Random r = new Random();
		    	double g = r.nextGaussian();
		    	R_Pmatrix[i][j]=g; 
		    }
		   
		}
		return R_Pmatrix;
	}

	public static double[][] rowReshape(double[] vector, int k, int ell)
	{	double[][] R_Pmatrix= new double[k][ell]; 
		int count= 0;
		if (vector.length!=(k*ell)) {System.out.println("length of input vector must equal");
		return R_Pmatrix;}
		else {
		 

		for(int i=0; i<k; i++){
		   
		    for(int j=0; j<ell; j++){
		    
		    	R_Pmatrix[i][j]=vector[count]; 	count++;
		    }
		   
		}
		return R_Pmatrix;}
		
	}
	
	public static double[] multiply_double(double[][] matrix, double[] vector) {
	    return Arrays.stream(matrix)
	            .mapToDouble(row -> IntStream.range(0, row.length)
	                    .mapToDouble(col -> row[col] * vector[col])
	                    .sum())
	            .toArray();
	}
	
	public static int[] multiply_int(int[][] matrix, int[] vector) {
	    return Arrays.stream(matrix)
	            .mapToInt(row -> IntStream.range(0, row.length)
	                    .map(col -> row[col] * vector[col])
	                    .sum())
	            .toArray();
	}
	
	public static void printMatrix_double(double[][] R_Pmatrix)
	{System.out.print("["); 
		for (int i = 0; i < R_Pmatrix.length; i++) { //this equals to the row in our matrix.
			
	         for (int j = 0; j < R_Pmatrix[i].length; j++) { //this equals to the column in each row.
	        	 if (j==R_Pmatrix[i].length-1) {
	        		 System.out.print(R_Pmatrix[i][j] + "]");}
	        	 else {
	        	     System.out.print(R_Pmatrix[i][j] + ",");
	        	 }
	         }
	         System.out.println(""); //change line on console as row comes to end in the matrix.
	      }
	}

	public static void printMatrix_int(int[][] R_Pmatrix)
	{
		for (int i = 0; i < R_Pmatrix.length; i++) { //this equals to the row in our matrix.
			System.out.print("["); 
	         for (int j = 0; j < R_Pmatrix[i].length; j++) { //this equals to the column in each row.
	        	 if (j==R_Pmatrix[i].length-1) {
	        		 System.out.print(R_Pmatrix[i][j] + "]");}
	        	 else {
	        	     System.out.print(R_Pmatrix[i][j] + ",");
	        	 }
	         }
	         System.out.println(""); //change line on console as row comes to end in the matrix.
	      }
	}
	
	public static void printVector_double(double[] R_Pmatrix)
	{	
		System.out.print("["); 
		for (int i = 0; i < R_Pmatrix.length; i++) { //this equals to the row in our matrix.
		
	     { //this equals to the column in each row.
	        	 if (i==R_Pmatrix.length -1) {
	        		 System.out.print(R_Pmatrix[i] + "]");}
	        	 else {
	        	     System.out.print(R_Pmatrix[i] + ",");
	        	 }
	         
	       //change line on console as row comes to end in the matrix.
	      }
	} System.out.println("");
	
	}
	
	public static void printVector_int(int[] R_Pmatrix)
	{	
		System.out.print("["); 
		for (int i = 0; i < R_Pmatrix.length; i++) { //this equals to the row in our matrix.
		
	     { //this equals to the column in each row.
	        	 if (i==R_Pmatrix.length -1) {
	        		 System.out.print(R_Pmatrix[i] + "]");}
	        	 else {
	        	     System.out.print(R_Pmatrix[i] + ",");
	        	 }
	         
	       //change line on console as row comes to end in the matrix.
	      }
	} System.out.println("");
	
	}

	public static int[] Quantize(double[] R_Pmatrix)
	{	
		int[] newarray= new int[R_Pmatrix.length];
		
		 //this equals to the row in our matrix.
			
	         for (int i = 0; i < R_Pmatrix.length; i++) { //this equals to the column in each row.
	        	 if (R_Pmatrix[i]>0) {
	        		 newarray[i]=1;}
	        	 else {
	        	     newarray[i]=0;
	        	 }
	         
	       
	      }
		return newarray;
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
