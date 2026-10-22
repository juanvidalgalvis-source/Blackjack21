package com.example.blackjack21.controller;

import com.example.blackjack21.model.Carta;
import com.example.blackjack21.model.JuegoBlackjack;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.stream.Collectors;

public class BlackjackController {
    private final JuegoBlackjack juego = new JuegoBlackjack();

    @FXML
    private Label manoLabel;

    @FXML
    private Label puntajeLabel;

    @FXML
    private Label estadoLabel;

    @FXML
    private Label cartasRestantesLabel;

    @FXML
    private Button pedirCartaButton;

    @FXML
    private Button plantarseButton;

    @FXML
    private Button iniciarRondaButton;

    @FXML
    private void initialize() {
        actualizarVista();
    }

    @FXML
    private void onPedirCarta() {
        if (juego.mazoEstaVacio()) {
            avisarMazoAgotado();
            return;
        }

        juego.pedirCarta();
        actualizarVista();

        if (juego.mazoEstaVacio()) {
            avisarMazoAgotado();
        }
    }

    @FXML
    private void onPlantarse() {
        juego.plantarse();
        actualizarVista();
    }

    @FXML
    private void onIniciarRonda() {
        juego.iniciarNuevaRonda();
        actualizarVista();
    }

    private void actualizarVista() {
        String cartas = juego.getMano().stream()
                .map(Carta::toString)
                .collect(Collectors.joining(", "));
        manoLabel.setText("Tu mano: " + cartas);
        puntajeLabel.setText("Puntaje: " + juego.getPuntaje());
        estadoLabel.setText(juego.getMensajeEstado());
        cartasRestantesLabel.setText("Cartas restantes en el mazo: "
                + juego.getCartasRestantes());
        boolean turnoActivo = juego.isTurnoJugadorActivo();
        pedirCartaButton.setDisable(!turnoActivo || juego.mazoEstaVacio());
        plantarseButton.setDisable(!turnoActivo);
        iniciarRondaButton.setDisable(turnoActivo);
    }

    private void avisarMazoAgotado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Mazo agotado");
        alerta.setHeaderText(null);
        alerta.setContentText("El mazo se ha agotado. No quedan cartas para repartir");
        alerta.showAndWait();
    }
}
