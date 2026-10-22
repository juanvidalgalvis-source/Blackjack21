package com.example.blackjack21.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Mazo {
    private static final List<String> VALORES = List.of(
            "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");

    private final ArrayList<Carta> cartas = new ArrayList<>(52);

    public Mazo() {
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : VALORES) {
                cartas.add(new Carta(valor, palo));
            }
        }
        Collections.shuffle(cartas);
    }

    public Carta sacarCarta() {
        if (cartas.isEmpty()) {
            throw new NoSuchElementException("El mazo no tiene cartas.");
        }
        return cartas.remove(cartas.size() - 1);
    }

    public boolean estaVacio() {
        return cartas.isEmpty();
    }

    public int getCartasRestantes() {
        return cartas.size();
    }
}
