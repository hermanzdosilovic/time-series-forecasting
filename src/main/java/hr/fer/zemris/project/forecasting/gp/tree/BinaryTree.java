package hr.fer.zemris.project.forecasting.gp.tree;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.tree.nodes.BiVariableNode;
import hr.fer.zemris.project.forecasting.gp.tree.nodes.TerminalNode;
import hr.fer.zemris.project.forecasting.gp.tree.nodes.UniVariableNode;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;
import hr.fer.zemris.project.forecasting.util.NumericErrorUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class BinaryTree implements Serializable {

    public static final Function<BinaryTree, Double> getTrainFitness = m -> m.getTrainFitness();
    public static final Function<BinaryTree, Double> getTestFitness  = m -> m.getTestFitness();

    public static final Supplier<javafx.scene.Node>
        ROOT_ICON = () -> new ImageView(new Image(BinaryTree.class.getClassLoader().getResourceAsStream("root.png")));
    public static final Supplier<javafx.scene.Node>
        LOG_ICON = () -> new ImageView(new Image(BinaryTree.class.getClassLoader().getResourceAsStream("log.png")));
    public static final Supplier<javafx.scene.Node>
        LEAF_ICON = () -> new ImageView(new Image(BinaryTree.class.getClassLoader().getResourceAsStream("leaf.png")));

    private Node   topNode;
    private double trainFitness;

    private double testFitness;

    private List<Node> nodes;

    private int depth;

    public BinaryTree(Node topNode) {
        this.topNode = topNode;
        setNodes();
        setMaximumDepth();
    }

    public void setMaximumDepth() {
        depth = 1;
        for (Node node : nodes) {
            if (node.getDepth() > depth) {
                depth = node.getDepth();
            }
        }
    }

    public int getDepth() {
        return depth;
    }

    public Node getTopNode() {
        return topNode;
    }

    public static BinaryTree growFactory(int maxDepth, Random random, ValueTypes valueTypes) {
        Node topNode = getRandomFunctionNode(maxDepth, 1, random, null, valueTypes);
        topNode.grow(random, valueTypes);
        return new BinaryTree(topNode);
    }

    public static BinaryTree fullFactory(int maxDepth, Random random, ValueTypes valueTypes) {
        Node topNode = getRandomFunctionNode(maxDepth, 1, random, null, valueTypes);
        topNode.full(random, valueTypes);
        return new BinaryTree(topNode);
    }

    public static Node getRandomFunctionNode(
        int totalDepth,
        int depth,
        Random random,
        Node parent,
        ValueTypes valueTypes
    ) {
        if (random.nextDouble() <= 0.5) {
            return new UniVariableNode(
                totalDepth,
                depth,
                parent,
                valueTypes.getRandomUniFunction()
            );
        }
        return new BiVariableNode(
            totalDepth,
            depth,
            parent,
            valueTypes.getRandomBiFunction()
        );
    }

    public void forward(double[] forecastedOutput, double[] actualOutput, List<DatasetEntry> dataEntries) {
        for (int i = 0; i < forecastedOutput.length; i++) {
            forecastedOutput[i] = getOutput(dataEntries.get(i).getInput());
            actualOutput[i] = dataEntries.get(i).getOutput()[0]; // it's always lentgh of 1 in gp
        }
    }

    public double getOutput(double[] input) {
        return topNode.getOutput(input);
    }

    public int getNodesSize() {
        return nodes.size();
    }

    public static Node getRandomTerminatingNode(
        int totalDepth,
        int depth,
        Random random,
        Node parent,
        ValueTypes valueTypes
    ) {
        return new TerminalNode(
            totalDepth,
            depth,
            parent,
            valueTypes.getRandomTerminating()
        );
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public static Node getRandomTerminatingOrFunctionNode(
        int totalDepth,
        int depth,
        Random random,
        Node parent,
        ValueTypes valueTypes
    ) {
        if (random.nextDouble() <= 0.66) {
            return getRandomFunctionNode(totalDepth, depth, random, parent, valueTypes);
        }
        return getRandomTerminatingNode(totalDepth, depth, random, parent, valueTypes);
    }

    public void setNodes() {
        nodes = new ArrayList<>();
        List<Node> unvisited = new ArrayList<>();
        unvisited.add(topNode);

        while (unvisited.size() > 0) {
            Node node = unvisited.get(0);
            unvisited.remove(0);
            nodes.add(node);
            unvisited.addAll(node.getChildren());
        }
    }

    private static double[] getLastInput(DatasetEntry lastEntry) {
        double[] lastInput = new double[lastEntry.getInput().length];
        for (int i = 0; i < lastInput.length; i++) {
            if (i < lastInput.length - 1) {
                lastInput[i] = lastEntry.getInput()[i + 1];
            } else {
                lastInput[i] = lastEntry.getOutput()[0];
            }
        }
        return lastInput;
    }

    public double[] predictNextValues(DatasetEntry lastEntry, int numberOfPredictions) {
        double[] lastInput   = getLastInput(lastEntry);
        double[] predictions = new double[numberOfPredictions];
        for (int i = 0; i < predictions.length; i++) {
            predictions[i] = getOutput(lastInput);
            moveArrayToLeft(lastInput, predictions[i]);
        }
        return predictions;
    }

    private void moveArrayToLeft(double[] lastEntry, double prediction) {
        for (int i = 0; i < lastEntry.length - 1; i++) {
            lastEntry[i] = lastEntry[i + 1];
        }
        lastEntry[lastEntry.length - 1] = prediction;
    }

    public static BinaryTree randomFactory(int startDepth, int maxDepth, Random random, ValueTypes valueTypes) {
        if (startDepth == maxDepth) {
            return new BinaryTree(
                getRandomTerminatingNode(
                    maxDepth,
                    startDepth,
                    random,
                    null,
                    valueTypes
                ));
        }

        Node topNode = getRandomTerminatingOrFunctionNode(
            maxDepth,
            startDepth,
            random,
            null,
            valueTypes
        );
        topNode.grow(random, valueTypes);
        return new BinaryTree(topNode);
    }


    public static BinaryTree findBest(BinaryTree[] population, Function<BinaryTree, Double> obtainFitness) {
        BinaryTree best = null;
        for (BinaryTree binaryTree : population) {
            if (best == null || obtainFitness.apply(binaryTree) > obtainFitness.apply(best)) {
                best = binaryTree;
            }
        }
        return best;
    }

    public double getTrainFitness() {
        return trainFitness;
    }

    public void evaluate(List<DatasetEntry> train, List<DatasetEntry> test) {
        trainFitness = computeFitness(train);
        testFitness = computeFitness(test);
    }

    private double computeFitness(List<DatasetEntry> dataEntries) {
        double[] forecastedOutput = new double[dataEntries.size()];
        double[] actualOutput     = new double[dataEntries.size()];

        forward(forecastedOutput, actualOutput, dataEntries);
        double result = -NumericErrorUtil.meanSquaredError(actualOutput, forecastedOutput);

        // dijeljenje s nulom i ostale zabranjene stvari
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            result = -Double.MAX_VALUE;
        }
        return result;
    }

    public void replaceNode(int i, Node replacement) {
        if (i == 0) {
            topNode = replacement;
        } else {
            Node parent = nodes.get(i).getParent();
            parent.replaceChild(nodes.get(i), replacement);
            replacement.setParent(parent);
        }
        setNodes();
        setMaximumDepth();
    }

    public BinaryTree newLikeThis() {
        Node       newTopNode = topNode.newLikeThis(null);
        BinaryTree bt         = new BinaryTree(newTopNode);
        bt.setNodes();
        bt.setMaximumDepth();
        bt.trainFitness = trainFitness;
        return bt;
    }

    public TreeView<String> toTreeView(boolean expand) {
        TreeItem<String> root = topNode.asTreeItem(expand);
        root.setGraphic(ROOT_ICON.get());
        return new TreeView<>(root);
    }


    @Override
    public String toString() {
        return topNode.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BinaryTree that = (BinaryTree) o;

        return Double.compare(that.trainFitness, trainFitness) == 0 && topNode.equals(that.topNode);
    }

    @Override
    public int hashCode() {
        int  result;
        long temp;
        result = topNode.hashCode();
        temp = Double.doubleToLongBits(trainFitness);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getTestFitness() {
        return testFitness;
    }

}
