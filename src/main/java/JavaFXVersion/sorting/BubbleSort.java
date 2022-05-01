package JavaFXVersion.sorting;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import mattia.Tail;

import java.util.concurrent.FutureTask;

public class BubbleSort {

    //Sequential transition which will group both the transitions of the swap
    SequentialTransition s;

    //Random object used for lock the threads in this class
    //TODO Use the Lock class!
    final Object lock = new String("Ciaoo");


    public BubbleSort(){
        s = new SequentialTransition();
    }


    public void sort(Tail[] array, GridPane gridPane) {

        //We use a new thread to pause/resume its execution whenever we want
        Thread thread = new Thread(() -> {

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
                        FutureTask<Void> future = new FutureTask<>(
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
                                e.printStackTrace();
                                break;
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
}