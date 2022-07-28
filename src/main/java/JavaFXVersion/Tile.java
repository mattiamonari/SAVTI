package JavaFXVersion;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Tile extends ImageView implements Comparable<Tile> {
    public final int position;
    final int x;
    final int y;
    final WritableImage tile;

    public Tile(Image image , int position , int x , int y) {
        super(image);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tile = null;
    }

    @Override
    public int compareTo(Tile o) {
        return Integer.compare(this.position , o.position);
    }

    public WritableImage getTile() {
        return tile;
    }
}
