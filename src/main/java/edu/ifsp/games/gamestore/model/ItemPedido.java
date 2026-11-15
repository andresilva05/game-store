package edu.ifsp.games.gamestore.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ItemPedido {
    private int id;
    private int pedidoId;
    private int produtoId;
    private BigDecimal preco;
    private String pin;
}
