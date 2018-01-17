package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class CosGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Cos();
    }

    private static class Cos extends DoubleUnary {
        @Override
        public double applyAsDouble(double operand) {
            return Math.cos(operand);
        }

        @Override
        public String toString() {
            return "cos";
        }
    }
}
