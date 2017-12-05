//package hr.fer.zemris.projekt.predictions.test;
//
//import hr.fer.zemris.project.forecasting.models.Stationary;
//import hr.fer.zemris.projekt.predictions.models.AR;
//import hr.fer.zemris.project.forecasting.util.Util;
//import javax.swing.*;
//import java.awt.*;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//public class TestAR extends JFrame {
//
//    public TestAR() {
//        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        setTitle("TestAR");
//        setSize(640, 480);
//
//        initGUI();
//    }
//
//    private void initGUI() {
//        Container cp = getContentPane();
//        cp.setLayout(new BorderLayout());
//
//        Path path = Paths.get("./src/main/resources/ARDataset.txt");
//        JPanel panel;
//        try {
//            Graph graph = new Graph();
//
//            List<Double> ar_data = Util.parseDataset(path, 3, " ");
//            List<Double> ar_data_stationary = Stationary.stationarize(ar_data);
//            //graph.addNewData(ar_data_stationary, "AR data");
//
//            AR ar = new AR(1, ar_data_stationary);
//            ar.computeNextValues(200);
//            graph.addNewData(ar.getData(), "AR predicted");
//
//            panel = Util.graphAsPanel(graph);
//        } catch (UtilException e) {
//            cp.add(new JLabel(String.format(
//                    "Exception %s happened.",
//                    e.getMessage()
//            )), BorderLayout.CENTER);
//            return;
//        }
//
//        cp.add(panel, BorderLayout.CENTER);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new TestAR().setVisible(true);
//        });
//    }
//}
