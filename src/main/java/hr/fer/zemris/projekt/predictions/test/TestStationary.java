package hr.fer.zemris.projekt.predictions.test;

import hr.fer.zemris.projekt.predictions.models.ModelUtil;
import hr.fer.zemris.projekt.predictions.util.Graph;
import hr.fer.zemris.projekt.predictions.util.Util;
import hr.fer.zemris.projekt.predictions.util.UtilException;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestStationary extends JFrame {

    public TestStationary() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TestStationary");
        setSize(640, 480);

        initGUI();
    }

    private void initGUI() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        Path path = Paths.get("./src/main/resources/exchange-rate-twi-may-1970-aug-1.csv");

        JPanel panel;
        try {
            Graph graph = new Graph();

            List<Double> ex_rate = Util.parseDataset(path, 1);
            //graph.addNewData(ex_rate, "Ex rate non stat");

            List<Double> ex_rate_stationary = ModelUtil.stationarize(ex_rate);
//            List<Double> ex_rate_stationary = ModelUtil.differentiate(ex_rate);
//            System.out.println(ModelUtil.checkIfStationary(ex_rate_stationary));
//            List<Double> ex_rate_stationary2 = ModelUtil.differentiate(ex_rate_stationary);
            graph.addNewData(ex_rate_stationary, "Ex rate stat");

            panel = Util.graphAsPanel(graph);
        } catch (UtilException e) {
            cp.add(new JLabel(String.format(
                    "Exception %s happened.",
                    e.getMessage()
            )), BorderLayout.CENTER);
            return;
        }

        cp.add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TestStationary().setVisible(true);
        });
    }
}