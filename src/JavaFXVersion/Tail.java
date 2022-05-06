package JavaFXVersion;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

//Extends image view perchè ogni tail conterrà un immagine propria
//Implementa comparable per poterle confrontare dentro ad un vettore
//I costruttori di questa classe sono stati presi dal suo Genitore ImageView
//E modellati a piacimento per poter settare i campi custom.

public class Tail extends ImageView implements Comparable<Tail>{

    public int position;
    int x;
    int y;
    WritableImage tail;

    public Tail(int position , int x , int y , WritableImage tail) {
        this.position = position;
        this.x = x;
        this.y = y;
        this.tail = tail;
    }

    public Tail(String url , int position , int x , int y , WritableImage tail) {
        super(url);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tail = tail;
    }

    public Tail(Image image , int position , int x , int y) {
        super(image);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tail = null;
    }

    public Tail(Image image , int position , int x , int y , WritableImage tail) {
        super(image);
        this.position = position;
        this.x = x;
        this.y = y;
        this.tail = tail;
    }

    @Override
    public int compareTo(Tail o) {

        if(this.position==o.position)
            return 0;
        else if(this.position>o.position)
            return 1;
        else
            return -1;

    }


    public WritableImage getTail() {
        return tail;
    }
}
