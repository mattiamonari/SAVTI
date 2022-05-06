package JavaFXVersion.sorting;

import JavaFXVersion.Tail;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.FutureTask;

public class BubbleSort implements SortAlgorithm{

    //Sequential transition which will group both the transitions of the swap
    SequentialTransition s;

    FutureTask<Void> future;

    Thread thread;

    int countComparison = 0, countSwaps = 0;

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
                future.cancel(false);
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
                    countComparison++;
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
                                Thread.currentThread().interrupt();
                            }
                        }
                        countSwaps++;
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
            System.out.println("Comparison: " + countComparison);
            System.out.println("Swaps: " + countSwaps);
            writeImage(array);
        });

        thread.start();


    }

    private void writeImage(Tail[] array, int chunkWidth, int chunkHeight, int cols, int rows){
        //PixelWriter reader = imageToWrite.getPixelReader();
        BufferedImage bufferedImage = new BufferedImage(cols*chunkWidth,rows*chunkHeight,BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < array.length; i++) {
            WritableImage tmp = array[i].getTail();
            IntBuffer buffer = IntBuffer.allocate(chunkHeight*chunkWidth);
            tmp.getPixelReader().getPixels(0,0, chunkWidth, chunkHeight, PixelFormat.getIntArgbInstance(), buffer, 0);
            for (int j = 0; j < buffer.array().length; j++) {
                int col_to_write = i % 4, row_to_write = i/4;
                //TODO FIND RIGHT X-Y COORDINATES
                bufferedImage.setRGB(col_to_write*chunkWidth,row_to_write*chunkHeight, buffer.get(j));
            }
        }
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