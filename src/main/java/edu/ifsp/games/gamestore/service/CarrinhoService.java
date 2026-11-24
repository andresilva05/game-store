package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.ProdutoDAO;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Carrinho;
import edu.ifsp.games.gamestore.model.ItemCarrinho;
import edu.ifsp.games.gamestore.model.Produto;

public class CarrinhoService {

    private final ProdutoDAO produtoDAO;

    public CarrinhoService() {
        this.produtoDAO = new ProdutoDAO();
    }

    // Construtor para testes unitários com injeção de dependência / stub
    public CarrinhoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void adicionarItem(Carrinho carrinho, int produtoId, int quantidade) {
        if (carrinho == null) {
            throw new RegraNegocioException("Carrinho inválido.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade informada deve ser maior que zero.");
        }

        Produto produto = produtoDAO.buscarPorId(produtoId);
        if (produto == null) {
            throw new RegraNegocioException("Gift Card não encontrado.");
        }
        if (!produto.isAtivo()) {
            throw new RegraNegocioException("Este Gift Card não está mais disponível para venda.");
        }
        if (produto.getEstoque() <= 0) {
            throw new RegraNegocioException("Este Gift Card está esgotado no momento.");
        }

        ItemCarrinho itemExistente = carrinho.buscarItemPorProdutoId(produtoId);
        int novaQuantidade = (itemExistente != null)
                ? itemExistente.getQuantidade() + quantidade
                : quantidade;

        if (novaQuantidade > produto.getEstoque()) {
            throw new RegraNegocioException(
                    "Quantidade solicitada excede o estoque disponível (" + produto.getEstoque() + " un.)."
            );
        }

        if (itemExistente != null) {
            itemExistente.setProduto(produto);
            itemExistente.setQuantidade(novaQuantidade);
        } else {
            carrinho.getItens().add(new ItemCarrinho(produto, quantidade));
        }
    }

    public void alterarQuantidade(Carrinho carrinho, int produtoId, int novaQuantidade) {
        if (carrinho == null) {
            throw new RegraNegocioException("Carrinho inválido.");
        }

        ItemCarrinho item = carrinho.buscarItemPorProdutoId(produtoId);
        if (item == null) {
            throw new RegraNegocioException("Item não encontrado no carrinho.");
        }

        if (novaQuantidade < 0) {
            throw new RegraNegocioException("A quantidade não pode ser negativa.");
        }

        if (novaQuantidade == 0) {
            carrinho.getItens().remove(item);
            return;
        }

        Produto produto = produtoDAO.buscarPorId(produtoId);
        if (produto == null || !produto.isAtivo()) {
            throw new RegraNegocioException("Este Gift Card não está mais disponível para venda.");
        }

        if (novaQuantidade > produto.getEstoque()) {
            throw new RegraNegocioException(
                    "Quantidade solicitada excede o estoque disponível (" + produto.getEstoque() + " un.)."
            );
        }

        item.setProduto(produto);
        item.setQuantidade(novaQuantidade);
    }

    public void removerItem(Carrinho carrinho, int produtoId) {
        if (carrinho == null) {
            throw new RegraNegocioException("Carrinho inválido.");
        }

        ItemCarrinho item = carrinho.buscarItemPorProdutoId(produtoId);
        if (item != null) {
            carrinho.getItens().remove(item);
        }
    }

    public void limparCarrinho(Carrinho carrinho) {
        if (carrinho != null) {
            carrinho.limpar();
        }
    }
}
