package com.example.blackjack21.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BJTest4VEconomia {
    @Test
    // Comprueba que el juego comience sin repartir y con el saldo inicial.
    void V04_HU12_IniciaSinRepartirConSaldoDeMilFichas() {
        JuegoBlackjack juego = new JuegoBlackjack();

        assertEquals(1_000, juego.getSaldo());
        assertTrue(juego.getMano().isEmpty());
        assertTrue(juego.getManoComputador().isEmpty());
        assertNull(juego.getApuestaActual());
        assertEquals(JuegoBlackjack.EstadoRonda.ESPERANDO_APUESTA, juego.getEstadoRonda());
    }

    @Test
    // Comprueba que las apuestas inválidas no repartan cartas ni descuenten saldo.
    void V04_HU12_RechazaApuestasInvalidasSinRepartirNiDescontar() {
        JuegoBlackjack juego = new JuegoBlackjack();

        for (String apuesta : List.of("", "texto", "15", "0", "-10", "10.5", "1010")) {
            assertThrows(IllegalArgumentException.class, () -> juego.iniciarRonda(apuesta),
                    "Debe rechazar la apuesta: " + apuesta);
            assertEquals(1_000, juego.getSaldo());
            assertTrue(juego.getMano().isEmpty());
            assertTrue(juego.getManoComputador().isEmpty());
            assertEquals(JuegoBlackjack.EstadoRonda.ESPERANDO_APUESTA,
                    juego.getEstadoRonda());
        }
    }

    @Test
    // Comprueba que una apuesta válida se descuente al iniciar y no cambie durante la ronda.
    void V04_HU12_DescuentaUnaApuestaValidaAlIniciarYLaMantieneFija() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));

        juego.iniciarRonda("100");

        assertEquals(900, juego.getSaldo());
        assertEquals(100, juego.getApuestaActual());
        assertEquals(2, juego.getMano().size());
        assertTrue(juego.getManoComputador().isEmpty());
        assertThrows(IllegalStateException.class, () -> juego.iniciarRonda("200"));
        assertEquals(900, juego.getSaldo());
        assertEquals(100, juego.getApuestaActual());
    }

    @Test
    // Comprueba que se pueda apostar todo el saldo disponible.
    void V04_HU12_AceptaApostarElSaldoCompleto() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("9", Carta.Palo.T));

        juego.iniciarRonda("1000");

        assertEquals(0, juego.getSaldo());
        assertEquals(1_000, juego.getApuestaActual());
        assertEquals(2, juego.getMano().size());
    }

    @Test
    // Comprueba que una derrota no acredite fichas.
    void V04_HU13_LiquidaUnaDerrotaSinAcreditarFichas() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("7", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        juego.iniciarRonda(100);

        juego.plantarse();

        assertEquals(JuegoBlackjack.ResultadoRonda.DERROTA, juego.getResultadoRonda());
        assertEquals(0, juego.getFichasAcreditadas());
        assertEquals(900, juego.getSaldo());
        assertNull(juego.getApuestaActual());
    }

    @Test
    // Comprueba que un empate devuelva la apuesta.
    void V04_HU13_DevuelveLaApuestaEnUnEmpate() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("8", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        juego.iniciarRonda(100);

        juego.plantarse();

        assertEquals(JuegoBlackjack.ResultadoRonda.EMPATE, juego.getResultadoRonda());
        assertEquals(100, juego.getFichasAcreditadas());
        assertEquals(1_000, juego.getSaldo());
    }

    @Test
    // Comprueba el pago de una victoria ordinaria.
    void V04_HU13_AcreditaDosVecesLaApuestaEnVictoriaOrdinaria() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("10", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        juego.iniciarRonda(100);

        juego.plantarse();

        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, juego.getResultadoRonda());
        assertEquals(200, juego.getFichasAcreditadas());
        assertEquals(1_100, juego.getSaldo());
    }

    @Test
    // Comprueba el pago adicional por una victoria con Blackjack.
    void V04_HU13_AcreditaDosYCincoVecesLaApuestaPorBlackjackInicial() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("As", Carta.Palo.P), carta("K", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("7", Carta.Palo.T));
        juego.iniciarRonda(100);

        assertEquals(JuegoBlackjack.ResultadoRonda.VICTORIA, juego.getResultadoRonda());
        assertEquals(250, juego.getFichasAcreditadas());
        assertEquals(1_150, juego.getSaldo());
    }

    @Test
    // Comprueba que un empate con Blackjack solo devuelva la apuesta.
    void V04_HU13_EmpateConBlackjackDevuelveSoloLaApuesta() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("As", Carta.Palo.P), carta("K", Carta.Palo.C),
                carta("As", Carta.Palo.D), carta("Q", Carta.Palo.T));
        juego.iniciarRonda(100);

        assertEquals(JuegoBlackjack.ResultadoRonda.EMPATE, juego.getResultadoRonda());
        assertEquals(100, juego.getFichasAcreditadas());
        assertEquals(1_000, juego.getSaldo());
    }

    @Test
    // Comprueba que una ronda no liquide la apuesta más de una vez.
    void V04_HU13_NoLiquidaLaApuestaDosVecesAlRepetirAcciones() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("10", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        juego.iniciarRonda(100);
        juego.plantarse();

        assertThrows(IllegalStateException.class, juego::plantarse);
        assertEquals(1_100, juego.getSaldo());
        assertEquals(200, juego.getFichasAcreditadas());
    }

    @Test
    // Comprueba que el juego termine cuando el saldo queda por debajo de diez fichas.
    void V04_HU14_TerminaSiElSaldoBajaDeDiezYBloqueaNuevasApuestas() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("7", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        juego.iniciarRonda(1_000);

        juego.plantarse();

        assertEquals(0, juego.getSaldo());
        assertTrue(juego.isJuegoTerminado());
        assertTrue(juego.getMensajeEstado().contains("Fin del juego"));
        assertThrows(IllegalStateException.class, () -> juego.iniciarRonda(10));
    }

    @Test
    // Comprueba que un saldo de diez fichas aún permita iniciar otra ronda.
    void V04_HU14_ConSaldoExactoDeDiezTodaviaPermiteApostar() {
        JuegoBlackjack juego = juegoDeterminista(
                carta("10", Carta.Palo.P), carta("7", Carta.Palo.C),
                carta("10", Carta.Palo.D), carta("8", Carta.Palo.T));
        juego.iniciarRonda(990);
        juego.plantarse();

        assertEquals(10, juego.getSaldo());
        assertFalse(juego.isJuegoTerminado());

        juego.iniciarRonda(10);

        assertEquals(0, juego.getSaldo());
        assertEquals(10, juego.getApuestaActual());
        assertFalse(juego.isJuegoTerminado());
    }

    // Prepara un juego de prueba con un orden de reparto conocido.
    private static JuegoBlackjack juegoDeterminista(Carta... ordenReparto) {
        List<Carta> cartas = new ArrayList<>();
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : List.of(
                    "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")) {
                cartas.add(carta(valor, palo));
            }
        }
        List<Carta> cartasFijas = new ArrayList<>(List.of(ordenReparto));
        assertEquals(cartasFijas.size(), new HashSet<>(cartasFijas).size());
        cartas.removeAll(cartasFijas);
        cartasFijas.addAll(cartas);
        return new JuegoBlackjack(() -> new Mazo(cartasFijas));
    }

    // Crea una carta para los escenarios de prueba.
    private static Carta carta(String valor, Carta.Palo palo) {
        return new Carta(valor, palo);
    }
}
