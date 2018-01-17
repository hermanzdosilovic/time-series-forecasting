package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class TanHGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new TanH();
    }

    private static class TanH extends DoubleUnary {
        @Override public double applyAsDouble(double operand) {
            return 2.0 / (1.0 + Math.exp(-2.0 * operand)) - 1.0;
        }

        @Override
        public String toString() {
            return "TanH";
        }
    }
}
