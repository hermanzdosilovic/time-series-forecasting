package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class SigmoidGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Sigmoid();
    }

    private static class Sigmoid extends DoubleUnary {
        @Override public double applyAsDouble(double operand) {
            return 1 / (1 + Math.exp(-operand));
        }

        @Override
        public String toString() {
            return "Sigmoid";
        }
    }
}
