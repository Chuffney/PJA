
public class Vector {
    public static final int DIMENSIONS = 'z' - 'a' + 1;

    public final float[] data = new float[DIMENSIONS];

    public float dotProduct(Vector other) {
        float product = 0.0f;
        for (int i = 0; i < DIMENSIONS; i++) {
            product += data[i] * other.data[i];
        }
        return product;
    }

    public void normalise() {
        float length = distance(new Vector());
        for (int i = 0; i < DIMENSIONS; i++) {
            data[i] /= length;
        }
    }

    public float distance(Vector other) {
        float distanceSqrd = 0f;
        for (int i = 0; i < DIMENSIONS; i++) {
            distanceSqrd += Math.pow(data[i] - other.data[i], 2);
        }
        return (float) Math.sqrt(distanceSqrd);
    }
}
