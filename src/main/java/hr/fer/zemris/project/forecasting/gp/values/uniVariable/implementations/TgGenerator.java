package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class TgGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Tg();
    }

    private static class Tg extends DoubleUnary {
        @Override
        public double applyAsDouble(double operand) {
            return Math.tan(operand);
        }

        @Override
        public String toString() {
            return "tg";
        }
    }
}
