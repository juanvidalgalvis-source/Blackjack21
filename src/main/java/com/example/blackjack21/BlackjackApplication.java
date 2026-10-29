package com.example.blackjack21;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.blackjack21.controller.BlackjackController;

import java.io.IOException;

public class BlackjackApplication extends Application {
    // Carga la vista principal y muestra la ventana del juego.
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                BlackjackApplication.class.getResource("view/blackjack-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 760, 720);
        stage.setTitle("Blackjack 21 - Reparto de cartas");
        stage.setMinWidth(640);
        stage.setMinHeight(680);
        stage.setScene(scene);
        BlackjackController controller = fxmlLoader.getController();
        controller.configurarCierre(stage);
        stage.show();
    }
}
