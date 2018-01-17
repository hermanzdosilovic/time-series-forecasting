package hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.DoubleUnary;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;

import java.util.function.DoubleUnaryOperator;

public class LogGenerator implements IDoubleUnaryOperatorGenerator {
    @Override public DoubleUnaryOperator getDoubleUnaryOperator() {
        return new Log();
    }

    private static class Log extends DoubleUnary {
        @Override public double applyAsDouble(double operand) {
            return Math.log(operand);
        }

        @Override
        public String toString() {
            return "log";
        }
    }
}
