package com.example.blackjack21.model;

public record Carta(String valor, Palo palo) {
    public enum Palo {
        P, C, D, T
    }

    public int getPuntajeBase() {
        return switch (valor) {
            case "As" -> 11;
            case "J", "Q", "K" -> 10;
            default -> Integer.parseInt(valor);
        };
    }

    @Override
    public String toString() {
        return valor + palo;
    }
}
