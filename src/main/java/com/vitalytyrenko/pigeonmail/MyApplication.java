package com.vitalytyrenko.pigeonmail;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;

public class MyApplication extends Application {

    private Universal universal;

    @Override
    public void start(Stage stage) throws IOException {
        Sprites.initialize();
        universal = Universal.getInstance();
        universal.setCreatePigeonStage(createPigeonStage());

        Scene scene = new Scene(new AnchorPane(universal.getNode()));
        scene.setOnKeyPressed(universal);

        stage.setTitle("ГолубПошта");
        stage.getIcons().add(Sprites.getIcon());
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMaximized(true);
        stage.show();

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(20), e -> universal.update(0.02)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    private Stage createPigeonStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("create-pigeon-view.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Створення нового голуба");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
                e -> ((CreatePigeonController) loader.getController()).resetFields());
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}