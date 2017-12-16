package hr.fer.zemris.project.forecasting.models.arma;

import Jama.Matrix;

public interface IHFunction extends IFunction {

    public Matrix getHessianMatrix(Matrix vector);
}
