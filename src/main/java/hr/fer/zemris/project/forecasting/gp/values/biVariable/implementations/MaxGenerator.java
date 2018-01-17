package hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.DoubleBinary;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;

import java.util.function.DoubleBinaryOperator;

public class MaxGenerator implements IDoubleBinaryOperatorGenerator {
    @Override public DoubleBinaryOperator getDoubleBinaryOperator() {
        return new Max();
    }

    private static class Max extends DoubleBinary {

        @Override
        public double applyAsDouble(double left, double right) {
            return Math.max(left, right);
        }

        @Override
        public String toString() {
            return "Max";
        }
    }
}
