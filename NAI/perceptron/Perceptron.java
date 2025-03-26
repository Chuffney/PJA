public class Perceptron {
    private static final float ALPHA = 0.1f;

    float[] weights;
    float threshold;
    int dimensions;

    public Perceptron(int dimensions) {
        this.dimensions = dimensions;
        weights = new float[dimensions];
        threshold = (float) Math.random();

        for (int i = 0; i < dimensions; i++) {
            weights[i] = (float) ((Math.random() * 2.0) - 1.0); //(-1, 1)
        }
    }

    private float computeNet(Vector input) {
        float net = 0.0f;
        for (int i = 0; i < dimensions; i++) {
            net += input.values()[i] * weights[i];
        }
        return net;
    }

    public boolean learn(Vector input) {
        boolean correctAnswer = input.classification();
        float net = computeNet(input);

        boolean decision = net >= threshold;

        if (decision == correctAnswer)
            return true;


        for (int i = 0; i < dimensions; i++) {
            weights[i] += Boolean.compare(correctAnswer, decision) * ALPHA * input.values()[i];
        }

        //threshold += Boolean.compare(correctAnswer, decision) * ALPHA;
        return false;
    }

    public boolean compute(Vector input) {
        float net = computeNet(input);
        return net >= threshold;
    }
}
