package hr.fer.zemris.project.forecasting.util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SerializationUtil {

    private static final String DEFAULT_PATH = "./exports/serializedObjects/";
    private static final String DEFAULT_NAME = "export.ser";

    public static void serialize(Serializable serializable) throws IOException {
        serialize(DEFAULT_NAME, serializable);
    }

    public static void serialize(String name, Serializable serializable) throws IOException {
        serialize(Paths.get(String.format("%s%s", DEFAULT_PATH, name)), serializable);
    }

    public static void serialize(Path path, Serializable serializable) throws IOException {
        FileOutputStream   fileOut = new FileOutputStream(path.toFile());
        ObjectOutputStream out     = new ObjectOutputStream(fileOut);
        out.writeObject(serializable);
        out.close();
        fileOut.close();
    }

    public static Serializable deserialize() throws IOException, ClassNotFoundException {
        return deserialize(DEFAULT_NAME);
    }

    public static Serializable deserialize(String name) throws IOException, ClassNotFoundException {
        return deserialize(Paths.get(String.format("%s%s", DEFAULT_PATH, name)));
    }

    public static Serializable deserialize(Path path) throws IOException, ClassNotFoundException {
        FileInputStream   fileIn = new FileInputStream(path.toFile());
        ObjectInputStream in     = new ObjectInputStream(fileIn);
        Serializable      result = (Serializable) in.readObject();
        in.close();
        fileIn.close();
        return result;
    }
}
