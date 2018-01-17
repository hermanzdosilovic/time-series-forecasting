package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;

import java.util.function.Function;

public class MinInputGenerator implements ITerminatorGenerator {
    @Override public Function<double[], Double> getRandomTerminator() {
        return new MinInput();
    }

    private static class MinInput extends Terminator {

        @Override
        public Double apply(double[] array) {
            return ArraysUtil.min(array);
        }

        @Override
        public String toString() {
            return "MinInput";
        }
    }
}
