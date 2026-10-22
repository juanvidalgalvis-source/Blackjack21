package com.example.blackjack21;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BlackjackApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                BlackjackApplication.class.getResource("view/blackjack-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 480);
        stage.setTitle("Blackjack 21 - Reparto de cartas");
        stage.setScene(scene);
        stage.show();
    }
}
