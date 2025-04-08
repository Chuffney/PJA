import java.util.ArrayList;
import java.util.List;

public class Enumerator {
    private static final List<String> stringBuffer = new ArrayList<>();

    public static int enumerate(String str) {
        int idx = stringBuffer.indexOf(str);

        if (idx == -1) {
            idx = stringBuffer.size();
            stringBuffer.add(str);
        }

        return idx;
    }

    public static String getName(int id) {
        return stringBuffer.get(id);
    }

    public static List<String> getAllNames() {
        return stringBuffer;
    }
}
