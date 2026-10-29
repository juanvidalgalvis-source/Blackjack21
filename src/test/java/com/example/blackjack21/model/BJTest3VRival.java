package com.example.blackjack21.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BJTest3VRival {
    @Test
    // Comprueba que el computador espere y que ambas manos sean listas enlazadas.
    void V03_HU09_ComputadorEsperaYAmbasManosUsanLinkedList() throws NoSuchFieldException {
        assertEquals(LinkedList.class,
                JuegoBlackjack.class.getDeclaredField("mano").getType());
        assertEquals(LinkedList.class,
                JuegoBlackjack.class.getDeclaredField("manoComputador").getType());

        JuegoBlackjack juego = crearJuego(
                carta("10", Carta.Palo.P), carta("9", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("6", Carta.Palo.T),
                carta("2", Carta.Palo.P));

        assertTrue(juego.getManoComputador().isEmpty());
        assertEquals(JuegoBlackjack.EstadoRonda.TURNO_JUGADOR, juego.getEstadoRonda());

        juego.plantarse();

        assertEquals(3, juego.getManoComputador().size());
        assertEquals(18, juego.getPuntajeComputador());
        assertEquals(JuegoBlackjack.EstadoRonda.TERMINADA, juego.getEstadoRonda());
        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, juego.getResultadoRonda());
    }

    @Test
    // Comprueba que el computador no reciba cartas si el jugador se pasa.
    void V03_HU09_JugadorSePasaYElComputadorNoRecibeCartas() {
        JuegoBlackjack juego = crearJuego(
                carta("10", Carta.Palo.P), carta("6", Carta.Palo.C),
                carta("Q", Carta.Palo.D), carta("10", Carta.Palo.T), carta("7", Carta.Palo.P));

        juego.pedirCarta();

        assertEquals(JuegoBlackjack.SituacionMano.PASARSE, juego.getSituacionMano());
        assertTrue(juego.getManoComputador().isEmpty());
        assertEquals(JuegoBlackjack.ResultadoRonda.DERROTA, juego.getResultadoRonda());
        assertTrue(juego.getMensajeResultado().contains("el computador no jugó"));
    }

    @Test
    // Comprueba que el Blackjack del jugador active automáticamente al computador.
    void V03_HU09_BlackjackDelJugadorIniciaElTurnoComputadorAutomaticamente() {
        JuegoBlackjack juego = crearJuego(
                carta("As", Carta.Palo.P), carta("K", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("7", Carta.Palo.T), carta("2", Carta.Palo.P));

        assertEquals(2, juego.getManoComputador().size());
        assertEquals(JuegoBlackjack.EstadoRonda.TERMINADA, juego.getEstadoRonda());
        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, juego.getResultadoRonda());
        assertTrue(juego.getMensajeResultado().contains("Blackjack"));
    }

    @Test
    // Comprueba que llegar a 21 inicie automáticamente el turno del computador.
    void V03_HU09_VeintiunoAlPedirIniciaElTurnoComputadorAutomaticamente() {
        JuegoBlackjack juego = crearJuego(
                carta("7", Carta.Palo.P), carta("8", Carta.Palo.C), carta("6", Carta.Palo.D),
                carta("10", Carta.Palo.T), carta("6", Carta.Palo.P), carta("2", Carta.Palo.C));

        juego.pedirCarta();

        assertEquals(JuegoBlackjack.SituacionMano.VEINTIUNO, juego.getSituacionMano());
        assertFalse(juego.getManoComputador().isEmpty());
        assertEquals(JuegoBlackjack.EstadoRonda.TERMINADA, juego.getEstadoRonda());
    }

    @Test
    // Comprueba que el computador pida con 16 y se plante con 17.
    void V03_HU10_ComputadorPideCon16YSePlantaCon17() {
        JuegoBlackjack pide = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("6", Carta.Palo.T), carta("2", Carta.Palo.P));
        pide.plantarse();

        assertEquals(3, pide.getManoComputador().size());
        assertEquals(18, pide.getPuntajeComputador());

        JuegoBlackjack sePlanta = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("7", Carta.Palo.T), carta("2", Carta.Palo.P));
        sePlanta.plantarse();

        assertEquals(2, sePlanta.getManoComputador().size());
        assertEquals(17, sePlanta.getPuntajeComputador());
    }

    @Test
    // Comprueba que el computador se plante con un 17 blando.
    void V03_HU10_ComputadorSePlantaCon17Blando() {
        JuegoBlackjack juego = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("As", Carta.Palo.D), carta("6", Carta.Palo.T), carta("2", Carta.Palo.P));

        juego.plantarse();

        assertEquals(2, juego.getManoComputador().size());
        assertEquals(17, juego.getPuntajeComputador());
        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, juego.getResultadoRonda());
    }

    @Test
    // Comprueba que las decisiones del computador no dependan de la mano del jugador.
    void V03_HU10_DecisionesDelComputadorNoDependenDelPuntajeDelJugador() {
        JuegoBlackjack juego = crearJuego(
                carta("K", Carta.Palo.P), carta("Q", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("6", Carta.Palo.T), carta("2", Carta.Palo.P));

        juego.plantarse();

        assertEquals(20, juego.getPuntaje());
        assertEquals(18, juego.getPuntajeComputador());
        assertEquals(3, juego.getManoComputador().size());
    }

    @Test
    // Comprueba que el computador reconozca un Blackjack inicial.
    void V03_HU10_ComputadorReconoceBlackjackInicial() {
        JuegoBlackjack juego = crearJuego(
                carta("10", Carta.Palo.P), carta("6", Carta.Palo.C),
                carta("As", Carta.Palo.D), carta("K", Carta.Palo.T), carta("2", Carta.Palo.P));

        juego.plantarse();

        assertEquals(JuegoBlackjack.SituacionMano.BLACKJACK,
                juego.getSituacionComputador());
        assertEquals(2, juego.getManoComputador().size());
        assertEquals(JuegoBlackjack.ResultadoRonda.DERROTA, juego.getResultadoRonda());
    }

    @Test
    // Comprueba la prioridad de Blackjack y el empate entre dos Blackjack.
    void V03_HU11_BlackjackGanaA21DeTresCartasYDosBlackjackEmpatan() {
        JuegoBlackjack blackjackContraVeintiuno = crearJuego(
                carta("7", Carta.Palo.P), carta("7", Carta.Palo.C), carta("7", Carta.Palo.D),
                carta("As", Carta.Palo.T), carta("K", Carta.Palo.P), carta("2", Carta.Palo.C));
        blackjackContraVeintiuno.pedirCarta();

        assertEquals(JuegoBlackjack.SituacionMano.VEINTIUNO,
                blackjackContraVeintiuno.getSituacionMano());
        assertEquals(JuegoBlackjack.SituacionMano.BLACKJACK,
                blackjackContraVeintiuno.getSituacionComputador());
        assertEquals(JuegoBlackjack.ResultadoRonda.DERROTA,
                blackjackContraVeintiuno.getResultadoRonda());

        JuegoBlackjack dobleBlackjack = crearJuego(
                carta("As", Carta.Palo.P), carta("K", Carta.Palo.C),
                carta("As", Carta.Palo.D), carta("Q", Carta.Palo.T), carta("2", Carta.Palo.P));

        assertEquals(JuegoBlackjack.ResultadoRonda.EMPATE, dobleBlackjack.getResultadoRonda());
        assertEquals(4, new HashSet<>(cartasRepartidas(dobleBlackjack)).size());
    }

    @Test
    // Comprueba la comparación de puntajes y el empate entre manos iguales.
    void V03_HU11_ComparaManosValidasYEmpataConPuntajesIguales() {
        JuegoBlackjack victoria = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("7", Carta.Palo.T));
        victoria.plantarse();
        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, victoria.getResultadoRonda());
        assertTrue(victoria.getMensajeResultado().contains("mayor"));

        JuegoBlackjack empate = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        empate.plantarse();
        assertEquals(JuegoBlackjack.ResultadoRonda.EMPATE, empate.getResultadoRonda());
    }

    @Test
    // Comprueba que el jugador gane cuando el computador se pasa.
    void V03_HU11_ComputadorQueSePasaHaceGanarAlJugador() {
        JuegoBlackjack juego = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("6", Carta.Palo.T), carta("K", Carta.Palo.P));

        juego.plantarse();

        assertEquals(JuegoBlackjack.SituacionMano.PASARSE, juego.getSituacionComputador());
        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, juego.getResultadoRonda());
        assertTrue(juego.getMensajeResultado().contains("computador se pasó"));
    }

    @Test
    // Comprueba las reglas especiales para comparar Veinte y medio.
    void V03_HU11_VeinteYMedioRespetaLaPrioridadDeLaManoRival() {
        JuegoBlackjack ganaContra20 = crearJuego(
                carta("J", Carta.Palo.P), carta("4", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("K", Carta.Palo.T));
        ganaContra20.plantarse();
        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, ganaContra20.getResultadoRonda());

        JuegoBlackjack pierdeContra21 = crearJuego(
                carta("Q", Carta.Palo.P), carta("4", Carta.Palo.C),
                carta("8", Carta.Palo.D), carta("7", Carta.Palo.T), carta("6", Carta.Palo.P));
        pierdeContra21.plantarse();
        assertEquals(21, pierdeContra21.getPuntajeComputador());
        assertEquals(JuegoBlackjack.ResultadoRonda.DERROTA, pierdeContra21.getResultadoRonda());
    }

    @Test
    // Comprueba que el resultado se cierre una vez y luego permita otra ronda.
    void V03_HU11_ResultadoSeCalculaUnaVezYSoloPermiteIniciarRondaAlTerminar() {
        JuegoBlackjack juego = crearJuego(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        assertThrows(IllegalStateException.class, () -> juego.iniciarRonda(10));

        juego.plantarse();
        JuegoBlackjack.ResultadoRonda resultado = juego.getResultadoRonda();
        assertThrows(IllegalStateException.class, juego::plantarse);
        assertEquals(resultado, juego.getResultadoRonda());

        juego.iniciarRonda(10);
        assertEquals(JuegoBlackjack.EstadoRonda.TURNO_JUGADOR, juego.getEstadoRonda());
        assertTrue(juego.getManoComputador().isEmpty());
        assertEquals(JuegoBlackjack.ResultadoRonda.EN_CURSO, juego.getResultadoRonda());
    }

    // Crea una carta para los escenarios de prueba.
    private static Carta carta(String valor, Carta.Palo palo) {
        return new Carta(valor, palo);
    }

    // Prepara un juego con las primeras cartas en un orden conocido.
    private static JuegoBlackjack crearJuego(Carta... cartasInicialesYRobadas) {
        List<Carta> todasLasCartas = new ArrayList<>();
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : List.of(
                    "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")) {
                todasLasCartas.add(carta(valor, palo));
            }
        }
        List<Carta> ordenReparto = new ArrayList<>(List.of(cartasInicialesYRobadas));
        assertEquals(ordenReparto.size(), new HashSet<>(ordenReparto).size(),
                "La prueba no debe repartir la misma carta física más de una vez.");
        todasLasCartas.removeAll(ordenReparto);
        ordenReparto.addAll(todasLasCartas);
        return new JuegoBlackjack(new Mazo(ordenReparto),
                () -> new Mazo(ordenReparto));
    }

    // Reúne las cartas repartidas a ambos participantes.
    private static List<Carta> cartasRepartidas(JuegoBlackjack juego) {
        List<Carta> cartas = new ArrayList<>(juego.getMano());
        cartas.addAll(juego.getManoComputador());
        return cartas;
    }
}
