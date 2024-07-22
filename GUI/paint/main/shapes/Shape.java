package main.shapes;

import main.Brush;

import java.awt.*;
import java.util.Random;

public abstract class Shape {
    private static final Random RANDOM_COLOR = new Random();

    protected static final int BASE_SIZE = 15;

    protected Point origin;
    protected final Color color;

    public Shape(Point origin) {
        this(origin, new Color(RANDOM_COLOR.nextInt()));
    }

    protected Shape(Point origin, Color color) {
        this.origin = new Point(origin);
        this.color = color;
    }

    public abstract boolean isInside(Point point);

    public abstract void draw(Graphics graphics);

    protected abstract Brush.Mode getType();

    /**
     * struct shape {
     *     int4:   enum type,
     *     int10:  X,
     *     int10:  Y,
     *     int24:  color,
     * }
     */
    public byte[] toArray() {
        byte[] array = new byte[6];
        long contiguous = 0L;

        contiguous |= getType().ordinal() & 0xF;
        contiguous |= (origin.x & 0x3FF) << 4;
        contiguous |= (origin.y & 0x3FF) << 14;
        contiguous |= (long) color.getRGB() << 24;

        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) contiguous;
            contiguous >>= 8;
        }
        return array;
    }

    public static Shape fromArray(byte[] array) {
        if (array.length != 6)
            throw new IllegalArgumentException();

        long contiguous = 0L;

        for (int i = array.length - 1; i >= 0; i--) {
            contiguous <<= 8;
            contiguous |= Byte.toUnsignedLong(array[i]);
        }

        int x = (int) (contiguous >> 4) & 0x3FF;
        int y = (int) (contiguous >> 14) & 0x3FF;
        Point origin = new Point(x, y);

        int rgb = (int) (contiguous >> 24) & 0xFFFFFF;
        Color color = new Color(rgb);
        int typeOrdinal = (int) contiguous & 0xF;
        return switch (Brush.Mode.values()[typeOrdinal]) {
            case Circle -> new Circle(origin, color);
            case Square -> new Square(origin, color);
            default -> throw new IllegalArgumentException();
        };
    }
}
