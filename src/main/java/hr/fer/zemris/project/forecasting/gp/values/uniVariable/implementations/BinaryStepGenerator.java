package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class BinaryStepGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new BinaryStep();
    }

    private static class BinaryStep extends DoubleUnary {
        @Override public double applyAsDouble(double operand) {
            return operand < 0 ? 0 : 1;
        }

        @Override
        public String toString() {
            return "BinaryStep";
        }
    }
}
