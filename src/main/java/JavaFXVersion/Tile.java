package JavaFXVersion;

import javafx.scene.image.WritableImage;

public class Tile implements Comparable<Tile>, Cloneable {
    public int currentPosition;
    private int x;
    private int y;
    final int initialPosition;
    final WritableImage tile;

    public Tile(WritableImage image , int position , int x , int y) {
        this.currentPosition = position;
        this.x = x;
        this.y = y;
        this.tile = image;
        this.initialPosition = position;
    }

    @Override
    public int compareTo(Tile o) {
        return Integer.compare(this.initialPosition , o.initialPosition);
    }

    @Override
    public String toString() {
        return "Posizione: " + currentPosition + "\t x: " + x + " y: " + y;
    }

    public WritableImage getTile() {
        return tile;
    }

    public double getWidth() {
        return tile.getWidth();
    }

    public double getHeight() {
        return tile.getHeight();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public Tile clone() throws CloneNotSupportedException {
        Tile clone = (Tile) super.clone();
        clone.setX(this.x);
        clone.setY(this.y);
        clone.currentPosition = this.currentPosition;
        return clone;
    }
}
