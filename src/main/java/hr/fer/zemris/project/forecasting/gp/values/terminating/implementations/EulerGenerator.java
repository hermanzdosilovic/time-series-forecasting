package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;

import java.util.function.Function;

public class EulerGenerator implements ITerminatorGenerator {
    @Override
    public Function<double[], Double> getRandomTerminator() {
        return new Euler();
    }

    private static class Euler extends Terminator {
        @Override
        public Double apply(double[] doubles) {
            return Math.E;
        }

        @Override
        public String toString() {
            return "E";
        }
    }
}
