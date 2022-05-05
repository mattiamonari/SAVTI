package JavaFXVersion;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

public class ImageUtilities {

    /**
     * Divide un'immagine in tante WritableImage, le quali verranno poi inserite nel vettore array
     *
     * @param oldImage Immagine da dividere
     * @param rows Numero di righe in cui l'immagine verrà divisa
     * @param cols Numero di colonne in cui l'immagine verrà divisa
     */

    //? Could it be better to return a Tail[] vector?
    public static void splitImage(Image oldImage, int rows , int cols, Tail[] array) {
        int chunkWidth = (int) oldImage.getWidth() / cols;
        int chunkHeight = (int) oldImage.getHeight() / rows;
        PixelReader reader = oldImage.getPixelReader();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                array[x * rows + y] = new Tail(new WritableImage(reader , x * chunkWidth , y * chunkHeight, chunkWidth,
                        chunkHeight) , x * rows + y , x , y);
            }
        }
    }

    /**
     * Questa funzione aggiunge al GridPane le varie Tail prendendole dal vettore globale main
     *
     * @param chunkWidth La larghezza in pixel dei chunk
     * @param chunkHeight L'altezza in pixel dei chunk
     * @param rows Il numero di colonne in cui è stata divisa l'immagine
     * @param cols Il numero di righe in cui è stata divisa l'immagine
     */
    public static void fillImage(int chunkWidth , int chunkHeight , int rows , int cols, Tail[] array, GridPane container) {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                array[x * rows + y].setPreserveRatio(true);
                array[x * rows + y].setFitHeight(chunkHeight);
                array[x * rows + y].setFitWidth(chunkWidth);
                array[x * rows + y].setOpacity(0.8);

                //Qua aggiungiamo la tail presa dal vettore
                //Nella griglia nella riga x e alla colonna y
                container.add(array[x * rows + y] , x , y);
            }
        }
    }


}
