package edu.ifsp.games.gamestore.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class Produto {
    private int id;
    private int plataformaId;
    private String plataformaNome; 
    private String titulo;
    private BigDecimal valorCredito;
    private BigDecimal precoVenda;
    private int estoque;
    private boolean ativo = true;

    public Produto() {}

    public Produto(int id, int plataformaId, String titulo, BigDecimal valorCredito, BigDecimal precoVenda, int estoque, boolean ativo) {
        this.id = id;
        this.plataformaId = plataformaId;
        this.titulo = titulo;
        this.valorCredito = valorCredito;
        this.precoVenda = precoVenda;
        this.estoque = estoque;
        this.ativo = ativo;
    }
}