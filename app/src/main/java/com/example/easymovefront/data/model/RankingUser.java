package com.example.easymovefront.data.model;

/**
 * Data used in the ranking for each user.
 * It is composed by the name of the user, his score and user id in the database.
 * All this data is needed for coding RankingActivity
 * @see com.example.easymovefront.ui.ranking.RankingActivity
 */
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
