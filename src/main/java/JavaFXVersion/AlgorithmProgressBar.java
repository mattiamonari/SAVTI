package JavaFXVersion;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AlgorithmProgressBar extends VBox {

    ProgressBar progressBar;

    ProgressIndicator progressIndicator;

    Label algoName;

    HBox container;

    public AlgorithmProgressBar(String algorithmName) {

        progressIndicator = new ProgressIndicator(0);
        progressBar = new ProgressBar(0);
        container = new HBox(progressBar, progressIndicator);
        algoName = new Label();
        progressIndicator.progressProperty().bind(progressBar.progressProperty());
        progressIndicator.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        progressBar.setPrefWidth(1000);
        progressBar.setMinWidth(1000);
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        progressIndicator.setMinHeight(65);
        progressIndicator.setPrefHeight(65);
        System.out.println();
        algoName.setText(algorithmName);
        algoName.setFont(Font.font("", FontWeight.BOLD, 33));
        algoName.setAlignment(Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(algoName, container);
        HBox.setMargin(progressBar, new Insets(10));
    }

    public void setAlgoName(String name) {
        algoName.setText(name);
    }

    public void setProgress(double progess) {
        Platform.runLater(() -> {
            progressBar.setProgress(progess);
        });
    }
}
