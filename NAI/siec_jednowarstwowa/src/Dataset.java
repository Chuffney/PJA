import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dataset {
    private static final Map<Character, Character> charMap = new HashMap<>();
    public final int nameId;
    public final Vector charFrequency;

    public Dataset(File trainingDirectory, int nameId) {
        this.nameId = nameId;
        charFrequency = countFrequencies(trainingDirectory);
    }

    public static void parseCharMapFile(File charMapFile) {
        if (charMapFile == null)
            return;

        try {
            List<String> lines = Files.readAllLines(charMapFile.toPath());
            for (String line : lines) {
                String[] tokens = line.split("=");
                charMap.put(tokens[0].charAt(0), tokens[1].charAt(0));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Vector countFrequencies(String str) {
        Vector charFrequency = new Vector();
        int letterCount = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (!Character.isAlphabetic(c))
                continue;

            c = Character.toLowerCase(c);

            c = charMap.getOrDefault(c, c);

            if (c < 'a' || c > 'z')
                continue;

            charFrequency.data[c - 'a']++;
            letterCount++;
        }

        for (int i = 0; i < Vector.DIMENSIONS; i++) {
            charFrequency.data[i] /= letterCount;
        }
        return charFrequency;
    }

    private Vector countFrequencies(File dataFile) {
        try {
            FileChannel fileChannel = FileChannel.open(dataFile.toPath());
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            CharBuffer charBuffer = Charset.defaultCharset().decode(byteBuffer);

            fileChannel.close();
            return countFrequencies(charBuffer.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
