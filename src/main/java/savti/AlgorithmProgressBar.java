package savti;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AlgorithmProgressBar extends VBox {

    private ProgressBar progressBar;

    private ProgressIndicator progressIndicator;

    private Label algoName;

    private HBox container;

    public AlgorithmProgressBar(String algorithmName) {

        initComponents();

        setAlgoName(algorithmName);

    }

    private void initComponents() {
        progressIndicator = new ProgressIndicator(0);
        progressBar = new ProgressBar(0);
        container = new HBox(progressBar, progressIndicator);
        algoName = new Label();

        progressBar.setPrefWidth(1000);
        progressBar.setMinWidth(1000);
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);

        progressIndicator.setMinHeight(70);
        progressIndicator.setPrefHeight(70);
        progressIndicator.progressProperty().bind(progressBar.progressProperty());

        algoName.setFont(Font.font("", FontWeight.BOLD, 33));
        algoName.setAlignment(Pos.CENTER);

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(algoName, container);
        HBox.setMargin(progressBar, new Insets(10));
    }

    public void setAlgoName(String name) {
        Platform.runLater(() -> algoName.setText(name));
    }

    public void setProgress(double progess) {
        Platform.runLater(() -> progressBar.setProgress(progess));
    }
}
