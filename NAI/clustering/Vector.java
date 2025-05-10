public class Vector implements Cloneable {
    public static final int DIMENSIONS = 4;

    public final float[] values = new float[DIMENSIONS];

    public Vector() {}

    public float distanceSquared(Vector other) {
        float sum = 0f;

        for (int i = 0; i < DIMENSIONS; i++) {
            float diff = values[i] - other.values[i];
            sum += diff * diff;
        }

        return sum;
    }

    public Vector copy() {
        Vector copy = new Vector();
        System.arraycopy(this.values, 0, copy.values, 0, DIMENSIONS);
        return copy;
    }
}
