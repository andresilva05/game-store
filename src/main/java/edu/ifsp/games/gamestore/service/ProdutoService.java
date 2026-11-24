package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.ProdutoDAO;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Produto;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService() {
        this.produtoDAO = new ProdutoDAO();
    }

    // Construtor para testes unitários com injeção de dependência / mock
    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void salvar(Produto produto) {
        validarProduto(produto);

        if (produto.getId() == 0) {
            produtoDAO.inserir(produto);
        } else {
            produtoDAO.atualizar(produto);
        }
    }

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public Produto buscarPorId(int id) {
        Produto produto = produtoDAO.buscarPorId(id);
        if (produto == null) {
            throw new RegraNegocioException("Gift Card não encontrado para o ID: " + id);
        }
        return produto;
    }

    public void alternarStatus(int id, boolean ativo) {
        buscarPorId(id); // Garante que o produto existe
        produtoDAO.alternarStatus(id, ativo);
    }

    public void excluir(int id) {
        buscarPorId(id); // Garante que o produto existe

        // REGRA DE NEGÓCIO OBRIGATÓRIA:
        if (produtoDAO.possuiVendas(id)) {
            throw new RegraNegocioException(
                "Não é permitido excluir este Gift Card pois ele já possui vendas registradas no sistema. " +
                "Para tirá-lo de circulação, altere o status para 'Inativo'."
            );
        }

        produtoDAO.excluir(id);
    }

    private void validarProduto(Produto p) {
        if (p == null) {
            throw new RegraNegocioException("Os dados do Gift Card não foram informados.");
        }

        if (p.getTitulo() == null || p.getTitulo().trim().isEmpty()) {
            throw new RegraNegocioException("O título do Gift Card é obrigatório.");
        }

        if (p.getPlataformaId() <= 0) {
            throw new RegraNegocioException("Selecione uma plataforma válida.");
        }

        if (p.getValorCredito() == null || p.getValorCredito().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor de crédito deve ser maior que zero (R$ 0,00).");
        }

        if (p.getPrecoVenda() == null || p.getPrecoVenda().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O preço de venda não pode ser negativo.");
        }

        if (p.getEstoque() < 0) {
            throw new RegraNegocioException("A quantidade de estoque não pode ser negativa.");
        }
    }
}