package com.example.blackjack21.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BJTest1VBase {
    @Test
    void mazoContiene52CartasUnicas() {
        Mazo mazo = new Mazo();
        Set<Carta> cartas = new HashSet<>();

        while (!mazo.estaVacio()) {
            cartas.add(mazo.sacarCarta());
        }

        assertEquals(52, cartas.size());
        assertTrue(mazo.estaVacio());
    }

    @Test
    void partidaEmpiezaConDosCartasYDeja50EnElMazo() {
        JuegoBlackjack juego = new JuegoBlackjack();

        assertEquals(2, juego.getMano().size());
        assertEquals(50, juego.getCartasRestantes());
    }

    @Test
    void mazoYManoUsanLasEstructurasSolicitadas() throws NoSuchFieldException {
        assertEquals(ArrayList.class, Mazo.class.getDeclaredField("cartas").getType());
        assertEquals(LinkedList.class,
                JuegoBlackjack.class.getDeclaredField("mano").getType());
    }

    @Test
    void pedirCartaAgregaUnaCartaALaManoDinamica() {
        JuegoBlackjack juego = new JuegoBlackjack();

        juego.pedirCarta();

        assertEquals(3, juego.getMano().size());
        assertEquals(49, juego.getCartasRestantes());
    }

    @Test
    void juegoNoReparteCartasRepetidasNiPermitePedirConElMazoVacio() {
        JuegoBlackjack juego = new JuegoBlackjack();

        while (!juego.mazoEstaVacio()) {
            juego.pedirCarta();
        }

        assertEquals(52, juego.getMano().size());
        assertEquals(52, new HashSet<>(juego.getMano()).size());
        assertThrows(java.util.NoSuchElementException.class, juego::pedirCarta);
    }

    @Test
    void cartaSeMuestraConLaAbreviaturaDeValorYPalo() {
        assertEquals("4P", new Carta("4", Carta.Palo.P).toString());
        assertEquals("AsC", new Carta("As", Carta.Palo.C).toString());
        assertEquals("KD", new Carta("K", Carta.Palo.D).toString());
    }
}
