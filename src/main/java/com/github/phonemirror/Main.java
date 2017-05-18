package com.github.phonemirror;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static AppComponent component;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

//        component = DaggerAppComponent.create();

        // TODO: don't hardcode values
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(400);

        primaryStage.setTitle("Phone Mirror");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
