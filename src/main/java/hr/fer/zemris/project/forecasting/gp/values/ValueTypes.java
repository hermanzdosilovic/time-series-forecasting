package hr.fer.zemris.project.forecasting.gp.values;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations.*;
import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.implementations.InputGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.implementations.MeanGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.implementations.RandomIntGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.implementations.RealNumberGenerator;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class ValueTypes {

//    private static final double PERCENTAGE_TO_GET_BI_FUNCTIONS = 0.5;

    private static final int INT_BOUND = 10;
    private int offset;
    private Random rand = new Random();
    private List<ITerminatorGenerator>           TERMINATING_GENERATORS;
    private List<IDoubleBinaryOperatorGenerator> BI_FUNCTIONS_GENERATORS;
    private List<IDoubleUnaryOperatorGenerator>  UNI_FUNCTIONS_GENERATORS;

    public ValueTypes(int offset) {
        this.offset = offset;
        TERMINATING_GENERATORS = setTerminatingGenerator();
        BI_FUNCTIONS_GENERATORS = setBiFunctionGenerators();
        UNI_FUNCTIONS_GENERATORS = setUniFunctionGenerators();
    }

    private List<ITerminatorGenerator> setTerminatingGenerator() {
        return Arrays.asList(
            new InputGenerator(offset, rand),
            new RealNumberGenerator(rand),
            new MeanGenerator(),
            new RandomIntGenerator(rand, INT_BOUND)
//        new ARGenerator(random)
//        new MaxInputGenerator(),
//        new MinInputGenerator(),
//        new PiGenerator(),
//        new EulerGenerator()
        );
    }

    private List<IDoubleBinaryOperatorGenerator> setBiFunctionGenerators() {
        return Arrays.asList(
            new AddGenerator(),
            new SubGenerator(),
            new MultiplyGenerator(),
            new PowGenerator(),
            new DivGenerator()
//        new MaxGenerator(),
//        new MinGenerator()
        );
    }

    private List<IDoubleUnaryOperatorGenerator> setUniFunctionGenerators() {
        return Arrays.asList(
            new SinGenerator(),
            new LogGenerator(),
            new CosGenerator(),
            new ExpGenerator(),
            new SqrtGenerator(),
            new TgGenerator()
//        new TanHGenerator(),
//        new ReciprocalGenerator(),
//        new SinGenerator(),
//        new BinaryStepGenerator()
        );
    }

    public Function<double[], Double> getRandomTerminating() {
        return TERMINATING_GENERATORS.get(rand.nextInt(TERMINATING_GENERATORS.size())).getRandomTerminator();
    }

    public DoubleBinaryOperator getRandomBiFunction() {
        return BI_FUNCTIONS_GENERATORS.get(rand.nextInt(BI_FUNCTIONS_GENERATORS.size())).getDoubleBinaryOperator();
    }

    public DoubleUnaryOperator getRandomUniFunction() {
        return UNI_FUNCTIONS_GENERATORS.get(rand.nextInt(UNI_FUNCTIONS_GENERATORS.size())).getDoubleUnaryOperator();
    }

}
