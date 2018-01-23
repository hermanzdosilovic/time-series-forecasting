package hr.fer.zemris.project.forecasting.gp;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.gui.IListener;
import hr.fer.zemris.project.forecasting.gp.gui.IObserver;
import hr.fer.zemris.project.forecasting.gp.methods.RampedHalfAndHalf;
import hr.fer.zemris.project.forecasting.gp.reproduction.Crossover;
import hr.fer.zemris.project.forecasting.gp.reproduction.Mutation;
import hr.fer.zemris.project.forecasting.gp.reproduction.Replication;
import hr.fer.zemris.project.forecasting.gp.selections.ISelection;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class GeneticProgramming implements IObserver<BinaryTree> {

    private int                    maxIter;
    private int                    populationSize;
    private double                 goodEnoughFitness;
    private int                    startDepth;
    private double                 replicationPercentage;
    private double                 mutationPercentage;
    private ISelection<BinaryTree> selection;
    private Replication            replication;
    private Mutation               mutation;
    private Crossover              crossover;
    private int                    maxDepth;
    private int                    maxNodes;
    private boolean                acceptDuplicates;
    private boolean                elitism;
    private Random                 random;
    private int                    offset;
    private List<DatasetEntry>     trainSet;
    private List<DatasetEntry>     testSet;
    private BinaryTree             bestSolution;
    private boolean                stop;
    private List<IListener<BinaryTree>> listeners = new ArrayList<>();
    private ValueTypes valueTypes;

    public GeneticProgramming(
        int maxIter,
        int populationSize,
        double goodEnoughFitness,
        int startDepth,
        double replicationPercentage,
        double mutationPercentage,
        ISelection<BinaryTree> selection,
        int maxDepth,
        int maxNodes,
        boolean acceptDuplicates,
        boolean elitism,
        int offset,
        List<DatasetEntry> trainSet,
        List<DatasetEntry> testSet
    ) {
        this.maxIter = maxIter;
        this.populationSize = populationSize;
        this.goodEnoughFitness = goodEnoughFitness;
        this.startDepth = startDepth;
        this.replicationPercentage = replicationPercentage;
        this.mutationPercentage = mutationPercentage;
        this.selection = selection;
        this.maxDepth = maxDepth;
        this.maxNodes = maxNodes;
        this.acceptDuplicates = acceptDuplicates;
        this.elitism = elitism;
        this.offset = offset;
        this.trainSet = trainSet;
        this.testSet = testSet;
        valueTypes = new ValueTypes(offset);

        random = new Random();
    }

    public BinaryTree compute(
    ) {
        stop = false;
        replication = new Replication(random, trainSet, testSet, valueTypes);
        mutation = new Mutation(random, trainSet, testSet, valueTypes, maxNodes, maxDepth);
        crossover = new Crossover(random, trainSet, testSet, valueTypes, maxNodes, maxDepth);

        BinaryTree[] population = RampedHalfAndHalf.getPopulation(
            populationSize,
            startDepth,
            valueTypes
        );
        evaluate(population, trainSet, testSet);

        for (int i = 0; i < maxIter; i++) {
            bestSolution = BinaryTree.findBest(population, BinaryTree.getTrainFitness);
            handleNewBest(bestSolution, i);
//            BinaryTree bestTest  = BinaryTree.findBest(population, BinaryTree.getTestFitness);
            System.err.printf(
                "Iter %d, bestTrain train fitness %.2f, test fitness %.2f %n",
                i,
                bestSolution.getTrainFitness(),
                bestSolution.getTestFitness()
            );
//            System.err.printf(
//                "Iter %d, bestTest train fitness %.2f, test fitness %.2f %n",
//                i,
//                bestTest.getTrainFitness(),
//                bestTest.getTestFitness()
//            );
            if (bestSolution.getTrainFitness() >= goodEnoughFitness || stop) {
                return bestSolution;
            }

            population = getNewGeneration(population, bestSolution);
        }
        return BinaryTree.findBest(population, BinaryTree.getTrainFitness);
    }

    private void handleNewBest(BinaryTree bestSolution, int i) {
        listeners.forEach(l -> l.newBest(bestSolution, i));
    }

    private BinaryTree[] getNewGeneration(BinaryTree[] population, BinaryTree best) {
        BinaryTree[] children     = new BinaryTree[population.length];
        int          currentIndex = 0;

        if (elitism) {
            children[0] = best;
            currentIndex++;
        }

        while (true) {
            BinaryTree[] childrenCandidates = getRandomChildren(population);
            for (BinaryTree childrenCandidate : childrenCandidates) {
                if (currentIndex >= children.length) {
                    return children;
                }

                if ((!acceptDuplicates && !ArrayUtils.contains(children, childrenCandidate)) || acceptDuplicates) {
                    children[currentIndex++] = childrenCandidate;
                }
            }
        }
    }

    private BinaryTree[] getRandomChildren(BinaryTree[] population) {
        double chance = random.nextDouble();
        if (chance <= replicationPercentage) {
            return replication.getChildren(population, selection);
        }
        if (chance <= mutationPercentage + replicationPercentage) {
            return mutation.getChildren(population, selection);
        }
        return crossover.getChildren(population, selection);
    }


    private void evaluate(BinaryTree[] population, List<DatasetEntry> trainEntries, List<DatasetEntry> testEntries) {
        for (BinaryTree tree : population) {
            tree.evaluate(trainEntries, testEntries);
        }
    }

    @Override public BinaryTree getBestSolution() {
        return bestSolution;
    }

    @Override public BinaryTree start() {
        return compute();
    }

    @Override public void stop() {
        stop = true;
    }

    @Override public void addListener(IListener l) {
        listeners.add(l);
    }

    @Override public void removeListener(IListener l) {
        listeners.remove(l);
    }

    public DatasetEntry returnFirst() {
        return trainSet.get(0);
    }

    public Map<String, double[]> getForecastedData(BinaryTree solution) {
        List<DatasetEntry> trainAndTest = new ArrayList<>(trainSet);
        trainAndTest.addAll(testSet);

        double[] forecastedOutput = new double[trainAndTest.size()];
        double[] actualOutput     = new double[trainAndTest.size()];

        solution.forward(forecastedOutput, actualOutput, trainAndTest);
        HashMap<String, double[]> map = new HashMap<>();
        map.put("Expected", actualOutput);
        map.put("Forecasted", forecastedOutput);
        return map;
    }

    public double[] predictNextValues(int numberOfValues) {
        return bestSolution.predictNextValues(testSet.get(testSet.size() - 1), numberOfValues);
    }

//     debugging purposes
//    private void checkForNodesSize(BinaryTree[] population) {
//        for (int i = 0; i < population.length; i++) {
//            if (population[i].getNodesSize() > maxNodes) {
//                System.out.println(i);
//            }
//        }
//    }
}
