package hr.fer.zemris.project.forecasting.gp.tree.nodes;

import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.tree.Node;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.FiveParametarInterface;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.FourParametarInterface;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.ThreeParametarInterface;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TerminalNode extends Node {

    private Function<double[], Double> value;

    public TerminalNode(int totalDepth, int depth, Node parent, Function<double[], Double> value) {
        super(totalDepth, depth, parent);
        this.value = value;
    }

    @Override
    public void setNewDepth(int depth) {
        setDepth(depth);
    }

    @Override public TreeItem<String> asTreeItem(boolean expand) {
        TreeItem<String> node = new TreeItem<>(value.toString() + String.format(" (depth: %d)", getDepth()), BinaryTree.LEAF_ICON.get());
        node.setExpanded(expand);
        return node;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>();
    }

    @Override public void addChildren(
        Random random,
        FiveParametarInterface<Integer, Integer, Random, Node, ValueTypes, Node> getNode,
        ThreeParametarInterface<Node, Random, ValueTypes> buildMethod,
        ValueTypes valueTypes
    ) {

    }

    @Override
    public Node newLikeThis(Node parent) {
        return new TerminalNode(
            getTotalDepth(),
            getDepth(),
            parent,
            value
        );
    }

    @Override public void replaceChild(Node toBeReplaced, Node replacement) {
    }

    @Override
    public double getOutput(double[] input) {
        return value.apply(input);
    }

    @Override
    public String toString() {
        return asStringJoiner(value).toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TerminalNode that = (TerminalNode) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
