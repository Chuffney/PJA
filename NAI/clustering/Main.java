import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Main {
    private static final List<Datapoint> data = new ArrayList<>();
    private static final List<Cluster> clusters = new ArrayList<>();

    private static void parseDataFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());

        for (String line : lines) {
            data.add(new Datapoint(line));
        }
    }

    private static void initData(int k) {
        Random random = new Random();
        List<Datapoint> potentialCenters = new ArrayList<>(data);

        for (; k > 0; --k) {
            Datapoint center = potentialCenters.remove(random.nextInt(potentialCenters.size()));
            clusters.add(new Cluster(center));
        }
    }

    private static boolean clusteringStep() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }

        boolean changed = false;

        for (int i = 0; i < data.size(); i++) {
            Datapoint datapoint = data.get(i);
            Cluster nearest = datapoint.findNearestCluster(clusters);
            if (nearest.id != datapoint.clusterId) {
                datapoint.clusterId = nearest.id;
                changed = true;
            }

            nearest.add(datapoint);
        }

        for (Cluster cluster : clusters) {
            cluster.updateCenter();
        }
        return changed;
    }

    private static float squaredDistanceSum(Cluster cluster) {
        float sum = 0f;
        Vector centre = cluster.centroid;

        for (Datapoint point : cluster) {
            sum += point.distanceSquared(centre);
        }
        return sum;
    }

    private static float computeEntropy(Cluster cluster) {
        final float logConvConst = (float) Math.log(2);

        float entropy = 0f;
        int categoryCount = Enumerator.getAllNames().size();

        int[] occurrences = new int[categoryCount];

        for (Datapoint point : cluster) {
            occurrences[point.classificationId]++;
        }

        for (int i = 0; i < categoryCount; i++) {
            float probability = (float) occurrences[i] / cluster.size();
            if (probability != 0)
                entropy += probability * ((float) Math.log(probability) / logConvConst);
        }

        return Math.abs(entropy);
    }

    public static void main(String[] args) throws IOException {
        int minK = 3;

        parseDataFile(new File("iris_training.txt"));
        int maxK = data.size();

        Scanner scanner = new Scanner(System.in);

        System.out.print("k: ");

        int k = 0;
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= minK && input <= maxK) {
                    k = input;
                    break;
                }
            } catch (InputMismatchException ignored) {}
            System.err.printf("Podaj liczbę całkowitą z zakresu [%d, %d]\n", minK, maxK);
        }

        initData(k);

        while (clusteringStep()) {
            for (Cluster cluster : clusters) {
                float distanceSum = squaredDistanceSum(cluster);
                System.out.printf("Cluster %d suma kwadratów odległości: %.2f\n", cluster.id, distanceSum);
            }
            System.out.println();
        }

        for (Cluster cluster : clusters) {
            System.out.printf("Cluster %d - entropia: %.2f\n", cluster.id, computeEntropy(cluster));
            for (int i = 0; i < data.size(); i++) {
                Datapoint point = data.get(i);
                if (point.clusterId == cluster.id) {
                    System.out.printf("\t %s (id: %d)\n", point, i);
                }
            }
        }
    }
}
