package hr.fer.zemris.project.forecasting.gui.forms.gpForms;

import hr.fer.zemris.project.forecasting.gp.values.biVariable.IDoubleBinaryOperatorGenerator;
import hr.fer.zemris.project.forecasting.gp.values.biVariable.implementations.*;

import java.util.ArrayList;
import java.util.List;

public class BiVariableForm {

    private boolean add      = true;
    private boolean sub      = true;
    private boolean multiply = true;
    private boolean pow      = true;
    private boolean div      = true;
    private boolean max      = false;
    private boolean min      = false;

    private static BiVariableForm instance = new BiVariableForm();

    public static BiVariableForm getInstance() {
        return instance;
    }

    public List<IDoubleBinaryOperatorGenerator> getAsList() {
        List<IDoubleBinaryOperatorGenerator> result = new ArrayList<>();

        if (add) {
            result.add(new AddGenerator());
        }
        if (sub) {
            result.add(new SubGenerator());
        }
        if (multiply) {
            result.add(new MultiplyGenerator());
        }
        if (pow) {
            result.add(new PowGenerator());
        }
        if (div) {
            result.add(new DivGenerator());
        }
        if (max) {
            result.add(new MaxGenerator());
        }
        if (min) {
            result.add(new MinGenerator());
        }

        return result;
    }

    public boolean isDiv() {
        return div;
    }

    public void setDiv(boolean div) {
        this.div = div;
    }

    public boolean isMax() {
        return max;
    }

    public void setMax(boolean max) {
        this.max = max;
    }

    public boolean isMin() {
        return min;
    }

    public void setMin(boolean min) {
        this.min = min;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isSub() {
        return sub;
    }

    public void setSub(boolean sub) {
        this.sub = sub;
    }

    public boolean isMultiply() {
        return multiply;
    }

    public void setMultiply(boolean multiply) {
        this.multiply = multiply;
    }

    public boolean isPow() {
        return pow;
    }

    public void setPow(boolean pow) {
        this.pow = pow;
    }


}
