package hr.fer.zemris.project.forecasting.nn.util;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.List;

public class BackpropagationUtil {

    public static RealMatrix[] createLayerWeights(double[] weights, int[] architecture) {
        RealMatrix[] layerWeights = new RealMatrix[architecture.length - 1];
        int offset = 0;
        for (int i = 0; i < architecture.length - 1; ++i) {
            RealMatrix layer = new Array2DRowRealMatrix(architecture[i] + 1, architecture[i + 1]);
            for (int j = 0; j < architecture[i] + 1; ++j) {
                double[] rowWeight = Arrays.copyOfRange(weights, offset, offset + architecture[i + 1]);
                offset += architecture[i + 1];
                layer.setRow(j, rowWeight);
            }
            layerWeights[i] = layer;
        }
        return layerWeights;
    }

    public static double[] extractWeights(RealMatrix[] layerWeights, int weightsNumber) {
        double[] weights = new double[weightsNumber];
        int offset = 0;
        for (RealMatrix layerWeight : layerWeights) {
            for (int j = 0; j < layerWeight.getRowDimension(); ++j) {
                double[] row = layerWeight.getRow(j);
                System.arraycopy(row, 0, weights, offset, row.length);
                offset += row.length;
            }
        }
        return weights;
    }

    public static List<DatasetEntry>[] createBatches(int batchSize, List<DatasetEntry> trainingSet) {
        int batchNumber = (int) Math.ceil(trainingSet.size() / (double) batchSize);
        List<DatasetEntry>[] batches = (List<DatasetEntry>[]) new List[batchNumber];
        for (int i = 0; i < batchNumber; i++) {
            if (i < batchNumber - 1) {
                batches[i] = trainingSet.subList(i * batchSize, (i + 1) * batchSize);
            } else {
                int len = trainingSet.size() % batchSize;
                len = len == 0 ? batchSize : len;
                batches[i] = trainingSet.subList(i * batchSize, i * batchSize + len);
            }
        }
        return batches;
    }
}
