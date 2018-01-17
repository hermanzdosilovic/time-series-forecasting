package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;
import hr.fer.zemris.project.forecasting.util.DataUtil;

import java.util.function.Function;

public class MeanGenerator implements ITerminatorGenerator {

    @Override
    public Function<double[], Double> getRandomTerminator() {
        return new Mean();
    }

    private static class Mean extends Terminator {

        @Override
        public Double apply(double[] array) {
            return DataUtil.getMean(array);
        }

        @Override
        public String toString() {
            return "Mean";
        }
    }
}
