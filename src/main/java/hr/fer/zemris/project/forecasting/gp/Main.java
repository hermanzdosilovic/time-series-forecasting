package hr.fer.zemris.project.forecasting.gp;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.selections.ISelection;
import hr.fer.zemris.project.forecasting.gp.selections.Tournament;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.Pair;

import java.io.IOException;
import java.util.List;

public class Main {
    //todo remove or add plagiat cost
    private static final int                    START_DEPTH             = 6;
    private static final double                 REPRODUCTION_PERCENTAGE = 0.01;
    private static final double                 MUTATION_PERCENTAGE     = 0.15;
    private static final ISelection<BinaryTree> SELECTION               = new Tournament(5);
    public static final  int                    OFFSET                  = 5;
    private static final boolean                ACCEPT_DUPLICATES       = false;
    private static final double                 PLAGIAT_COST            = 0.9; // currently not used
    private static final int                    MAX_ITER                = 10;
    private static final double                 GOOD_ENOUGH_FITNESS     = 0.0;
    private static final int                    POPULATION_SIZE         = 700;
    private static final int                    MAX_DEPTH               = 20;
    private static final int                    MAX_NODES               = 200;
    private static final boolean                ELITISM                 = true;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        double[] data = DataReaderUtil.readDataset("datasets/USDT_BTC_sijecanj_8_2018.csv", 1, ",");
        double[] data = DataReaderUtil.readDataset("datasets/exchange-rate-twi-may-1970-aug-1.csv");
//        double[] data = DataReaderUtil.readDataset("datasets/smijesanDataset", 1, ",");

        List<DatasetEntry> dataEntries = NeuralNetworkUtil.createTDNNDateset(data, OFFSET, 1);
//        IFunction          function    = new Sin();
//        List<DataEntry> dataEntries = FunctionGenerator.generateDataEntries(200, 5, function);

        Pair<List<DatasetEntry>, List<DatasetEntry>>splittedDataEntries =
            NeuralNetworkUtil.splitTDNNDataset(dataEntries, 0.8);

        List<DatasetEntry> trainSet = splittedDataEntries.getFirst();
        List<DatasetEntry> testSet  = splittedDataEntries.getSecond();
//
//        SpecialFunction sp = new SpecialFunction(dataEntries);
//        System.out.println(sp.getMse());

//        ARGenerator generator = new ARGenerator(new Random());
//        InputGenerator generator = new InputGenerator(5, new Random());
//        Node        topNode   = new TerminalNode(1, 1, null, generator.getRandomTerminator());
//        BinaryTree  bt        = new BinaryTree(topNode);
//        bt.evaluate(trainSet, testSet);
//        System.out.println(bt.toString());
//        System.out.println(bt.getTrainFitness());
//        System.out.println(bt.getTestFitness());
//        bt.evaluate(trainSet, dataEntries);
//        System.out.println(bt.getTestFitness());
//        System.out.println();

        GeneticProgramming gp = new GeneticProgramming(
            MAX_ITER,
            POPULATION_SIZE,
            GOOD_ENOUGH_FITNESS,
            START_DEPTH,
            REPRODUCTION_PERCENTAGE,
            MUTATION_PERCENTAGE,
            SELECTION,
            MAX_DEPTH,
            MAX_NODES,
            ACCEPT_DUPLICATES,
            ELITISM,
            OFFSET,
            null,
            trainSet,
            testSet
        );

        BinaryTree result = gp.compute(
        );

        gp.predictNextValues(100);
        System.out.println(result.toString());
//        SerializationUtil.serialize(result);

//        BinaryTree novo = (BinaryTree) SerializationUtil.deserialize();
////        System.out.println(novo.toString());
//        if (result.getTrainFitness() >= novo.getTrainFitness()) {
//            System.out.println("Mijenjam");
//            SerializationUtil.serialize(result);
//        }

//        GeneticProgrammingUtil.plot("Train", result, trainSet);
//        GeneticProgrammingUtil.plot("Test", result, testSet);
//        GeneticProgrammingUtil.plot("Dataset", result, dataEntries);
    }
}
