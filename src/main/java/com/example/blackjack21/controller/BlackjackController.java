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
    private Label manoComputadorLabel;

    @FXML
    private Label puntajeLabel;

    @FXML
    private Label puntajeComputadorLabel;

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
        String cartasComputador = juego.getManoComputador().stream()
                .map(Carta::toString)
                .collect(Collectors.joining(", "));
        manoComputadorLabel.setText("Mano del computador: " + cartasComputador);
        puntajeLabel.setText("Puntaje: " + juego.getPuntaje());
        puntajeComputadorLabel.setText(juego.getManoComputador().isEmpty()
                ? "Puntaje del computador: —"
                : "Puntaje del computador: " + juego.getPuntajeComputador());
        estadoLabel.setText(juego.getMensajeEstado());
        cartasRestantesLabel.setText("Cartas restantes en el mazo: "
                + juego.getCartasRestantes());
        boolean turnoJugador = juego.getEstadoRonda()
                == JuegoBlackjack.EstadoRonda.TURNO_JUGADOR;
        boolean rondaTerminada = juego.getEstadoRonda()
                == JuegoBlackjack.EstadoRonda.TERMINADA;
        pedirCartaButton.setDisable(!turnoJugador || juego.mazoEstaVacio());
        plantarseButton.setDisable(!turnoJugador);
        iniciarRondaButton.setDisable(!rondaTerminada);
    }

    private void avisarMazoAgotado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Mazo agotado");
        alerta.setHeaderText(null);
        alerta.setContentText("El mazo se ha agotado. No quedan cartas para repartir");
        alerta.showAndWait();
    }
}
