package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;

import java.util.function.Function;

public class MaxInputGenerator implements ITerminatorGenerator {
    @Override public Function<double[], Double> getRandomTerminator() {
        return new MaxInput();
    }

    private static class MaxInput extends Terminator {

        @Override
        public Double apply(double[] array) {
            return ArraysUtil.max(array);
        }

        @Override
        public String toString() {
            return "MaxInput";
        }
    }
}
