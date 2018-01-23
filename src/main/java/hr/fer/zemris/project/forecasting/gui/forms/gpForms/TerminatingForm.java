package hr.fer.zemris.project.forecasting.gui.forms.gpForms;

import hr.fer.zemris.project.forecasting.gp.values.terminating.ITerminatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.terminating.implementations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerminatingForm {

    private boolean input      = true;
    private boolean realNumber = true;
    private boolean mean       = true;
    private boolean randomInt  = true;
    private boolean ar         = false;
    private boolean maxInput   = false;
    private boolean minInput   = false;
    private boolean pi         = false;
    private boolean euler      = false;

    private static TerminatingForm instance = new TerminatingForm();

    public static TerminatingForm getInstance() {
        return instance;
    }

    private Random random = new Random();
    private Integer INT_BOUND = 10;

    public List<ITerminatorGenerator> getAsList(int offset) {
        List<ITerminatorGenerator> result = new ArrayList<>();

        if (input) {
            result.add(new InputGenerator(offset, random));
        }
        if (realNumber) {
            result.add(new RealNumberGenerator(random));
        }
        if (mean) {
            result.add(new MeanGenerator());
        }
        if (randomInt) {
            result.add(new RandomIntGenerator(random, INT_BOUND));
        }
        if (ar) {
            result.add(new ARGenerator(random));
        }
        if (maxInput) {
            result.add(new MaxInputGenerator());
        }
        if (minInput) {
            result.add(new MinInputGenerator());
        }
        if (pi) {
            result.add(new PiGenerator());
        }
        if (euler) {
            result.add(new EulerGenerator());
        }

        return result;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    public boolean isRealNumber() {
        return realNumber;
    }

    public void setRealNumber(boolean realNumber) {
        this.realNumber = realNumber;
    }

    public boolean isMean() {
        return mean;
    }

    public void setMean(boolean mean) {
        this.mean = mean;
    }

    public boolean isRandomInt() {
        return randomInt;
    }

    public void setRandomInt(boolean randomInt) {
        this.randomInt = randomInt;
    }

    public boolean isAr() {
        return ar;
    }

    public void setAr(boolean ar) {
        this.ar = ar;
    }

    public boolean isMaxInput() {
        return maxInput;
    }

    public void setMaxInput(boolean maxInput) {
        this.maxInput = maxInput;
    }

    public boolean isMinInput() {
        return minInput;
    }

    public void setMinInput(boolean minInput) {
        this.minInput = minInput;
    }

    public boolean isPi() {
        return pi;
    }

    public void setPi(boolean pi) {
        this.pi = pi;
    }

    public boolean isEuler() {
        return euler;
    }

    public void setEuler(boolean euler) {
        this.euler = euler;
    }
}
