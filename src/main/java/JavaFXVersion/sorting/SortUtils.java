package JavaFXVersion.sorting;

import JavaFXVersion.Tile;

public final class SortUtils {
    /**
     * Helper method for swapping places in array
     *
     * @param array The array which elements we want to swap
     * @param idx index of the first element
     * @param idy index of the second element
     */
    static void swap(Tile[] array , int idx , int idy) {
        Tile swap = array[idx];
        array[idx] = array[idy];
        array[idy] = swap;
        swapCoordinates(array[idx], array[idy]);
    }

    static void replace(Tile[] array, int idx, Tile newTile)
    {
        array[idx] = newTile;
        swapCoordinates(array[idx], newTile);
    }

    public static void swapCoordinates(Tile t1, Tile t2){
        int x1 = t1.getX();
        int y1 = t1.getY();
        t1.setX(t2.getX());
        t1.setY(t2.getY());
        t2.setX(x1);
        t2.setY(y1);
    }

    /**
     * This method checks if first element is less than the other element
     *
     * @param v first element
     * @param w second element
     *
     * @return true if the first element is less than the second element
     */
    static <T extends Comparable<T>> boolean less(T v , T w) {
        return v.compareTo(w) < 0;
    }

    /**
     * This method checks if first element is greater than the other element
     *
     * @param v first element
     * @param w second element
     *
     * @return true if the first element is greater than the second element
     */
    static <T extends Comparable<T>> boolean greater(T v , T w) {
        return v.compareTo(w) > 0;
    }
    public static <T extends Comparable<T>> void reverse(T[] array)
    {
        for(int i = 0; i < array.length / 2; i++)
        {
            T temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }
}