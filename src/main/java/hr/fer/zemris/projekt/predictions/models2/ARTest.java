package hr.fer.zemris.projekt.predictions.models2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class ARTest {

    public static void main(String[] args) throws IOException {
        List<String> b = Files.readAllLines(Paths.get("./datasets/ARDataset.txt"));
        int i = 0;
        double[] dataset = new double[b.size()];
        for (String s : b) {
            double value = Double.parseDouble(s.split(" ")[4]);
            int k = (int) (value * 100);
            int l = k % 1000;
            value = l / 100.;
            System.out.println(value + ",");
            dataset[i] = value;
            i++;
        }

        double mean = new Mean().evaluate(dataset);

		/*for (int i = 0; i < dataset.length; i++) {
			dataset[i] -= mean;
		}*/

        AR model = new AR(1, dataset, false);
        model.fitModel();

        System.out.println(model.forecast(1));
        System.out.println(model.getAIC());
        //		System.out.println(model.checkIfStationary());

        System.out.println(model.coeffs()[0]);
        //		MA model = new MA(2, dataset, false);
        //		System.out.println(model.getAIC(new double[] {0.1537}));
        //		model.fitModel();
        //
        //		for (double d : model.getCoeff().getColumnPackedCopy()) {
        //			System.out.println(d);
        //		}
        //
    }
}
