package com.example.blackjack21.model;

public record Carta(String valor, Palo palo) {
    public enum Palo {
        P, C, D, T
    }

    @Override
    public String toString() {
        return valor + palo;
    }
}
