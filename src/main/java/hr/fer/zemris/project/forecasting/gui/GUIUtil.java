package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    public static void showErrorMessage(String message, Data data) {
        Platform.runLater(() -> {
            Stage notInvertible = new Stage();
            notInvertible.initOwner(data.getPrimaryStage());
            notInvertible.initModality(Modality.WINDOW_MODAL);

            Label l = new Label(message);

            l.setPadding(new Insets(30, 30, 30, 30));

            Scene scene = new Scene(l);
            notInvertible.setScene(scene);
            notInvertible.show();
        });
    }
}
