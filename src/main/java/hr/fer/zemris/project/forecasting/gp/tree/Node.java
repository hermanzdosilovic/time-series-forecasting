package hr.fer.zemris.project.forecasting.gp.tree;

import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.FiveParametarInterface;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.FourParametarInterface;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.ThreeParametarInterface;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;
import javafx.scene.control.TreeItem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class Node implements Serializable {

    private int totalDepth;

    private int depth;

    private Node parent;

    public Node(int totalDepth, int depth, Node parent) {
        this.totalDepth = totalDepth;
        this.depth = depth;
        this.parent = parent;
    }

    public int getDepth() {
        return depth;
    }

    public Node getParent() {
        return parent;
    }

    public abstract void setNewDepth(int depth);

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public abstract TreeItem<String> asTreeItem(boolean expand);

    public abstract DefaultMutableTreeNode asTreeNode();

    public abstract List<Node> getChildren();

    public void grow(Random random, ValueTypes valueTypes) {
        build(
            random,
            BinaryTree::getRandomTerminatingOrFunctionNode,
            BinaryTree::getRandomTerminatingNode,
            Node::grow,
            valueTypes
        );
    }

    public void full(Random random, ValueTypes valueTypes) {
        build(
            random,
            BinaryTree::getRandomFunctionNode,
            BinaryTree::getRandomTerminatingNode,
            Node::full,
            valueTypes
        );
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public abstract void addChildren(
        Random random,
        FiveParametarInterface<Integer, Integer, Random, Node, ValueTypes, Node> getNode,
        ThreeParametarInterface<Node, Random, ValueTypes> buildMethod,
        ValueTypes valueTypes
    );

    public int getTotalDepth() {
        return totalDepth;
    }

    private void build(
        Random random,
        FiveParametarInterface<Integer, Integer, Random, Node, ValueTypes, Node> getNode1,
        FiveParametarInterface<Integer, Integer, Random, Node, ValueTypes, Node> getNode2,
        ThreeParametarInterface<Node, Random, ValueTypes> buildMethod,
        ValueTypes valueTypes
    ) {
        if (depth < totalDepth - 1) {
            addChildren(
                random,
                getNode1,
                buildMethod,
                valueTypes
            );
        }
        if (depth == totalDepth - 1) {
            addChildren(
                random,
                getNode2,
                buildMethod,
                valueTypes
            );
        }
    }

    public abstract Node newLikeThis(Node parent);

    public abstract void replaceChild(Node toBeReplaced, Node replacement);

    public abstract double getOutput(double[] input);

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        return depth == node.depth;
    }

    @Override public int hashCode() {
        return depth;
    }

    protected StringJoiner asStringJoiner(Object value, boolean terminal) {
//        StringJoiner sj = new StringJoiner("\n");
//        sj.add(String.format(
//            "%s%s",
//            String.join("", Collections.nCopies(getDepth() - 1, "\t")),
//            value.toString()
//               )
//        );
//        return sj;
        if (terminal) {
            return new StringJoiner(
                ",",
                value.toString(),
                ""
            );
        }
        return new StringJoiner(
            ",",
            value.toString() + "(",
            ")"
        );
    }
}
