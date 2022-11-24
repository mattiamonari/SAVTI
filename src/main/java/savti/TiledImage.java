package savti;

import javafx.scene.image.Image;

import java.util.*;

public class TiledImage {
    private Tile[] array;
    private Image image;

    public TiledImage() {
        array = new Tile[64];
    }

    public TiledImage(TiledImage tiledImage) {
        this.array = new Tile[tiledImage.getArray().length];
        System.arraycopy(tiledImage.getArray(), 0, this.array, 0, this.array.length);
        this.setImage(tiledImage.getImage());
    }

    public static TiledImage newInstance(TiledImage tiledImage) {
        return new TiledImage(tiledImage);
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

    /**
     * Fills the array of the image of null and dereference the image.
     */
    public void clearImage() {
        Arrays.fill(array, null);
        image = null;
    }

    /**
     * Creates a new empty array of the given size.
     *
     * @param newSize The new size the array of the image will have.
     */
    public void resizeArray(int newSize) {
        array = new Tile[newSize];
    }

    /**
     * Change the value of a Tile at a given position of the array of the selected image.
     *
     * @param tile The tile to insert into the array
     * @param pos  The position of the tile
     */
    public void setTileAtPosition(Tile tile, int pos) {
        array[pos] = tile;
    }

    /**
     * @return true if the array of the selected image is empty, false otherwise.
     */
    public boolean isArrayEmpty() {
        return Arrays.stream(array).allMatch(Objects::isNull) || image == null;
    }

    /**
     * @return true if the array of the selected image is already ordered, false otherwise.
     */
    public boolean isAlreadyOrdered() {
        List<Tile> sorted = new ArrayList<>(List.of(array));
        Collections.sort(sorted);
        return sorted.equals(List.of(array));
    }
}
