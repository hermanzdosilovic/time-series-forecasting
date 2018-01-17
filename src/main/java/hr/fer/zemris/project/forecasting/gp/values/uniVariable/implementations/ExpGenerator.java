package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class ExpGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Exp();
    }

    private static class Exp extends DoubleUnary {
        @Override
        public double applyAsDouble(double operand) {
            return Math.exp(operand);
        }

        @Override
        public String toString() {
            return "exp";
        }
    }
}
