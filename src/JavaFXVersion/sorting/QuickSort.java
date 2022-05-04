package JavaFXVersion.sorting;

import JavaFXVersion.Tail;
import JavaFXVersion.sorting.SortUtils;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.concurrent.FutureTask;

import static JavaFXVersion.sorting.SortUtils.*;

public class QuickSort implements SortAlgorithm {

    //Sequential transition which will group both the transitions of the swap
    SequentialTransition s;

    FutureTask<Void> future;

    Thread thread;

    //Random object used for lock the threads in this class
    //TODO Use the Lock class!
    final Object lock = new String("Ciaoo");

    public QuickSort() {
        s = new SequentialTransition();
    }

    public boolean isThreadAlive(){
        if(thread != null){
            return thread.isAlive();
        }
        return false;
    }

    @Override
    public void killTask() {
        if (thread != null) {
            if (thread.isAlive()) {
                thread.interrupt();
                future.cancel(true);
            }
        }
    }

    @Override
    public void sort(Tail[] array , GridPane gridPane) {
        thread = new Thread(() -> {
            long start = System.nanoTime();
            doSort(array,0,array.length - 1, gridPane);
            long end = System.nanoTime();
            System.out.println(Math.floorDiv(end-start, 1000000));
        });
        thread.start();
    }

    /**
     * This method implements the Generic Quick Sort
     *
     * @param array The array to be sorted Sorts the array in increasing order
     */
    @Override
    public <T extends Comparable<T>> T[] sort(T[] array) {
        return array;
    }


    private <T extends Comparable<T>> void doSort(Tail[] array, int left, int right, GridPane gridPane) {
        if (left < right) {
            int pivot = randomPartition(array, left, right, gridPane);
            doSort(array, left, pivot - 1, gridPane);
            doSort(array, pivot, right, gridPane);
        }
    }

    /**
     * Ramdomize the array to avoid the basically ordered sequences
     *
     * @param array The array to be sorted
     * @param left The first index of an array
     * @param right The last index of an array
     * @return the partition index of the array
     */
    private <T extends Comparable<T>> int randomPartition(Tail[] array, int left, int right, GridPane gridPane) {
        int randomIndex = left + (int) (Math.random() * (right - left + 1));
        future = new FutureTask<>(
                () -> {
                    swapNodes( gridPane, array[randomIndex], array[right]);
                    return null;
                }
        );
        synchronized (lock) {
            try {
                //The future task gets run on the Javafx Thread (in order to perform gui action)
                Platform.runLater(future);

                lock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        swap(array, randomIndex, right);

        return partition(array, left, right, gridPane);
    }

    /**
     * This method finds the partition index for an array
     *
     * @param array The array to be sorted
     * @param left The first index of an array
     * @param right The last index of an array Finds the partition index of an
     * array
     */
    private <T extends Comparable<T>> int partition(Tail[] array, int left, int right, GridPane gridPane) {
        int mid = (left + right) >>> 1;
        Tail pivot = array[mid];

        while (left <= right) {
            while (less(array[left], pivot)) {
                ++left;
            }
            while (less(pivot, array[right])) {
                --right;
            }
            if (left <= right) {
                int finalLeft = left;
                int finalRight = right;
                future = new FutureTask<>(
                        () -> {
                            swapNodes( gridPane, array[finalLeft], array[finalRight]);
                            return null;
                        }
                );
                synchronized (lock) {
                    try {
                        //The future task gets run on the Javafx Thread (in order to perform gui action)
                        Platform.runLater(future);

                        lock.wait();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        //break;
                        Thread.currentThread().interrupt();
                    }
                }

                swap(array, left, right);
                ++left;
                --right;
            }
        }
        return left;
    }

    private void swapNodes(GridPane container, Tail first, Tail sec) {
        int first_col = GridPane.getColumnIndex(first);
        int first_row = GridPane.getRowIndex(first);
        int second_col = GridPane.getColumnIndex(sec);
        int second_row = GridPane.getRowIndex(sec);

        TranslateTransition tt = new TranslateTransition(Duration.millis(0.1), first);
        TranslateTransition tt1 = new TranslateTransition(Duration.millis(0.1), sec);

        tt.setToX(sec.getX());
        tt.setToY(sec.getY());

        tt1.setToX(first.getX());
        tt1.setToY(first.getY());

        s.getChildren().addAll(tt,tt1);

        s.play();

        s.setOnFinished(event ->{

            s.getChildren().removeAll(s.getChildren());
            synchronized (lock) {
                try {
                    lock.notify();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        container.getChildren().removeAll(first,sec);
        container.add(sec, first_col, first_row);
        container.add(first, second_col, second_row);
    }

}