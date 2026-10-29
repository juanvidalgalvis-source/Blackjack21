package com.example.blackjack21.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BJTest5VRecursosImagenes {
    @Test
    void V05_HU15_ExisteUnaImagenParaCadaCartaDelMazo() {
        int imagenesEncontradas = 0;
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (String valor : List.of(
                    "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")) {
                Carta carta = new Carta(valor, palo);
                String ruta = "/com/example/blackjack21/images/PNG-cards-1/"
                        + carta + ".png";

                assertNotNull(getClass().getResource(ruta),
                        "Falta la imagen correspondiente a " + carta);
                imagenesEncontradas++;
            }
        }

        assertEquals(52, imagenesEncontradas);
    }
}
