package hr.fer.zemris.projekt.predictions.models2;

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import Jama.Matrix;

public class AR {
	
	private int order;
	private double[] dataset;
	private Matrix coeff;
	private double[] forecastedValues;
	private double[] errorTerms;
	private double errorMean;
	private double errorSigma;
	private boolean differenced;
	
	public AR(int order, double[] dataset, boolean differenced){
		this.order = order;
		this.dataset = dataset;
		this.coeff = new Matrix(order, 1);
		this.differenced = differenced;
		forecastedValues = dataset.clone();
		errorTerms = new double[dataset.length - order];
	}
	
	private void findErrorTerms(){
		for(int i = order; i < dataset.length; i++){
			double value = dataset[i];
			double coeffValue = 0;
			for(int j = 1; j <= order; j++){
				coeffValue += coeff.get(j-1, 0) * dataset[i-j];
			}
			errorTerms[i - order] = value - coeffValue;
		}
	}
	
	public void fitModel(){
		double[] c = new double[order + 1];
		double[] r = new double[order + 1];
		
		double zMean = 0;
		
		//aritmeticka sredina
		final int N = dataset.length;
		if(!differenced) {
			for(int i = 0; i < N; i++){
				zMean += dataset[i];
			}
			zMean /= N;
		}
		
		//autocovariance estimate
		for(int k = 0; k < c.length; k++){
			double autoCov = 0;
			for(int t = 1; t < N - k; t++){
				autoCov = autoCov + (dataset[t] - zMean) * (dataset[t+k] - zMean);
			}
			c[k] = autoCov / N;
		}
		
		//autocorrelation estimate
		for(int k = 0; k < c.length; k++){
			r[k] = c[k] / c[0]; 
		}
		

		
		r = Arrays.copyOfRange(r, 1, r.length);
		Matrix ro = new Matrix(r, r.length);
		
		Matrix P = new Matrix(order, order);
		
		for(int i = 0; i < order; i++){
			for(int j = 0; j < order; j++){
				if(i == j) P.set(i, j, 1);
				else if(j < i) P.set(i, j, r[i - j]);
				else P.set(i, j, r[j - i]);
			}
		}
		
		
		coeff = P.inverse().times(ro);
		this.findErrorTerms();
		this.setNormDistributionParameters();
	}

	private void setNormDistributionParameters(){
		errorMean = new Mean().evaluate(errorTerms);
		errorSigma = new StandardDeviation().evaluate(errorTerms);
		errorSigma /= Math.sqrt(errorTerms.length);
		
	}
	
	public double forecast(int time){
		if(time == 0) return 0;
		for(int j = 0; j < time; j++){
			double newValue = 0;
			for(int i = order - 1; i >= 0; i--){
				newValue = newValue + coeff.get(i, 0) * forecastedValues[forecastedValues.length - i - 1]; 
			}
			newValue += /*new Random().nextGaussian() * sigma*/ + errorMean;
			forecastedValues = Arrays.copyOf(forecastedValues, 
					forecastedValues.length + 1);
			forecastedValues[forecastedValues.length - 1] = newValue;
		}
		double returnValue = forecastedValues[forecastedValues.length - 1];
		forecastedValues = dataset.clone();
		return returnValue;
	}
	
	
	public double[] coeffs(){
		return coeff.getColumnPackedCopy();
	}
	
	public double getAIC(){
		double minusTwoTimesLogLikelihood;
		double AIC = 0;
		double rss = 0;
		int n = dataset.length;
		int k = order + 2;
		
		double[] residuals = new double[errorTerms.length];
		for (int i = 0; i < residuals.length; i++) {
			residuals[i] = errorTerms[i] - errorMean;
		}
		
		for (double d : residuals) {
			rss += d*d;
		}
		rss /= n;
		minusTwoTimesLogLikelihood = 
				n * (Math.log(2 * Math.PI) + Math.log(rss) + 1);
		AIC = 2 * k + minusTwoTimesLogLikelihood;
		return AIC;
	}
	
	public boolean checkIfStationary() {
		Matrix minusCoeff = coeff.times(-1);
		return InvertibilityCheck.check(minusCoeff.getRowPackedCopy());
	}
}
