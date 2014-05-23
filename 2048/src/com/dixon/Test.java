package com.dixon;

public class Test {
	public static void main(String[] args) {
//		System.out.println(Math.pow(2, 11));
//		System.out.println(Math.sqrt(9));
		
		
		double a = 131072;
		System.out.println(tt(a, 0));
		
//		System.out.println(Math.pow(2, 2));
	}
	
	
	public static int tt(double a, int n){
		if(a == 2)
			return n;
		else{
			n++;
			
			return tt(a/2, n);
		}
		
	}
}
