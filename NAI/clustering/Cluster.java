import java.util.ArrayList;

public class Cluster extends ArrayList<Datapoint>{
    private static int nextId = 0;

    public final int id;
    public final Vector centroid;

    public Cluster(Vector centroid) {
        id = nextId++;
        this.centroid = centroid.copy();
    }

    public void updateCenter() {
        int datapointCount = size();
        if (datapointCount == 0)
            return;

        for (int i = 0; i < Vector.DIMENSIONS; i++) {
            float sum = 0f;

            for (Datapoint datapoint : this) {
                sum += datapoint.values[i];
            }

            centroid.values[i] = sum / datapointCount;
        }
    }
}
