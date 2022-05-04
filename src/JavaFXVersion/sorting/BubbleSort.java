package JavaFXVersion.sorting;

import JavaFXVersion.Tail;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.FutureTask;

public class BubbleSort implements SortAlgorithm{

    //Sequential transition which will group both the transitions of the swap
    SequentialTransition s;

    FutureTask<Void> future;

    Thread thread;

    //Random object used for lock the threads in this class
    //TODO Use the Lock class!
    final Object lock = new String("Ciaoo");


    public BubbleSort(){
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

        //We use a new thread to pause/resume its execution whenever we want
        thread = new Thread(() -> {

            long start = System.nanoTime();

            for (int i = 1, size = array.length; i < size; ++i) {
                boolean swapped = false;
                for (int j = 0; j < size - i; ++j) {
                    if (SortUtils.greater(array[j], array[j + 1])) {

                        //So that j is effectively final
                        int finalJ = j;

                        /*
                        A FutureTask can be used to wrap a Callable or Runnable object. Because FutureTask implements Runnable,
                        a FutureTask can be submitted to an Executor for execution.In addition to serving as a standalone class,
                         this class provides protected functionality that may be useful when creating customized task classes.
                         This class implements runnable
                         */
                        future = new FutureTask<>(
                                () -> {
                                    swapNodes( gridPane, array[finalJ], array[finalJ +1]);
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


                        SortUtils.swap(array , j , j + 1);
                        swapped = true;
                    }
                }
                if (!swapped) {
                    break;
                }
            }
            long end = System.nanoTime();
            System.out.println(Math.floorDiv(end-start, 1000000));
        });

        thread.start();


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

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }

    @Override
    public <T extends Comparable<T>> List<T> sort(List<T> unsorted) {
        return SortAlgorithm.super.sort(unsorted);
    }
}