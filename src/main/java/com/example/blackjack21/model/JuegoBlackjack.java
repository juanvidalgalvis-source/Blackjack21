package com.example.blackjack21.model;

import java.util.LinkedList;
import java.util.List;

public class JuegoBlackjack {
    public enum SituacionMano {
        EN_JUEGO,
        BLACKJACK,
        VEINTIUNO,
        VEINTE_Y_MEDIO,
        PASARSE
    }

    private Mazo mazo;
    private final LinkedList<Carta> mano = new LinkedList<>();
    private boolean turnoJugadorActivo;
    private SituacionMano situacionMano;

    public JuegoBlackjack() {
        this(new Mazo());
    }

    JuegoBlackjack(Mazo mazo) {
        this.mazo = mazo;
        repartirManoInicial();
    }

    private void repartirManoInicial() {
        mano.add(mazo.sacarCarta());
        mano.add(mazo.sacarCarta());
        turnoJugadorActivo = true;
        actualizarSituacion(true);
    }

    public Carta pedirCarta() {
        if (!turnoJugadorActivo) {
            throw new IllegalStateException("El turno del jugador ya terminó.");
        }
        Carta carta = mazo.sacarCarta();
        mano.add(carta);
        actualizarSituacion(false);
        return carta;
    }

    public void plantarse() {
        if (!turnoJugadorActivo) {
            throw new IllegalStateException("El turno del jugador ya terminó.");
        }
        turnoJugadorActivo = false;
        situacionMano = SituacionMano.EN_JUEGO;
    }

    public void iniciarNuevaRonda() {
        if (turnoJugadorActivo) {
            throw new IllegalStateException("No se puede iniciar otra ronda durante el turno actual.");
        }
        mazo = new Mazo();
        mano.clear();
        repartirManoInicial();
    }

    private void actualizarSituacion(boolean manoInicial) {
        situacionMano = evaluarMano(mano, manoInicial);
        turnoJugadorActivo = situacionMano == SituacionMano.EN_JUEGO
                || situacionMano == SituacionMano.VEINTE_Y_MEDIO;
    }

    public static int calcularPuntaje(List<Carta> mano) {
        int puntaje = 0;
        int ases = 0;
        for (Carta carta : mano) {
            puntaje += carta.getPuntajeBase();
            if ("As".equals(carta.valor())) {
                ases++;
            }
        }
        while (puntaje > 21 && ases > 0) {
            puntaje -= 10;
            ases--;
        }
        return puntaje;
    }

    public static SituacionMano evaluarMano(List<Carta> mano, boolean manoInicial) {
        int puntaje = calcularPuntaje(mano);
        if (puntaje > 21) {
            return SituacionMano.PASARSE;
        }
        if (manoInicial && mano.size() == 2 && esBlackjack(mano)) {
            return SituacionMano.BLACKJACK;
        }
        if (puntaje == 21) {
            return SituacionMano.VEINTIUNO;
        }
        if (puntaje == 14) {
            return SituacionMano.VEINTE_Y_MEDIO;
        }
        return SituacionMano.EN_JUEGO;
    }

    private static boolean esBlackjack(List<Carta> mano) {
        return mano.stream().anyMatch(carta -> "As".equals(carta.valor()))
                && mano.stream().anyMatch(carta -> carta.getPuntajeBase() == 10);
    }

    public List<Carta> getMano() {
        return List.copyOf(mano);
    }

    public int getPuntaje() {
        return calcularPuntaje(mano);
    }

    public SituacionMano getSituacionMano() {
        return situacionMano;
    }

    public boolean isTurnoJugadorActivo() {
        return turnoJugadorActivo;
    }

    public String getMensajeEstado() {
        return switch (situacionMano) {
            case BLACKJACK -> "Blackjack. Tu turno ha terminado.";
            case VEINTIUNO -> "Veintiuno. Tu turno ha terminado.";
            case VEINTE_Y_MEDIO -> "Veinte y medio. Puedes pedir carta o plantarte.";
            case PASARSE -> "Pasarse: superaste 21. Pierdes la ronda.";
            case EN_JUEGO -> turnoJugadorActivo
                    ? "Tu turno: puedes pedir carta o plantarte."
                    : "Te plantaste. La ronda ha terminado.";
        };
    }

    public int getCartasRestantes() {
        return mazo.getCartasRestantes();
    }

    public boolean mazoEstaVacio() {
        return mazo.estaVacio();
    }
}
