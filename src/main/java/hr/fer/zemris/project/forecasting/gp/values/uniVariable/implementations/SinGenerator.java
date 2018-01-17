package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class SinGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Sin();
    }

    private static class Sin extends DoubleUnary {
        @Override public double applyAsDouble(double operand) {
            return Math.sin(operand);
        }

        @Override
        public String toString() {
            return "sin";
        }
    }
}
