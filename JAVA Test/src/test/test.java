package test;

import java.util.ArrayList;

public class test {
 
	/* Fnction to check if n is a multiple of 3*/
	 
	/* Program to test function isMultipleOf3 */
	public static void main(String [] args)
	{
		long startTime = System.currentTimeMillis();
	    ArrayList<Integer> multipleOf7 = new ArrayList<Integer>();
		for (int counter = 200; counter >=0; counter --) {
			if(counter%7==0)
	        multipleOf7.add(counter);
	    }
		System.out.print(multipleOf7);
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("\nExcution Time : "+elapsedTime);
	}
	
	
}
