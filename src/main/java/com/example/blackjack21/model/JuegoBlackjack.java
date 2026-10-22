package com.example.blackjack21.model;

import java.util.LinkedList;
import java.util.List;

public class JuegoBlackjack {
    private final Mazo mazo = new Mazo();
    private final LinkedList<Carta> mano = new LinkedList<>();

    public JuegoBlackjack() {
        mano.add(mazo.sacarCarta());
        mano.add(mazo.sacarCarta());
    }

    public Carta pedirCarta() {
        Carta carta = mazo.sacarCarta();
        mano.add(carta);
        return carta;
    }

    public List<Carta> getMano() {
        return List.copyOf(mano);
    }

    public int getCartasRestantes() {
        return mazo.getCartasRestantes();
    }

    public boolean mazoEstaVacio() {
        return mazo.estaVacio();
    }
}
