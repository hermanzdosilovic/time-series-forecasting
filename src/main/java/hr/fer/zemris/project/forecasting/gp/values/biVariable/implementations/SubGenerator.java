package hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.DoubleBinary;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;

import java.util.function.DoubleBinaryOperator;

public class SubGenerator implements IDoubleBinaryOperatorGenerator {
    @Override public DoubleBinaryOperator getDoubleBinaryOperator() {
        return new Sub();
    }

    private static class Sub extends DoubleBinary {

        @Override
        public double applyAsDouble(double left, double right) {
            return left - right;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

}
