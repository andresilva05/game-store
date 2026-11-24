package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.ProdutoDAO;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoServiceTest {

    @Test
    @DisplayName("Nao deve permitir produto com estoque negativo")
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        ProdutoDAO daoFake = new ProdutoDAO();
        ProdutoService service = new ProdutoService(daoFake);

        Produto p = new Produto(0, 1, "Steam R$ 50", new BigDecimal("50.00"), new BigDecimal("45.00"), -1, true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.salvar(p));
        assertEquals("A quantidade de estoque não pode ser negativa.", ex.getMessage());
    }

    @Test
    @DisplayName("Nao deve permitir produto com preco de venda negativo")
    void deveLancarExcecaoQuandoPrecoVendaNegativo() {
        ProdutoDAO daoFake = new ProdutoDAO();
        ProdutoService service = new ProdutoService(daoFake);

        Produto p = new Produto(0, 1, "Steam R$ 50", new BigDecimal("50.00"), new BigDecimal("-10.00"), 5, true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.salvar(p));
        assertEquals("O preço de venda não pode ser negativo.", ex.getMessage());
    }

    @Test
    @DisplayName("Nao deve permitir produto com valor de credito menor ou igual a zero")
    void deveLancarExcecaoQuandoValorCreditoZeroOuNegativo() {
        ProdutoDAO daoFake = new ProdutoDAO();
        ProdutoService service = new ProdutoService(daoFake);

        Produto p = new Produto(0, 1, "Steam R$ 50", BigDecimal.ZERO, new BigDecimal("45.00"), 5, true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.salvar(p));
        assertEquals("O valor de crédito deve ser maior que zero (R$ 0,00).", ex.getMessage());
    }

    @Test
    @DisplayName("Nao deve permitir produto com titulo vazio")
    void deveLancarExcecaoQuandoTituloVazio() {
        ProdutoDAO daoFake = new ProdutoDAO();
        ProdutoService service = new ProdutoService(daoFake);

        Produto p = new Produto(0, 1, "   ", new BigDecimal("50.00"), new BigDecimal("45.00"), 5, true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.salvar(p));
        assertEquals("O título do Gift Card é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Nao deve permitir excluir Gift Card que ja possua vendas registradas")
    void deveBloquearExclusaoDeProdutoComVendas() {
        // Stub do DAO para simular produto existente e com vendas sem precisar bater no banco
        ProdutoDAO daoStub = new ProdutoDAO() {
            @Override
            public Produto buscarPorId(int id) {
                return new Produto(id, 1, "PlayStation Store R$ 100", new BigDecimal("100.00"), new BigDecimal("95.00"), 2, true);
            }

            @Override
            public boolean possuiVendas(int produtoId) {
                return true; // Simula que ja tem vendas registradas em item_pedido
            }

            @Override
            public void excluir(int id) {
                fail("Nao deveria ter chamado o metodo excluir do DAO!");
            }
        };

        ProdutoService service = new ProdutoService(daoStub);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.excluir(1));
        assertTrue(ex.getMessage().contains("já possui vendas registradas"));
    }
}
