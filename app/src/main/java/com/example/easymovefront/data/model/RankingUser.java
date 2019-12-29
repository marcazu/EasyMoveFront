package com.example.easymovefront.data.model;

public class RankingUser {

    private String nom;

    private Integer puntuacio;

    public RankingUser(String nom, Integer puntuacio) {
        this.nom = nom;
        this.puntuacio = puntuacio;
    }

    public String getNom() { return nom; }

    public Integer getPuntuacio() { return puntuacio; }
}
