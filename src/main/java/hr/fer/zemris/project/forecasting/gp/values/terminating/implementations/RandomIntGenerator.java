package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;

import java.util.Random;
import java.util.function.Function;

public class RandomIntGenerator implements ITerminatorGenerator {

    private Random random;

    private int bound;

    public RandomIntGenerator(Random random, int bound) {
        this.random = random;
        this.bound = bound;
    }

    @Override public Function<double[], Double> getRandomTerminator() {
        return new RandomInt(random.nextInt(bound));
    }

    private static class RandomInt extends Terminator {
        private int number;

        private RandomInt(int number) {
            this.number = number;
        }

        @Override public Double apply(double[] doubles) {
            return (double) number;
        }

        @Override public String toString() {
            return String.format("%d", number);
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            RandomInt randomInt = (RandomInt) o;

            return number == randomInt.number;
        }

        @Override public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + number;
            return result;
        }
    }
}
