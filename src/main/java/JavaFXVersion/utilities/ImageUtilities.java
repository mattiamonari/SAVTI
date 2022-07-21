package JavaFXVersion.utilities;

import JavaFXVersion.Tile;
import javafx.geometry.VPos;
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
    //? Could it be better to return a Tile[] vector?
    public static void splitImage(Image oldImage , int rows , int cols , Tile[] array) {
        int chunkWidth = (int) oldImage.getWidth() / cols;
        int chunkHeight = (int) oldImage.getHeight() / rows;
        PixelReader reader = oldImage.getPixelReader();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                array[x * rows + y] = new Tile(new WritableImage(reader , x * chunkWidth , y * chunkHeight , chunkWidth , chunkHeight) , x * rows + y , x , y);
            }
        }
    }

    /**
     * Questa funzione aggiunge al GridPane le varie Tile prendendole dal vettore globale main
     *
     * @param chunkWidth La larghezza in pixel dei chunk
     * @param chunkHeight L'altezza in pixel dei chunk
     * @param rows Il numero di colonne in cui è stata divisa l'immagine
     * @param cols Il numero di righe in cui è stata divisa l'immagine
     */
    public static void fillImage(int chunkWidth , int chunkHeight , int rows , int cols , Tile[] array , GridPane container) {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                Tile tile = array[x * rows + y];
                tile.setPreserveRatio(true);
                tile.setFitHeight(chunkHeight);
                tile.setFitWidth(chunkWidth);
                //Qua aggiungiamo la tail presa dal vettore
                //Nella griglia nella riga x e alla colonna y
                if (!container.getChildren().contains(tile))
                    container.add(tile , x , y);
                else {
                    container.getChildren().remove(tile);
                    container.add(tile , x , y);
                }
            }
        }
    }
}
