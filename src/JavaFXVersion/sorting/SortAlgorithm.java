package JavaFXVersion.sorting;

import JavaFXVersion.Tail;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The common interface of most sorting algorithms
 *
 * @author Podshivalov Nikita (https://github.com/nikitap492)
 */
public interface SortAlgorithm {

    /*********
     Methood that i added. Improve them!
     **********/
    void killTask();
    boolean isThreadAlive();
    void sort(Tail[] array , GridPane gridPane);


    /**
     * Main method arrays sorting algorithms
     *
     * @param unsorted - an array should be sorted
     * @return a sorted array
     */
    <T extends Comparable<T>> T[] sort(T[] unsorted);

    /**
     * Auxiliary method for algorithms what wanted to work with lists from JCF
     *
     * @param unsorted - a list should be sorted
     * @return a sorted list
     */
    @SuppressWarnings("unchecked")
    default <T extends Comparable<T>> List<T> sort(List<T> unsorted) {
        return Arrays.asList(sort(unsorted.toArray((T[]) new Comparable[unsorted.size()])));
    }

    /**
     * Questa funzione randomizza un vettore di Tail utilizzato la classe Random
     *
     * @param array Il vettore da randomizzare
     */
    public static <T extends Comparable<T>> void rand(T[] array) {
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


}