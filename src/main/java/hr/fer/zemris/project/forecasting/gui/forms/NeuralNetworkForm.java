package hr.fer.zemris.project.forecasting.gui.forms;

public class NeuralNetworkForm {

    private int percentage = 90;
    private String inputLayer = "";
    private String hiddenLayers = "";
    private String outputLayer = "";
    private String inputLayerActivation = "Sigmoid";
    private String hiddenLayersActivation = "Sigmoid";
    private String outputLayerActivation = "Sigmoid";


    private static NeuralNetworkForm ourInstance = new NeuralNetworkForm();

    private NeuralNetworkForm() {
    }

    public static NeuralNetworkForm getInstance() {
        return ourInstance;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setInputLayer(String inputLayer) {
        this.inputLayer = inputLayer;
    }

    public void setHiddenLayers(String hiddenLayers) {
        this.hiddenLayers = hiddenLayers;
    }

    public void setOutputLayer(String outputLayer) {
        this.outputLayer = outputLayer;
    }

    public void setInputLayerActivation(String inputLayerActivation) {
        this.inputLayerActivation = inputLayerActivation;
    }

    public void setHiddenLayersActivation(String hiddenLayersActivation) {
        this.hiddenLayersActivation = hiddenLayersActivation;
    }

    public void setOutputLayerActivation(String outputLayerActivation) {
        this.outputLayerActivation = outputLayerActivation;
    }

    public String getInputLayer() {
        return inputLayer;
    }

    public String getHiddenLayers() {
        return hiddenLayers;
    }

    public String getOutputLayer() {
        return outputLayer;
    }

    public String getInputLayerActivation() {
        return inputLayerActivation;
    }

    public String getHiddenLayersActivation() {
        return hiddenLayersActivation;
    }

    public String getOutputLayerActivation() {
        return outputLayerActivation;
    }

    public int getPercentage() {
        return percentage;
    }
}
