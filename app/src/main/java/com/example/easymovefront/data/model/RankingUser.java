package com.example.easymovefront.data.model;

public class RankingUser {

    private String nom;

    private Integer puntuacio;

    private Integer id;

    public RankingUser(String nom, Integer puntuacio, Integer id) {
        this.nom = nom;
        this.puntuacio = puntuacio;
        this.id = id;
    }

    public String getNom() { return nom; }

    public Integer getPuntuacio() { return puntuacio; }

    public Integer getId() { return id; }
}
