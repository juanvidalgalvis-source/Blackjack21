package com.example.blackjack21.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class Mazo {
    private static final List<String> VALORES = List.of(
            "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");

    private final Carta[] cartas;
    private int cartasRestantes = 52;

    public Mazo() {
        this(crearMazoMezclado());
    }

    Mazo(List<Carta> cartasEnOrdenDeReparto) {
        if (cartasEnOrdenDeReparto.size() != 52
                || new HashSet<>(cartasEnOrdenDeReparto).size() != 52) {
            throw new IllegalArgumentException("El mazo debe contener 52 cartas únicas.");
        }
        cartas = new Carta[52];
        for (int indice = 0; indice < cartas.length; indice++) {
            cartas[indice] = cartasEnOrdenDeReparto.get(cartas.length - 1 - indice);
        }
    }

    private static List<Carta> crearMazoMezclado() {
        Carta[] cartas = new Carta[52];
        int indice = 0;
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : VALORES) {
                cartas[indice++] = new Carta(valor, palo);
            }
        }
        Collections.shuffle(Arrays.asList(cartas));
        return List.of(cartas);
    }

    public Carta sacarCarta() {
        if (estaVacio()) {
            throw new NoSuchElementException("El mazo no tiene cartas.");
        }
        Carta carta = cartas[--cartasRestantes];
        cartas[cartasRestantes] = null;
        return carta;
    }

    public boolean estaVacio() {
        return cartasRestantes == 0;
    }

    public int getCartasRestantes() {
        return cartasRestantes;
    }
}
