package hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.DoubleBinary;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;

import java.util.function.DoubleBinaryOperator;

public class MultiplyGenerator implements IDoubleBinaryOperatorGenerator {
    @Override public DoubleBinaryOperator getDoubleBinaryOperator() {
        return new Multiply();
    }

    private static class Multiply extends DoubleBinary {
        @Override
        public double applyAsDouble(double left, double right) {
            return left * right;
        }

        @Override
        public String toString() {
            return "*";
        }
    }
}
