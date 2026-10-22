package com.example.blackjack21.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BJTest1VBase {
    @Test
    void V01_HU01_MazoContiene52CartasUnicas() {
        Mazo mazo = new Mazo();
        Set<Carta> cartas = new HashSet<>();

        while (!mazo.estaVacio()) {
            cartas.add(mazo.sacarCarta());
        }

        assertEquals(52, cartas.size());
        assertTrue(mazo.estaVacio());
    }

    @Test
    void V01_HU01_ManoInicialTieneDosCartasYQuedan50() {
        JuegoBlackjack juego = new JuegoBlackjack();

        assertEquals(2, juego.getMano().size());
        assertEquals(50, juego.getCartasRestantes());
    }

    @Test
    void V01_HU01_MazoYManoUsanLasEstructurasSolicitadas() throws NoSuchFieldException {
        assertEquals(Carta[].class, Mazo.class.getDeclaredField("cartas").getType());
        assertEquals(LinkedList.class,
                JuegoBlackjack.class.getDeclaredField("mano").getType());
    }

    @Test
    void V01_HU02_PedirCartaAgregaUnaCartaEnOrden() {
        JuegoBlackjack juego = new JuegoBlackjack();
        var manoInicial = juego.getMano();

        juego.pedirCarta();
        var manoActualizada = juego.getMano();

        assertEquals(3, manoActualizada.size());
        assertEquals(manoInicial, manoActualizada.subList(0, manoInicial.size()));
        assertEquals(49, juego.getCartasRestantes());
    }

    @Test
    void V01_HU03_MazoAgotadoNoRepiteCartasYRechazaOtraExtraccion() {
        Mazo mazo = new Mazo();
        Set<Carta> cartas = new HashSet<>();

        while (!mazo.estaVacio()) {
            cartas.add(mazo.sacarCarta());
        }

        assertEquals(52, cartas.size());
        assertThrows(java.util.NoSuchElementException.class, mazo::sacarCarta);
    }

    @Test
    void V01_HU04_CartaSeRepresentaConLaAbreviaturaDeValorYPalo() {
        assertEquals("4P", new Carta("4", Carta.Palo.P).toString());
        assertEquals("AsC", new Carta("As", Carta.Palo.C).toString());
        assertEquals("KD", new Carta("K", Carta.Palo.D).toString());
    }
}
