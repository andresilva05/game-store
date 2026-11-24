package edu.ifsp.games.gamestore.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class ItemCarrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    private Produto produto;
    private int quantidade;

    public ItemCarrinho() {
    }

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        if (produto == null || produto.getPrecoVenda() == null) {
            return BigDecimal.ZERO;
        }
        return produto.getPrecoVenda().multiply(BigDecimal.valueOf(quantidade));
    }
}
