package edu.ifsp.games.gamestore.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Carrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ItemCarrinho> itens = new ArrayList<>();

    public Carrinho() {
    }

    public ItemCarrinho buscarItemPorProdutoId(int produtoId) {
        for (ItemCarrinho item : itens) {
            if (item.getProduto() != null && item.getProduto().getId() == produtoId) {
                return item;
            }
        }
        return null;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrinho item : itens) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public int getQuantidadeTotal() {
        int total = 0;
        for (ItemCarrinho item : itens) {
            total += item.getQuantidade();
        }
        return total;
    }

    public boolean isVazio() {
        return itens.isEmpty();
    }

    public void limpar() {
        itens.clear();
    }
}
