import java.util.Arrays;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
 
/**
 * Creates a simple real-time chart
 */
public class RealTime_Plot {
	 
	static int nCr(int n, int r)
	{
	    return fact(n) / (fact(r) *
	                  fact(n - r));
	}
	 
	// Returns factorial of n
	static int fact(int n)
	{
	    int res = 1;
	    for (int i = 2; i <= n; i++)
	        res = res * i;
	    return res;
	}
	 
	// Driver code
	public static void main(String[] args)
	{
	    
	    System.out.println(nCr(16, 2));
	}
	}
	 
	// This code is Contributed by
	// Smitha Dinesh Semwal.