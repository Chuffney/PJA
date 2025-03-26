import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static final String recognizedClassification = "Iris-setosa";

    private static Vector parseUnknownInputLine(String line) {
        line = line.replaceAll(" ", "").replaceAll(",", ".");
        String[] tokens = line.split("\\s");

        float[] data = new float[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            data[i] = Float.parseFloat(tokens[i]);
        }

        return new Vector(data, false);
    }

    private static Vector parseKnownInputLine(String line) {
        line = line.replaceAll(" ", "").replaceAll(",", ".");
        String[] tokens = line.split("\t");

        float[] data = new float[tokens.length - 1];
        for (int i = 0; i < tokens.length - 1; i++) {
            data[i] = Float.parseFloat(tokens[i]);
        }

        return new Vector(data, recognizedClassification.equals(tokens[tokens.length - 1]));
    }

    public static void main(String[] args) throws Throwable {
        List<String> lines = Files.readAllLines(Path.of("iris_training.txt"));
        List<Vector> trainingData = new ArrayList<>(lines.size());

        for (String line : lines) {
            trainingData.add(parseKnownInputLine(line));
        }

        int dimensions = trainingData.get(0).values().length;
        Perceptron perceptron = new Perceptron(dimensions);

        int passes = 0;
        for (boolean perfect = false; !perfect;) {
            perfect = true;
            for (Vector trainingDatum : trainingData) {
                perfect &= perceptron.learn(trainingDatum);
            }

            passes++;
            Collections.shuffle(trainingData);
        }

        System.out.println("training passes: " + passes);

        lines = Files.readAllLines(Path.of("iris_test.txt"));
        int errors = lines.stream().map(Main::parseKnownInputLine).mapToInt(v -> Math.abs(Boolean.compare(v.classification(), perceptron.compute(v)))).reduce(0, Integer::sum);
        System.out.printf("tests classified correctly: (%d/%d) %f%%\n", lines.size() - errors, lines.size(), 100.0f - (float) errors / lines.size());

        Scanner userInput = new Scanner(System.in);
        while (true) {
            String inputData = userInput.nextLine();

            if (inputData.equalsIgnoreCase("stop"))
                break;

            String response = perceptron.compute(parseUnknownInputLine(inputData)) ? "setosa" : "nie setosa";
            System.out.println(response);
        }
    }
}
