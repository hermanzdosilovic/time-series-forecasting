package hr.fer.zemris.project.forecasting.models;

import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

public abstract class InvertibilityCheck {
	
	public final static double PRECISION = 10e-2;
	
	public static boolean check(double...coeff){
		
		double[] a = new double[coeff.length + 1];
		a[0] = 1;
		for(int i = 1; i < a.length; i++){
			a[i] = coeff[i-1];
		}
		
		LaguerreSolver p = new LaguerreSolver();
		
		Complex[] solutions = p.solveAllComplex(a, 0);
		
		boolean invertible = true;
		
		for (Complex complex : solutions) {
			if(complex.abs() < 1 - PRECISION){
				invertible = false;
				System.out.println(complex);
			}
		}
		
		return invertible;
	}
}
