package hr.fer.zemris.project.forecasting.models.arma;

import Jama.Matrix;

public interface IFunction {

    public int getNumberOfVariables();

    public double getValueOfFunction(Matrix vector);

    public Matrix getGradientVector(Matrix vector);
    //	{
    //
    //		Matrix grad = new Matrix(vector.getRowDimension(), 1);
    //		for(int i = 0; i < grad.getRowDimension(); i++){
    //			double epsilon = NumOptAlgorithms.getEpsilon();
    //			Matrix novi = vector.copy();
    //			novi.set(i, 0, vector.get(i, 0) + epsilon);
    //			double diff =
    //					(this.getValueOfFunction(novi) -
    //							this.getValueOfFunction(vector)) / epsilon;
    //			grad.set(i, 0, diff);
    //		}
    //
    //		return grad;
    //	};
    //
}
