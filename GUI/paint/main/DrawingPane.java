package main;

import main.shapes.Circle;
import main.shapes.Shape;
import main.shapes.Square;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.FileManager.SaveState.*;

public class DrawingPane extends JPanel {
    public static final Dimension DRAWING_DIMENSIONS = new Dimension(400, 400);
    public static final Color BACKGROUND_COLOR = Color.lightGray;

    public static List<Shape> shapes = new ArrayList<>();
    public static Color[][] pixels = new Color[DRAWING_DIMENSIONS.width][DRAWING_DIMENSIONS.height];

    public static final DrawingPane singleton = new DrawingPane();
    static Color penColor = Color.black;

    private DrawingPane() {
        setPreferredSize(DRAWING_DIMENSIONS);
        Brush.BrushListener brush = new Brush.BrushListener();
        addMouseMotionListener(brush);
        addMouseListener(brush);
        addKeyListener(brush);
        for (Color[] pixel : pixels) {
            Arrays.fill(pixel, BACKGROUND_COLOR);
        }
    }

    public static void setColor(ActionEvent ignored) {
        penColor = JColorChooser.showDialog(singleton, "color", penColor);
    }

    public static void clearCanvas(ActionEvent ignored) {
        for (Color[] pixel : pixels) {
            Arrays.fill(pixel, BACKGROUND_COLOR);
        }
        shapes.clear();
        singleton.repaint();
        FileManager.setSaveState(Modified);
    }

    private static boolean isWithinBoundaries(Point point) {
        return point.x >= 0 && point.x < DRAWING_DIMENSIONS.width &&
                point.y >= 0 && point.y < DRAWING_DIMENSIONS.height;
    }

    public static void paintPixel(Point point) {
        if (!isWithinBoundaries(point))
            return;

        pixels[point.x][point.y] = penColor;
        singleton.repaint(new Rectangle(point.x, point.y, 1, 1));
        FileManager.setSaveState(Modified);
    }

    public static void paintLine(Point start, Point end) {
        Point2D.Float floatPoint = new Point2D.Float(start.x, start.y);

        int diffX = end.x - start.x;
        int diffY = end.y - start.y;
        int length = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int i = 0; i < length; i++) {
            floatPoint.x += (float) diffX / length;
            floatPoint.y += (float) diffY / length;
            paintPixel(new Point((int) (floatPoint.x + 0.5f), (int) (floatPoint.y + 0.5f)));
        }
    }

    public static void paintShape(Brush.Mode shape, Point point) {
        Shape newShape = switch (shape) {
            case Circle -> new Circle(point);
            case Square -> new Square(point);
            default -> throw new IllegalStateException();
        };
        shapes.add(newShape);
        singleton.repaint();
        FileManager.setSaveState(Modified);
    }

    public static void eraseShapes(Point point) {
        boolean erased = false;
        for (Shape shape : shapes) {
            if (shape.isInside(point)) {
                erased = true;
                break;
            }
        }

        if (!erased || Dialog.askForConfirm(Main.getMainWindow(), "Erase shapes?", "Erase") == Dialog.Response.NO)
            return;

        shapes.removeIf(s -> s.isInside(point));
        FileManager.setSaveState(Modified);
        singleton.repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        for (int x = 0; x < DRAWING_DIMENSIONS.width; x++) {
            for (int y = 0; y < DRAWING_DIMENSIONS.height; y++) {
                graphics.setColor(pixels[x][y]);
                graphics.fillRect(x, y, 1, 1);
            }
        }

        for (Shape shape : shapes) {
            shape.draw(graphics);
        }
    }

    @Override
    public boolean isFocusable() {
        return true;
    }
}
