package my.project.simulation.utils;

import java.util.Objects;

public class Vector2D {
    final int x;
    final int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof Vector2D otherCasted) {
            return x == otherCasted.x && y == otherCasted.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public boolean precedes(Vector2D other) {
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2D other) {
        return x >= other.x && y >= other.y;
    }

    public Vector2D upperRight(Vector2D other) {
        return new Vector2D(Math.max(x, other.x), Math.max(y, other.y));
    }

    public Vector2D upperLeft(Vector2D other) {
        return new Vector2D(Math.min(x, other.x), Math.max(y, other.y));
    }

    public Vector2D lowerLeft(Vector2D other) {
        return new Vector2D(Math.min(x, other.x), Math.min(y, other.y));
    }

    public Vector2D lowerRight(Vector2D other) {
        return new Vector2D(Math.max(x, other.x), Math.min(y, other.y));
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D opposite() {return new Vector2D(-x, -y); }

    public static Vector2D randomVector(int minX, int maxX, int minY, int maxY) {
        return new Vector2D(Random.randInt(minX, maxX), Random.randInt(minY, maxY));
    }
}
