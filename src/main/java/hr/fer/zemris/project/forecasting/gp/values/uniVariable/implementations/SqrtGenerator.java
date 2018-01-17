package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class SqrtGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Sqrt();
    }

    private static class Sqrt extends DoubleUnary {
        @Override
        public double applyAsDouble(double operand) {
            return Math.sqrt(operand);
        }

        @Override
        public String toString() {
            return "sqrt";
        }
    }
}
