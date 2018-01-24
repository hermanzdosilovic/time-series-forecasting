package hr.fer.zemris.project.forecasting.gui.forms.annForms;

public class NeuralNetworkForm {

    private String type  = "TDNN";
    private int percentage = 90;
    private String inputLayer = "5";
    private String hiddenLayers = "10";
    private String outputLayer = "1";
    private String inputLayerActivation = "Identity";
    private String hiddenLayersActivation = "ReLU";
    private String outputLayerActivation = "Identity";


    private static NeuralNetworkForm ourInstance = new NeuralNetworkForm();

    private NeuralNetworkForm() {
    }

    public static NeuralNetworkForm getInstance() {
        return ourInstance;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getType() {
        return type;
    }
}
