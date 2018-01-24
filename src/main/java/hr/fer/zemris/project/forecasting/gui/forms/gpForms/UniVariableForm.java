package hr.fer.zemris.project.forecasting.gui.forms.gpForms;

import hr.fer.zemris.project.forecasting.gp.values.uniVariable.IDoubleUnaryOperatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.uniVariable.implementations.*;

import java.util.ArrayList;
import java.util.List;

public class UniVariableForm {

    private boolean sin        = true;
    private boolean log        = true;
    private boolean cos        = true;
    private boolean exp        = true;
    private boolean sqrt       = true;
    private boolean tg         = true;
    private boolean tanH       = false;
    private boolean reciprocal = false;
    private boolean binaryStep = false;
    private boolean sigmoid    = false;

    private static UniVariableForm instance = new UniVariableForm();

    public static UniVariableForm getInstance() {
        return instance;
    }

    public boolean isSin() {
        return sin;
    }

    public void setSin(boolean sin) {
        this.sin = sin;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isCos() {
        return cos;
    }

    public void setCos(boolean cos) {
        this.cos = cos;
    }

    public boolean isExp() {
        return exp;
    }

    public void setExp(boolean exp) {
        this.exp = exp;
    }

    public boolean isSqrt() {
        return sqrt;
    }

    public void setSqrt(boolean sqrt) {
        this.sqrt = sqrt;
    }

    public boolean isTg() {
        return tg;
    }

    public void setTg(boolean tg) {
        this.tg = tg;
    }

    public boolean isTanH() {
        return tanH;
    }

    public void setTanH(boolean tanH) {
        this.tanH = tanH;
    }

    public boolean isReciprocal() {
        return reciprocal;
    }

    public void setReciprocal(boolean reciprocal) {
        this.reciprocal = reciprocal;
    }

    public boolean isBinaryStep() {
        return binaryStep;
    }

    public void setBinaryStep(boolean binaryStep) {
        this.binaryStep = binaryStep;
    }

    public boolean isSigmoid() {
        return sigmoid;
    }

    public void setSigmoid(boolean sigmoid) {
        this.sigmoid = sigmoid;
    }

    public List<IDoubleUnaryOperatorGenerator> getAsList() {
        List<IDoubleUnaryOperatorGenerator> result = new ArrayList<>();

        if (sin) {
            result.add(new SinGenerator());
        }
        if (log) {
            result.add(new LogGenerator());
        }
        if (cos) {
            result.add(new CosGenerator());
        }
        if (exp) {
            result.add(new ExpGenerator());
        }
        if (sqrt) {
            result.add(new SqrtGenerator());
        }
        if (tg) {
            result.add(new TgGenerator());
        }
        if (tanH) {
            result.add(new TanHGenerator());
        }
        if (reciprocal) {
            result.add(new ReciprocalGenerator());
        }
        if (binaryStep) {
            result.add(new BinaryStepGenerator());
        }
        if (sigmoid) {
            result.add(new SigmoidGenerator());
        }

        return result;
    }
}
