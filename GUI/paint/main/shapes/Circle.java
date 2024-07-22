package main.shapes;

import main.Brush;

import java.awt.*;

public class Circle extends Shape {
    public static final int RADIUS = BASE_SIZE;
    private static final int RADIUS_SQUARED = RADIUS * RADIUS;

    public Circle(Point origin) {
        super(origin);
    }

    protected Circle(Point origin, Color color) {
        super(origin, color);
    }

    @Override
    public boolean isInside(Point point) {
        int xDiff = origin.x - point.x;
        int yDiff = origin.y - point.y;
        int distanceSquared = (xDiff * xDiff) + (yDiff * yDiff);
        return distanceSquared < RADIUS_SQUARED;
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillOval(origin.x - BASE_SIZE, origin.y - BASE_SIZE, RADIUS * 2, RADIUS * 2);
    }

    @Override
    protected Brush.Mode getType() {
        return Brush.Mode.Circle;
    }
}
