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
}
