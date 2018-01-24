package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;

import java.util.Random;
import java.util.function.Function;

public class RealNumberGenerator implements ITerminatorGenerator {

    private Random random;

    public RealNumberGenerator(Random random) {
        this.random = random;
    }

    @Override
    public String toString() {
        return "R";
    }

    @Override
    public Function<double[], Double> getRandomTerminator() {
        double number = random.nextDouble() * 2 - 1;
        return new RealNumber(number);
    }

    private static class RealNumber extends Terminator {
        private double number;

        private RealNumber(double number) {
            this.number = number;
        }

        @Override
        public Double apply(double[] array) {
            return number;
        }

        @Override
        public String toString() {
            return String.format("%f", number);
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

            RealNumber that = (RealNumber) o;

            return Double.compare(that.number, number) == 0;
        }

        @Override public int hashCode() {
            int  result = super.hashCode();
            long temp;
            temp = Double.doubleToLongBits(number);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
