package hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.DoubleBinary;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;

import java.util.function.DoubleBinaryOperator;

public class AddGenerator implements IDoubleBinaryOperatorGenerator {
    @Override public DoubleBinaryOperator getDoubleBinaryOperator() {
        return new Add();
    }

    private static class Add extends DoubleBinary {

        @Override public double applyAsDouble(double left, double right) {
            return left + right;
        }

        @Override
        public String toString() {
            return "+";
        }
    }
}
