import java.io.File;

public class Neuron {
    static {
        File libraryFile = new File("training.dll");
        System.load(libraryFile.getAbsolutePath());
    }

    private static final int DIMENSIONS = Vector.DIMENSIONS;
    private final long nativeAddr;

    private final Vector weights;
    public final int recognisedId;
    private float threshold;

    public Neuron(int recognisedId) {
        threshold = 0;
        this.recognisedId = recognisedId;
        weights = new Vector();
        nativeAddr = initNeuron(recognisedId);
    }

    private static native long initNeuron(int recognisedId);

    public static native long initBuffer(int elementCount);
    public static native void addDataset(long bufAddr, float[] data, int nameId);

    private static native void train(long neuronAddr, long dataAddr, long testsAddr);

    private static native void getWeights(long neuronAddr, float[] weights);
    private static native float getThreshold(long neuronAddr);

    public void train(long training, long tests) {
        train(nativeAddr, training, tests);
        getWeights(nativeAddr, weights.data);
        threshold = getThreshold(nativeAddr);
    }


    public boolean compute(Vector input) {
        float net = computeNet(input);
        return activationFunction(net);
    }

    //returns whether this neuron classified the dataset correctly
    public boolean test(Dataset dataset) {
        boolean decision = compute(dataset.charFrequency);
        boolean shouldAnswer = recognisedId == dataset.nameId;
        return decision == shouldAnswer;
    }

    private boolean activationFunction(float net) {
        return net >= threshold;
    }

    public float computeNet(Vector input) {
        float net = 0.0f;
        for (int i = 0; i < DIMENSIONS; i++) {
            net += input.data[i] * weights.data[i];
        }
        return net;
    }
}
