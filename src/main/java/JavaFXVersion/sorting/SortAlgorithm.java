package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import javafx.scene.layout.GridPane;

import java.util.Random;

//TODO IMPORTANTISSIMO
/*
   NELL'INTERFACCIA VENGONO DICHIARATI MOLTI METODI, ALCUNI NEANCHE UTILIZZATI
   E' CORRETTO AVERE UN INTERFACCIA DA CUI TUTTI GLI ALGORITMI DERIVANO MA BISOGNA SISTEMARLA
   ANCHE I PARAMETRI DEI METODI SONO DA RIVEDERE, DATO CHE UNA INTERFACCIA COSI POVERA NON CI
   PERMETTE DI UTILIZZARE ALTRI PARAMETRI NELLE FUNZIONI SORT
 */
public interface SortAlgorithm {
    static <T extends Comparable<T>> void rand(T[] array) {
        // Creating object for Random class
        Random rd = new Random();
        // Starting from the last element and swapping one by one.
        for (int i = array.length - 1; i > 0; i--) {
            // Pick a random index from 0 to i
            int j = rd.nextInt(i + 1);
            // Swap array[i] with the element at random index
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    /*********
     Methood that i added. Improve them!
     **********/
    void killTask();
    boolean isThreadAlive();
    void sort(Tile[] array , GridPane gridPane , MainWindow mainWindow);
}