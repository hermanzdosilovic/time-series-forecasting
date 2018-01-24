package hr.fer.zemris.project.forecasting.gp.values.terminating.implementations;

import hr.fer.zemris.project.forecasting.gp.Main;
import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.Terminator;
import hr.fer.zemris.project.forecasting.models.AR;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;

import java.util.Random;
import java.util.function.Function;

public class ARGenerator implements ITerminatorGenerator {

    private Random random;

    public ARGenerator(Random random) {
        this.random = random;
    }

    @Override public Function<double[], Double> getRandomTerminator() {
        int p = random.nextInt(Main.OFFSET) + 1;
        return new myAR(p);
    }

    private static class myAR extends Terminator {

        private int p;

        private myAR(int p) {
            this.p = p;
        }

        @Override
        public Double apply(double[] array) {
            AR ar = new AR(p, ArraysUtil.toList(array));
            return ar.computeNextValue();
        }

        @Override
        public String toString() {
            return String.format("AR(%d)", p);
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

            myAR myAR = (myAR) o;

            return p == myAR.p;
        }

        @Override public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + p;
            return result;
        }
    }

}
