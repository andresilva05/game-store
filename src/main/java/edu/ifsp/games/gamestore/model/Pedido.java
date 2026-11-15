package edu.ifsp.games.gamestore.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Pedido {

    private int id;
    private int idUsuario ;
    private Date dataCompra;
    private BigDecimal valorCompra;

    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(int id, int idUsuario, Date dataCompra, BigDecimal valorCompra, List<ItemPedido> itens) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.dataCompra = dataCompra;
        this.valorCompra = valorCompra;
        this.itens = itens;
    }
}
