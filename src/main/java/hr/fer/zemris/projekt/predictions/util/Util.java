package hr.fer.zemris.projekt.predictions.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static Map<String, Double> parseDataset(Path path) throws UtilException{
        List<String> dataset = null;
        try {
            dataset = Files.readAllLines(path);
        } catch (IOException e) {
            throw new UtilException("");
        }
        TreeMap<String, Double> map = new TreeMap<>();
        dataset.stream().forEach((s)->{
            String[] data = s.split(",");
            if ( data.length != 2) {
                throw new UtilException("Only univariable datasets permitted.");
            }
            map.put()
        } );

    }



}
