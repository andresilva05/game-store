package edu.ifsp.games.gamestore.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Plataforma {
    private int id;
    private String nome;

    public Plataforma() {}

    public Plataforma(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Plataforma(String nome) {
        this.nome = nome;
    }
}