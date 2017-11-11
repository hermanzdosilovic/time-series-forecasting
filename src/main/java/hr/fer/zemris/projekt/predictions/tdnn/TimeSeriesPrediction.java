package hr.fer.zemris.projekt.predictions.tdnn;

public class TimeSeriesPrediction {

    public static void main(String[] args) {

        INeuralNetwork tdnn = new TDNN(5, 5, 4, 3);
        IErrorCalculator errorCalculator = new ErrorCalculator();
        INeuralNetworkTrainer trainer = new INeuralNetworkTrainer() {
            @Override
            public void trainNeuralNetwork(INeuralNetwork neuralNetwork, IErrorCalculator errorCalculator) {
                //TODO
            }
        };

        trainer.trainNeuralNetwork(tdnn, errorCalculator);
    }

}
