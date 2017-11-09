package hr.fer.zemris.projekt.predictions.test;

import hr.fer.zemris.projekt.predictions.util.Graph;
import hr.fer.zemris.projekt.predictions.util.Util;
import hr.fer.zemris.projekt.predictions.util.UtilException;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestPlotGraphs extends JFrame {

    public TestPlotGraphs() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TestPlotGraphs");
        setSize(640, 480);

        initGUI();
    }

    private void initGUI() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        Path path1 = Paths.get("./src/main/resources/exchange-rate-twi-may-1970-aug-1.csv");
        Path path2 = Paths.get("./src/main/resources/monthly-milk-production-pounds-p.csv");
        JPanel panel;
        try {
            Graph graph = new Graph();

            List<Double> ex_rate = Util.parseDataset(path1, 1);
            graph.addNewData(ex_rate, "Ex rate");

            List<Double> milk_prod = Util.parseDataset(path2, 1);
            graph.addNewData(milk_prod, "Milk prod");

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
            new TestPlotGraphs().setVisible(true);
        });
    }
}
