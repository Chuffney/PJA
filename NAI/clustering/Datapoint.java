public class Datapoint extends Vector {
    int classificationId;
    int clusterId;
    int id;
    static int nextId = 0;

    public Datapoint(String line) {
        id = nextId++;
        line = line.replaceAll(" ", "").replaceAll(",", ".");
        String[] tokens = line.split("\t");


        for (int i = 0; i < DIMENSIONS; i++) {
            values[i] = Float.parseFloat(tokens[i]);
        }

        classificationId = Enumerator.enumerate(tokens[DIMENSIONS]);
        clusterId = 0;
    }

    public Cluster findNearestCluster(Iterable<Cluster> clusters) {
        float minDistance = Float.MAX_VALUE;
        Cluster minVector = null;
        for (Cluster cluster : clusters) {
            float distance = distanceSquared(cluster.centroid);
            if (distance < minDistance) {
                minVector = cluster;
                minDistance = distance;
            }
        }

        return minVector;
    }

    @Override
    public String toString() {
        return Enumerator.getName(classificationId);
    }
}
