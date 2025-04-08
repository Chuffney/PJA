import java.io.File;
import java.util.*;

public class Main {
    private static final String trainingPath = "testData";
    private static final String testPath = "testData";
    private static final String charmapPath = "charmap.txt";
    private static final List<Neuron> neurons = new ArrayList<>();


    public static void main(String[] args) {
        File trainingDir = new File(trainingPath);
        File testDir = new File(testPath);
        File[] trainingFiles = trainingDir.listFiles();
        File[] testFiles = testDir.listFiles();
        List<Dataset> datasets = new ArrayList<>();
        List<Dataset> testSets = new ArrayList<>();

        Dataset.parseCharMapFile(new File(charmapPath));

        for (File languageDir : trainingFiles) {
            if (!languageDir.isDirectory())
                continue;

            String name = languageDir.getName();
            int nameId = Enumerator.enumerate(name);

            for (File dataFile : languageDir.listFiles()) {
                datasets.add(new Dataset(dataFile, nameId));
            }
            neurons.add(new Neuron(nameId));
        }

        for (File languageDir : testFiles) {
            if (!languageDir.isDirectory())
                continue;

            String name = languageDir.getName();
            int nameId = Enumerator.enumerate(name);

            for (File testFile : languageDir.listFiles()) {
                testSets.add(new Dataset(testFile, nameId));
            }
        }

        long trainingBuffer = Neuron.initBuffer(datasets.size());
        long testsBuffer = Neuron.initBuffer(testSets.size());

        for (Dataset dataset : datasets) {
            Neuron.addDataset(trainingBuffer, dataset.charFrequency.data, dataset.nameId);
        }

        for (Dataset dataset : testSets) {
            Neuron.addDataset(testsBuffer, dataset.charFrequency.data, dataset.nameId);
        }

        for (Neuron neuron : neurons) {
            neuron.train(trainingBuffer, testsBuffer);
        }

        GUI.startGUI();
    }

    public static Collection<Integer> recogniseLanguage(String text) {
        Collection<Integer> positives = new ArrayList<>();

        for (Neuron neuron : neurons) {
            if (neuron.compute(Dataset.countFrequencies(text)))
                positives.add(neuron.recognisedId);
        }
        return positives;
    }
}
