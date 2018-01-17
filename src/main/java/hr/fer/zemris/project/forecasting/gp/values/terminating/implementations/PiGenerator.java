package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;

import java.util.function.Function;

public class PiGenerator implements ITerminatorGenerator {
    @Override public Function<double[], Double> getRandomTerminator() {
        return new Pi();
    }

    public static class Pi extends Terminator {
        @Override public Double apply(double[] doubles) {
            return Math.PI;
        }

        @Override
        public String toString() {
            return "PI";
        }

    }

}
