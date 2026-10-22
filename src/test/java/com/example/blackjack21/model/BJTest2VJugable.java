package com.example.blackjack21.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BJTest2VJugable {
    @Test
    void V02_HU05_CalculaValoresDeCartasYNoDependeDelPalo() {
        List<Carta> mano = List.of(
                new Carta("2", Carta.Palo.P),
                new Carta("J", Carta.Palo.C),
                new Carta("Q", Carta.Palo.D),
                new Carta("K", Carta.Palo.T));

        assertEquals(32, JuegoBlackjack.calcularPuntaje(mano));
    }

    @Test
    void V02_HU05_AjustaUnoYVariosAsesAlMayorPuntajeValido() {
        assertEquals(20, puntaje("As", "9"));
        assertEquals(15, puntaje("As", "9", "5"));
        assertEquals(21, puntaje("As", "As", "9"));
        assertEquals(12, puntaje("As", "As", "K"));
        assertEquals(22, puntaje("As", "As", "K", "K"));
    }

    @Test
    void V02_HU07_DistingueBlackjackVeintiunoVeinteYMedioYPasarse() {
        assertEquals(JuegoBlackjack.SituacionMano.BLACKJACK,
                evaluar(true, "As", "10"));
        assertEquals(JuegoBlackjack.SituacionMano.VEINTIUNO,
                evaluar(false, "7", "7", "7"));
        assertEquals(JuegoBlackjack.SituacionMano.VEINTIUNO,
                evaluar(false, "As", "10"));
        assertEquals(JuegoBlackjack.SituacionMano.VEINTE_Y_MEDIO,
                evaluar(true, new Carta("J", Carta.Palo.P), new Carta("4", Carta.Palo.C)));
        assertEquals(JuegoBlackjack.SituacionMano.VEINTE_Y_MEDIO, evaluar(true, "Q", "4"));
        assertEquals(JuegoBlackjack.SituacionMano.VEINTE_Y_MEDIO, evaluar(true, "K", "4"));
        assertEquals(JuegoBlackjack.SituacionMano.EN_JUEGO, evaluar(true, "9", "5"));
        assertEquals(JuegoBlackjack.SituacionMano.EN_JUEGO, evaluar(true, "As", "3"));
        assertEquals(JuegoBlackjack.SituacionMano.EN_JUEGO, evaluar(true, "10", "4"));
        assertEquals(JuegoBlackjack.SituacionMano.EN_JUEGO, evaluar(false, "J", "4"));
        assertEquals(JuegoBlackjack.SituacionMano.PASARSE,
                evaluar(false, "As", "As", "K", "K"));
    }

    @Test
    void V02_HU07_VeinteYMedioPermiteContinuarYPierdeValidezAlPedir() {
        JuegoBlackjack juego = crearJuego(new Carta("J", Carta.Palo.P),
                new Carta("4", Carta.Palo.C), new Carta("2", Carta.Palo.D));

        assertEquals(14, juego.getPuntaje());
        assertEquals(JuegoBlackjack.SituacionMano.VEINTE_Y_MEDIO,
                juego.getSituacionMano());
        assertTrue(juego.isTurnoJugadorActivo());
        assertTrue(juego.getMensajeEstado().contains("Veinte y medio"));

        juego.pedirCarta();

        assertEquals(16, juego.getPuntaje());
        assertEquals(JuegoBlackjack.SituacionMano.EN_JUEGO,
                juego.getSituacionMano());
        assertTrue(juego.isTurnoJugadorActivo());
    }

    @Test
    void V02_HU06_BlackjackInicialTerminaElTurnoYBloqueaAcciones() {
        JuegoBlackjack juego = crearJuego(new Carta("As", Carta.Palo.P),
                new Carta("Q", Carta.Palo.C));

        assertEquals(JuegoBlackjack.SituacionMano.BLACKJACK,
                juego.getSituacionMano());
        assertFalse(juego.isTurnoJugadorActivo());
        assertThrows(IllegalStateException.class, juego::pedirCarta);
        assertThrows(IllegalStateException.class, juego::plantarse);
    }

    @Test
    void V02_HU06_VeintiunoYPasarseTerminanElTurno() {
        JuegoBlackjack veintiuno = crearJuego(new Carta("7", Carta.Palo.P),
                new Carta("8", Carta.Palo.C), new Carta("6", Carta.Palo.D));
        veintiuno.pedirCarta();

        assertEquals(21, veintiuno.getPuntaje());
        assertEquals(JuegoBlackjack.SituacionMano.VEINTIUNO,
                veintiuno.getSituacionMano());
        assertFalse(veintiuno.isTurnoJugadorActivo());

        JuegoBlackjack pasarse = crearJuego(new Carta("10", Carta.Palo.P),
                new Carta("6", Carta.Palo.C), new Carta("10", Carta.Palo.D));
        pasarse.pedirCarta();

        assertEquals(JuegoBlackjack.SituacionMano.PASARSE,
                pasarse.getSituacionMano());
        assertFalse(pasarse.isTurnoJugadorActivo());
    }

    @Test
    void V02_HU06_PlantarseBloqueaAccionesYPermiteIniciarOtraRonda() {
        JuegoBlackjack juego = crearJuego(new Carta("8", Carta.Palo.P),
                new Carta("5", Carta.Palo.C));
        juego.plantarse();

        assertFalse(juego.isTurnoJugadorActivo());
        assertThrows(IllegalStateException.class, juego::pedirCarta);

        juego.iniciarNuevaRonda();

        assertEquals(2, juego.getMano().size());
        assertEquals(50, juego.getCartasRestantes());
    }

    @Test
    void V02_HU08_NoPermiteIniciarOtraRondaDuranteElTurno() {
        JuegoBlackjack juego = crearJuego(new Carta("8", Carta.Palo.P),
                new Carta("5", Carta.Palo.C));

        assertThrows(IllegalStateException.class, juego::iniciarNuevaRonda);
    }

    private static int puntaje(String... valores) {
        return JuegoBlackjack.calcularPuntaje(
                List.of(valores).stream()
                        .map(valor -> new Carta(valor, Carta.Palo.P))
                        .toList());
    }

    private static JuegoBlackjack.SituacionMano evaluar(boolean inicial, String... valores) {
        List<Carta> mano = List.of(valores).stream()
                .map(valor -> new Carta(valor, Carta.Palo.P))
                .toList();
        return JuegoBlackjack.evaluarMano(mano, inicial);
    }

    private static JuegoBlackjack.SituacionMano evaluar(boolean inicial, Carta... cartas) {
        return JuegoBlackjack.evaluarMano(List.of(cartas), inicial);
    }

    private static JuegoBlackjack crearJuego(Carta... cartasInicialesYRobadas) {
        List<Carta> todasLasCartas = new ArrayList<>();
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : List.of(
                    "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")) {
                todasLasCartas.add(new Carta(valor, palo));
            }
        }
        List<Carta> ordenReparto = new ArrayList<>(List.of(cartasInicialesYRobadas));
        todasLasCartas.removeAll(ordenReparto);
        ordenReparto.addAll(todasLasCartas);
        return new JuegoBlackjack(new Mazo(ordenReparto));
    }
}
