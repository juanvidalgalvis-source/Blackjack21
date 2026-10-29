package com.example.blackjack21.controller;

import com.example.blackjack21.model.Carta;
import com.example.blackjack21.model.JuegoBlackjack;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BlackjackController {
    private static final List<String> VALORES_CARTA = List.of(
            "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
    private static final String RUTA_IMAGENES =
            "/com/example/blackjack21/images/PNG-cards-1/";
    private static final double ANCHO_IMAGEN_CARTA = 92;
    private static final double ALTO_IMAGEN_CARTA = 138;

    private final JuegoBlackjack juego = new JuegoBlackjack();
    private final Map<Carta, Image> imagenesCartas = new HashMap<>();
    private final Set<Carta> imagenesFaltantes = new HashSet<>();
    private final Set<Carta> imagenesFaltantesNotificadas = new HashSet<>();
    private Stage stage;

    @FXML
    private HBox manoCartasBox;

    @FXML
    private HBox manoComputadorCartasBox;

    @FXML
    private Label puntajeLabel;

    @FXML
    private Label puntajeComputadorLabel;

    @FXML
    private Label estadoLabel;

    @FXML
    private Label saldoLabel;

    @FXML
    private Label apuestaVigenteLabel;

    @FXML
    private Label cartasRestantesLabel;

    @FXML
    private TextField apuestaTextField;

    @FXML
    private Button pedirCartaButton;

    @FXML
    private Button plantarseButton;

    @FXML
    private Button iniciarRondaButton;

    @FXML
    private Button verReglasButton;

    @FXML
    private Button cerrarJuegoButton;

    // Configura la confirmación que aparece al cerrar la ventana.
    public void configurarCierre(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            if (!confirmarCierre()) {
                event.consume();
            }
        });
    }

    // Carga las imágenes y presenta el estado inicial de la partida.
    @FXML
    private void initialize() {
        cargarImagenes();
        actualizarVista();
    }

    // Pide una carta al modelo y actualiza lo que se ve en pantalla.
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

    // Se planta el jugador y actualiza la pantalla con el resultado.
    @FXML
    private void onPlantarse() {
        juego.plantarse();
        actualizarVista();
    }

    // Valida la apuesta escrita e inicia una ronda.
    @FXML
    private void onIniciarRonda() {
        try {
            juego.iniciarRonda(apuestaTextField.getText());
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            mostrarErrorApuesta(excepcion.getMessage());
            return;
        }
        actualizarVista();
    }

    // Abre una ventana con un resumen de las reglas del juego.
    @FXML
    private void onVerReglas() {
        TextArea contenido = new TextArea(textoReglas());
        contenido.setEditable(false);
        contenido.setWrapText(true);
        contenido.setPrefColumnCount(58);
        contenido.setPrefRowCount(22);

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Reglas de Blackjack 21");
        dialogo.setHeaderText("Resumen de las reglas del juego");
        dialogo.initOwner(stage);
        dialogo.initModality(Modality.WINDOW_MODAL);
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().setPrefSize(540, 500);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialogo.showAndWait();
    }

    // Cierra la aplicación si el jugador confirma la acción.
    @FXML
    private void onCerrarJuego() {
        if (confirmarCierre()) {
            stage.close();
        }
    }

    // Actualiza manos, puntajes, mensajes y controles de la pantalla.
    private void actualizarVista() {
        Set<Carta> faltantesVisibles = new LinkedHashSet<>();
        mostrarMano(manoCartasBox, juego.getMano(), faltantesVisibles);
        mostrarMano(manoComputadorCartasBox, juego.getManoComputador(), faltantesVisibles);
        puntajeLabel.setText("Puntaje: " + juego.getPuntaje());
        puntajeComputadorLabel.setText(juego.getManoComputador().isEmpty()
                ? "Puntaje del computador: —"
                : "Puntaje del computador: " + juego.getPuntajeComputador());
        estadoLabel.setText(juego.getMensajeEstado());
        saldoLabel.setText("Saldo disponible: " + juego.getSaldo() + " fichas");
        apuestaVigenteLabel.setText(juego.getApuestaActual() == null
                ? "Apuesta vigente: ninguna"
                : "Apuesta vigente: " + juego.getApuestaActual() + " fichas");
        cartasRestantesLabel.setText(juego.getEstadoRonda()
                == JuegoBlackjack.EstadoRonda.ESPERANDO_APUESTA
                ? "Cartas restantes en el mazo: —"
                : "Cartas restantes en el mazo: " + juego.getCartasRestantes());
        boolean turnoJugador = juego.getEstadoRonda()
                == JuegoBlackjack.EstadoRonda.TURNO_JUGADOR;
        boolean rondaTerminada = juego.getEstadoRonda()
                == JuegoBlackjack.EstadoRonda.TERMINADA;
        boolean puedeApostar = (juego.getEstadoRonda()
                == JuegoBlackjack.EstadoRonda.ESPERANDO_APUESTA || rondaTerminada)
                && !juego.isJuegoTerminado();
        pedirCartaButton.setDisable(!turnoJugador || juego.mazoEstaVacio());
        plantarseButton.setDisable(!turnoJugador);
        iniciarRondaButton.setDisable(!puedeApostar);
        apuestaTextField.setDisable(!puedeApostar);
        verReglasButton.setDisable(false);
        cerrarJuegoButton.setDisable(false);
        avisarImagenesFaltantes(faltantesVisibles);
    }

    // Carga las imágenes disponibles para las cartas del mazo.
    private void cargarImagenes() {
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : VALORES_CARTA) {
                Carta carta = new Carta(valor, palo);
                URL recurso = getClass().getResource(RUTA_IMAGENES + carta + ".png");
                if (recurso == null) {
                    imagenesFaltantes.add(carta);
                    continue;
                }
                Image imagen = new Image(recurso.toExternalForm(), false);
                if (imagen.isError()) {
                    imagenesFaltantes.add(carta);
                } else {
                    imagenesCartas.put(carta, imagen);
                }
            }
        }
    }

    // Muestra las cartas de una mano como imágenes o como códigos de texto.
    private void mostrarMano(HBox contenedor, List<Carta> mano, Set<Carta> faltantesVisibles) {
        contenedor.getChildren().clear();
        for (Carta carta : mano) {
            Image imagen = imagenesCartas.get(carta);
            if (imagen == null) {
                Label codigoCarta = new Label(carta.toString());
                codigoCarta.setMinSize(ANCHO_IMAGEN_CARTA, ALTO_IMAGEN_CARTA);
                codigoCarta.setPrefSize(ANCHO_IMAGEN_CARTA, ALTO_IMAGEN_CARTA);
                codigoCarta.setStyle("-fx-border-color: #555; -fx-border-radius: 6;"
                        + "-fx-alignment: center; -fx-font-size: 18px;");
                codigoCarta.setAccessibleText("Carta " + carta);
                contenedor.getChildren().add(codigoCarta);
                if (imagenesFaltantes.contains(carta)) {
                    faltantesVisibles.add(carta);
                }
                continue;
            }
            ImageView vistaImagen = new ImageView(imagen);
            vistaImagen.setFitWidth(ANCHO_IMAGEN_CARTA);
            vistaImagen.setFitHeight(ALTO_IMAGEN_CARTA);
            vistaImagen.setPreserveRatio(true);
            vistaImagen.setSmooth(true);
            vistaImagen.setAccessibleText("Carta " + carta);
            contenedor.getChildren().add(vistaImagen);
        }
    }

    // Informa una vez cuando faltan imágenes de cartas que deben mostrarse.
    private void avisarImagenesFaltantes(Set<Carta> faltantesVisibles) {
        Set<Carta> nuevasFaltantes = new LinkedHashSet<>(faltantesVisibles);
        nuevasFaltantes.removeAll(imagenesFaltantesNotificadas);
        if (nuevasFaltantes.isEmpty()) {
            return;
        }
        imagenesFaltantesNotificadas.addAll(nuevasFaltantes);
        String archivos = nuevasFaltantes.stream()
                .map(carta -> carta + ".png")
                .collect(Collectors.joining(", "));
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.initOwner(stage);
        alerta.setTitle("Imagen de carta no disponible");
        alerta.setHeaderText(null);
        alerta.setContentText("No se encontraron estas imágenes: " + archivos
                + ". Se mostrarán las cartas con su código.");
        alerta.showAndWait();
    }

    // Pregunta si se debe cerrar la ventana.
    private boolean confirmarCierre() {
        ButtonType cerrar = new ButtonType("Sí, cerrar", ButtonBar.ButtonData.YES);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas cerrar la aplicación? El progreso de esta partida no se guardará.",
                cerrar, cancelar);
        alerta.initOwner(stage);
        alerta.setTitle("Confirmar cierre");
        alerta.setHeaderText(null);
        return alerta.showAndWait().filter(cerrar::equals).isPresent();
    }

    // Prepara el texto breve que se muestra en la ventana de reglas.
    private String textoReglas() {
        return """
                Objetivo
                Gana quien obtenga el puntaje válido más alto sin superar 21.

                Valores y ases
                Las cartas del 2 al 10 valen su número; J, Q y K valen 10. El palo no cambia el puntaje.
                El As vale 11 si no hace superar 21; de lo contrario se cuenta como 1. Con varios ases
                se busca el mayor puntaje posible sin pasarse.

                Turno del jugador
                Recibes dos cartas y puedes pedir otra o plantarte. Al llegar a 21 el turno termina y
                comienza el del computador. Si superas 21, pierdes inmediatamente y el computador no juega.

                Jugadas especiales
                Blackjack es un As y una carta de valor 10 en las dos cartas iniciales. Vence a cualquier
                mano que no sea Blackjack; dos Blackjack empatan.
                Veintiuno es cualquier 21 que no sea Blackjack.
                Veinte y medio solo se reconoce con J, Q o K más un 4 en las dos cartas iniciales, sin
                importar el palo. Puedes pedir o plantarte; vence a manos válidas de hasta 20, pierde
                contra cualquier 21 y empata contra otro Veinte y medio. No tiene pago adicional.
                Pasarse significa superar 21 después de ajustar los ases.

                Computador
                Espera sin cartas hasta que termina tu turno. Recibe dos cartas y pide mientras su puntaje
                sea menor que 17; se planta con 17 o más, incluido el 17 blando. Sus decisiones dependen
                únicamente de su mano.

                Apuestas y pagos
                Empiezas con 1.000 fichas. La apuesta debe ser entera, múltiplo de 10, de al menos 10
                fichas y no mayor que tu saldo. Se descuenta al iniciar la ronda.
                Derrota: no se acredita nada. Empate: se devuelve la apuesta. Victoria ordinaria: se
                acreditan dos veces la apuesta. Victoria con Blackjack: se acreditan 2,5 veces la apuesta.
                El computador no maneja saldo. Con menos de 10 fichas no puedes iniciar otra ronda.
                """;
    }

    // Muestra un aviso cuando la apuesta ingresada no es válida.
    private void mostrarErrorApuesta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Apuesta no válida");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Informa que no quedan cartas disponibles para repartir.
    private void avisarMazoAgotado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Mazo agotado");
        alerta.setHeaderText(null);
        alerta.setContentText("El mazo se ha agotado. No quedan cartas para repartir");
        alerta.showAndWait();
    }
}
