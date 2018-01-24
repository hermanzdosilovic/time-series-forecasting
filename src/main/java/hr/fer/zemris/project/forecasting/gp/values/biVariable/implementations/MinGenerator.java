package hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.DoubleBinary;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;

import java.util.function.DoubleBinaryOperator;

public class MinGenerator implements IDoubleBinaryOperatorGenerator {
    @Override public DoubleBinaryOperator getDoubleBinaryOperator() {
        return new Min();
    }

    private static class Min extends DoubleBinary {

        @Override
        public double applyAsDouble(double left, double right) {
            return Math.min(left, right);
        }

        @Override
        public String toString() {
            return "Min";
        }
    }
}
