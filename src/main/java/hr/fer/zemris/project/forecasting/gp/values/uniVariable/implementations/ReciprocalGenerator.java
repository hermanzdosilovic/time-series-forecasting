package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class ReciprocalGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Reciprocal();
    }

    private static class Reciprocal extends DoubleUnary {
        @Override public double applyAsDouble(double operand) {
            return 1 / operand;
        }

        @Override
        public String toString() {
            return "1/";
        }
    }
}
