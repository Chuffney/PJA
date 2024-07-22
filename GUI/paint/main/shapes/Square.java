package main.shapes;

import main.Brush;

import java.awt.*;

public class Square extends Shape {
    public static final Dimension SQUARE_DIMENSION = new Dimension(2 * BASE_SIZE, 2 * BASE_SIZE);

    public Square(Point origin) {
        super(origin);
        Point translated = new Point(origin);
        translated.translate(-BASE_SIZE, -BASE_SIZE);
        this.origin = translated;
    }

    protected Square(Point origin, Color color) {
        super(origin, color);
    }

    @Override
    public boolean isInside(Point point) {
        return point.x > origin.x && point.x < origin.x + SQUARE_DIMENSION.width &&
                point.y > origin.y && point.y < origin.y + SQUARE_DIMENSION.height;
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillRect(origin.x, origin.y, SQUARE_DIMENSION.width, SQUARE_DIMENSION.height);
    }

    @Override
    protected Brush.Mode getType() {
        return Brush.Mode.Square;
    }
}
