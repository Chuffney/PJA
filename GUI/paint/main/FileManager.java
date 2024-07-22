package main;

import main.shapes.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private static File associatedFile = null;

    public static SaveState currentSaveState = SaveState.New;

    public static void openFile(ActionEvent ignored) {
        if (currentSaveState == SaveState.Modified) {
            switch (Dialog.askForSave(Main.getMainWindow())) {
                case YES:
                    saveFile();
                case NO:
                    break;
                case CANCEL:
                    return;
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(Main.getMainWindow()) == JFileChooser.CANCEL_OPTION)
            return;

        File selectedFile = fileChooser.getSelectedFile();
        setAssociatedFile(selectedFile);

        try {
            FileInputStream fis = new FileInputStream(associatedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            int numberOfShapes = inputInt(bis);
            DrawingPane.shapes = new ArrayList<>(numberOfShapes);
            for (int i = 0; i < numberOfShapes; i++) {
                DrawingPane.shapes.add(Shape.fromArray(bis.readNBytes(6)));
            }

            for (int x = 0; x < DrawingPane.DRAWING_DIMENSIONS.width; x++) {
                for (int y = 0; y < DrawingPane.DRAWING_DIMENSIONS.height; y++) {
                    DrawingPane.pixels[x][y] = new Color(bis.read(), bis.read(), bis.read());
                }
            }

            bis.close();
        } catch (IOException | NullPointerException e) {
            Dialog.showError(fileChooser, "File could not be accessed");
        } catch (IllegalArgumentException e) {
            Dialog.showError(fileChooser, "Invalid format or corrupted file");
        }
        setSaveState(SaveState.Saved);
        DrawingPane.singleton.repaint();
    }

    public static void saveFile(ActionEvent ignored) {
        saveFile();
    }

    public static void saveFile() {
        if (associatedFile == null) {
            saveAs();
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(associatedFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            outputInt(bos, DrawingPane.shapes.size());
            for (Shape shape : DrawingPane.shapes) {
                bos.write(shape.toArray());
            }

            for (Color[] row : DrawingPane.pixels) {
                for (Color pixel : row) {
                    bos.write(pixel.getRed());
                    bos.write(pixel.getGreen());
                    bos.write(pixel.getBlue());
                }
            }

            bos.close();
        } catch (IOException e) {
            Dialog.showError(Main.getMainWindow(), "File could not be accessed");
        }

        setSaveState(SaveState.Saved);
    }

    public static void saveAs(ActionEvent ignored) {
        saveAs();
    }

    private static void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(Main.getMainWindow()) == JFileChooser.CANCEL_OPTION)
            return;

        File selectedFile = fileChooser.getSelectedFile();
        setAssociatedFile(selectedFile);
        saveFile();
    }

    private static int inputInt(InputStream stream) throws IOException {
        int number = 0;
        for (int i = 0, shift = 0; i < 4; i++, shift += 8) {
            number |= stream.read() << shift;
        }
        return number;
    }

    private static void outputInt(OutputStream stream, int number) throws IOException {
        for (int i = 0; i < 4; i++) {
            stream.write(number);
            number >>= 8;
        }
    }

    public static void setSaveState(SaveState state) {
        currentSaveState = state;
        Main.setSaveState(state);
    }

    private static void setAssociatedFile(File file) {
        associatedFile = file;
        Main.setAssociatedFile(file);
    }

    public enum SaveState {
        New,
        Saved,
        Modified
    }
}
