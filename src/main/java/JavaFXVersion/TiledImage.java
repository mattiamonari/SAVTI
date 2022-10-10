package JavaFXVersion;

import javafx.scene.image.Image;

import java.util.*;

public class TiledImage {
    private Tile[] array;
    private Image image;

    public TiledImage() {
        array = new Tile[64];
    }


    public Tile[] getArray() {
        return array;
    }

    public void setArray(Tile[] array) {
        this.array = array;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void clearImage() {
        Arrays.fill(array, null);
        image = null;
    }

    public void resizeArray(int newSize) {
        array = new Tile[newSize];
    }

    public void setTileAtPosition(Tile tile, int pos) {
        array[pos] = tile;
    }

    public boolean isArrayEmpty() {
        return Arrays.stream(array).allMatch(Objects::isNull);
    }

    public boolean isAlreadyOrdere() {

        List<Tile> sorted = new ArrayList<>(List.of(array));
        Collections.sort(sorted);
        return sorted.equals(List.of(array));
    }

    @Override
    protected TiledImage clone() throws CloneNotSupportedException {
        TiledImage clone = new TiledImage();
        Tile[] arr = new Tile[this.getArray().length];
        System.arraycopy(this.getArray(), 0, arr, 0, this.array.length);
        clone.setArray(arr);
        clone.setImage(image);
        return clone;
    }
}
