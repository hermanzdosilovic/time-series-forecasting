package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.*;

public class GUIUtil {
    public static IActivation extractActivation(String activation) {
        switch (activation) {
            case "Sigmoid":
                return SigmoidActivation.getInstance();
            case "Binary Step":
                return BinaryStepActivation.getInstance();
            case "Identity":
                return IdentityActivation.getInstance();
            case "ReLU":
                return ReLUActivation.getInstance();
            case "TanH":
                return TanHActivation.getInstance();
            default:
                return null;
        }
    }
}
