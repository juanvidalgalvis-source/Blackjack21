package com.example.blackjack21.model;

public record Carta(String valor, Palo palo) {
    public enum Palo {
        P, C, D, T
    }

    // Devuelve el valor base de la carta para calcular el puntaje.
    public int getPuntajeBase() {
        return switch (valor) {
            case "As" -> 11;
            case "J", "Q", "K" -> 10;
            default -> Integer.parseInt(valor);
        };
    }

    // Muestra la carta usando su valor y la inicial de su palo.
    @Override
    public String toString() {
        return valor + palo;
    }
}
