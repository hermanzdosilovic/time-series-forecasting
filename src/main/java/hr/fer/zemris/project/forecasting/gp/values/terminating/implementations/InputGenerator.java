package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;

import java.util.Random;
import java.util.function.Function;

public class InputGenerator implements ITerminatorGenerator {

    private int offset;

    private Random random;

    public InputGenerator(int offset, Random random) {
        this.offset = offset;
        this.random = random;
    }

    @Override
    public Function<double[], Double> getRandomTerminator() {
        int index = random.nextInt(offset);
        return new Input(index);
    }


    private static class Input extends Terminator {

        private int index;

        private Input(int index) {
            this.index = index;
        }

        @Override
        public Double apply(double[] array) {
            return array[index];
        }

        @Override
        public String toString() {
            return String.format("X%d", index);
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

            Input input = (Input) o;

            return index == input.index;
        }

        @Override public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + index;
            return result;
        }
    }
}
