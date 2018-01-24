package hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.DoubleBinary;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;

import java.util.function.DoubleBinaryOperator;

public class PowGenerator implements IDoubleBinaryOperatorGenerator {
    @Override public DoubleBinaryOperator getDoubleBinaryOperator() {
        return new Pow();
    }

    private static class Pow extends DoubleBinary {

        @Override public double applyAsDouble(double left, double right) {
            return Math.pow(left, right);
        }

        @Override
        public String toString() {
            return "^";
        }
    }
}
