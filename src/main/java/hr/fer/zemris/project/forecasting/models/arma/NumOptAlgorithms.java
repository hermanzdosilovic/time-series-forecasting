package hr.fer.zemris.project.forecasting.models.arma;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

public class NumOptAlgorithms {
    /**
     * Set tolerance when comparing double variables.
     */
    private static final double epsilon = 1e-4;
    private static List<Double> x1;
    private static List<Double> x2;
    /**
     * Starting point for all algorithms in this class
     */
    private static Matrix initialVector;

    public static double getEpsilon() {
        return epsilon;
    }

    /**
     * Sets the starting vector for algorithms in this class
     *
     * @param vector
     */
    public static void setInitialVector(double[] vector) {
        initialVector = new Matrix(vector, vector.length);
    }

    /**
     * @param lambda
     * @param function
     * @param x
     * @param d
     * @return numeric derivative of function(x + lambda*d) over lambda
     */

    private static double lambdaDerivative(double lambda, IFunction function, Matrix x, Matrix d) {

        return function.getGradientVector(x.plus(d.times(lambda))).
            transpose().times(d).get(0, 0);
    }

    /**
     * Uses bisection method to approximate the optimal step for numeric
     * optimization.
     *
     * @param function
     * @param x
     * @param d
     * @return approximation of an optimal step in numeric optimization
     */
    private static double bisection(IFunction function, Matrix x, Matrix d) {

        double lower = 0;
        double upper = 1;


        double derivative;
        do {
            derivative = lambdaDerivative(upper, function, x, d);
            if (derivative > 0)
                break;
            upper *= 2;

        } while (true);
        double lambda = 0;
        do {
            lambda = (lower + upper) / 2;
            derivative = lambdaDerivative(lambda, function, x, d);
            if (derivative > 0)
                upper = lambda;
            else
                lower = lambda;
        } while (Math.abs(derivative) > epsilon);
        return lambda;
    }

    /**
     * @param function
     * @param x
     * @return true if x is optimal for the given function
     */

    private static boolean isOptimal(IFunction function, Matrix x) {
        for (int i = 0; i < x.getRowDimension(); i++) {
            if (Math.abs(function.getGradientVector(x).get(i, 0)) > epsilon)
                return false;
        }

        return true;
    }

    /**
     * Initializes lists for storing former values of x for functions of 2 variables
     */
    private static void initializeFormer(Matrix x) {
        if (x.getRowDimension() == 2) {
            x1 = new ArrayList<>();
            x2 = new ArrayList<>();
        }
    }

    /**
     * Adds values to lists of former variables for functions of 2 variables.
     */

    private static void addFormer(Matrix x) {
        if (x.getRowDimension() == 2) {
            x1.add(x.get(0, 0));
            x2.add(x.get(1, 0));
        }
    }


    /**
     * Returns optimum approximation using gradient descent method.
     *
     * @param function
     * @param numberOfIterations
     * @return
     */
    public static Matrix gradientDescent(IFunction function, int numberOfIterations) {
        Matrix x = initialVector.copy();

        initializeFormer(x);
        for (int k = 0; k < numberOfIterations; k++) {
            //			System.out.println(k);
            addFormer(x);
            if (isOptimal(function, x))
                return x;
            else {

                Matrix d = function.getGradientVector(x).copy().uminus();

                double lambda = bisection(function, x, d);
                x.plusEquals(d.times(lambda));
            }
        }
        return x;
    }

    /**
     * Returns optimum approximation using Newton's method.
     *
     * @param function
     * @param numberOfIterations
     * @return
     */
    public static Matrix newtonMethod(IHFunction function, int numberOfIterations) {
        Matrix x = initialVector.copy();
        initializeFormer(x);
        for (int k = 0; k < numberOfIterations; k++) {

            addFormer(x);
            if (isOptimal(function, x))
                return x;

            Matrix d = function.getHessianMatrix((x)).inverse().uminus()
                .times(function.getGradientVector(x));
            double lambda = bisection(function, x, d);
            x.plusEquals(d.times(lambda));
        }
        return x;
    }

}

