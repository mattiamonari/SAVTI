package JavaFXVersion.sorting;

final class SortUtils {
    /**
     * Helper method for swapping places in array
     *
     * @param array The array which elements we want to swap
     * @param idx index of the first element
     * @param idy index of the second element
     */
    static <T> void swap(T[] array , int idx , int idy) {
        T swap = array[idx];
        array[idx] = array[idy];
        array[idy] = swap;
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
}