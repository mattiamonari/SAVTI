package JavaFXVersion;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
//Extends image view perchè ogni Tile conterrà un immagine propria
//Implementa comparable per poterle confrontare dentro ad un vettore
//I costruttori di questa classe sono stati presi dal suo Genitore ImageView
//E modellati a piacimento per poter settare i campi custom.

public class Tile extends ImageView implements Comparable<Tile> {
    public final int position;
    final int x;
    final int y;
    final WritableImage tile;

    public Tile(int position , int x , int y , WritableImage tile) {
        this.position = position;
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    public Tile(String url , int position , int x , int y , WritableImage tile) {
        super(url);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    public Tile(Image image , int position , int x , int y) {
        super(image);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tile = null;
    }

    public Tile(Image image , int position , int x , int y , WritableImage tile) {
        super(image);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    @Override
    public int compareTo(Tile o) {
        return Integer.compare(this.position , o.position);
    }

    public WritableImage getTile() {
        return tile;
    }
}
