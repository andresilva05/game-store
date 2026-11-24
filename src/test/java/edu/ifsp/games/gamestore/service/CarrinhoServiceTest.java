package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.ProdutoDAO;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Carrinho;
import edu.ifsp.games.gamestore.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CarrinhoServiceTest {

    private ProdutoDAO criarProdutoDaoStub(Produto... produtos) {
        Map<Integer, Produto> mapa = new HashMap<>();
        for (Produto p : produtos) {
            mapa.put(p.getId(), p);
        }
        return new ProdutoDAO() {
            @Override
            public Produto buscarPorId(int id) {
                return mapa.get(id);
            }
        };
    }

    @Test
    @DisplayName("Deve adicionar itens ao carrinho e calcular quantidade total e valor total")
    void deveAdicionarItensECalcularTotal() {
        Produto steam = new Produto(1, 1, "Steam Wallet R$ 50", new BigDecimal("50.00"), new BigDecimal("48.00"), 10, true);
        Produto psn = new Produto(2, 2, "PlayStation Store R$ 60", new BigDecimal("60.00"), new BigDecimal("58.50"), 5, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(steam, psn));
        Carrinho carrinho = new Carrinho();

        service.adicionarItem(carrinho, 1, 2);
        service.adicionarItem(carrinho, 2, 1);

        assertEquals(2, carrinho.getItens().size());
        assertEquals(3, carrinho.getQuantidadeTotal());
        assertEquals(new BigDecimal("154.50"), carrinho.getValorTotal());
        assertFalse(carrinho.isVazio());
    }

    @Test
    @DisplayName("Deve somar quantidade ao adicionar o mesmo Gift Card novamente")
    void deveSomarQuantidadeParaProdutoJaNoCarrinho() {
        Produto steam = new Produto(1, 1, "Steam Wallet R$ 50", new BigDecimal("50.00"), new BigDecimal("48.00"), 10, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(steam));
        Carrinho carrinho = new Carrinho();

        service.adicionarItem(carrinho, 1, 2);
        service.adicionarItem(carrinho, 1, 3);

        assertEquals(1, carrinho.getItens().size());
        assertEquals(5, carrinho.getQuantidadeTotal());
        assertEquals(new BigDecimal("240.00"), carrinho.getValorTotal());
    }

    @Test
    @DisplayName("Nao deve permitir adicionar quantidade superior ao estoque disponivel")
    void naoDevePermitirAdicionarAcimaDoEstoque() {
        Produto xbox = new Produto(4, 3, "Xbox Game Pass", new BigDecimal("49.99"), new BigDecimal("44.90"), 3, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(xbox));
        Carrinho carrinho = new Carrinho();

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.adicionarItem(carrinho, 4, 4)
        );

        assertTrue(ex.getMessage().contains("excede o estoque disponível"));
        assertTrue(carrinho.isVazio());
    }

    @Test
    @DisplayName("Nao deve permitir que a soma no carrinho ultrapasse o estoque disponivel")
    void naoDevePermitirSomaAcimaDoEstoque() {
        Produto xbox = new Produto(4, 3, "Xbox Game Pass", new BigDecimal("49.99"), new BigDecimal("44.90"), 3, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(xbox));
        Carrinho carrinho = new Carrinho();

        service.adicionarItem(carrinho, 4, 2);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.adicionarItem(carrinho, 4, 2)
        );

        assertTrue(ex.getMessage().contains("excede o estoque disponível"));
        assertEquals(2, carrinho.getQuantidadeTotal());
    }

    @Test
    @DisplayName("Nao deve permitir adicionar Gift Card inativo ou esgotado")
    void naoDevePermitirAdicionarProdutoInativoOuEsgotado() {
        Produto inativo = new Produto(1, 1, "Card Inativo", new BigDecimal("50.00"), new BigDecimal("45.00"), 5, false);
        Produto esgotado = new Produto(2, 1, "Card Esgotado", new BigDecimal("50.00"), new BigDecimal("45.00"), 0, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(inativo, esgotado));
        Carrinho carrinho = new Carrinho();

        assertThrows(RegraNegocioException.class, () -> service.adicionarItem(carrinho, 1, 1));
        assertThrows(RegraNegocioException.class, () -> service.adicionarItem(carrinho, 2, 1));
        assertThrows(RegraNegocioException.class, () -> service.adicionarItem(carrinho, 1, 0));
    }

    @Test
    @DisplayName("Deve alterar quantidade de item existente e impedir valor acima do estoque")
    void deveAlterarQuantidadeEValidarEstoque() {
        Produto steam = new Produto(1, 1, "Steam Wallet R$ 50", new BigDecimal("50.00"), new BigDecimal("48.00"), 5, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(steam));
        Carrinho carrinho = new Carrinho();

        service.adicionarItem(carrinho, 1, 1);
        service.alterarQuantidade(carrinho, 1, 4);

        assertEquals(4, carrinho.getQuantidadeTotal());
        assertEquals(new BigDecimal("192.00"), carrinho.getValorTotal());

        assertThrows(RegraNegocioException.class, () -> service.alterarQuantidade(carrinho, 1, 6));
        assertEquals(4, carrinho.getQuantidadeTotal());
    }

    @Test
    @DisplayName("Deve remover item ao atualizar quantidade para zero e ao chamar removerItem")
    void deveRemoverItemELimparCarrinho() {
        Produto steam = new Produto(1, 1, "Steam Wallet R$ 50", new BigDecimal("50.00"), new BigDecimal("48.00"), 10, true);
        Produto psn = new Produto(2, 2, "PlayStation Store R$ 60", new BigDecimal("60.00"), new BigDecimal("58.50"), 10, true);

        CarrinhoService service = new CarrinhoService(criarProdutoDaoStub(steam, psn));
        Carrinho carrinho = new Carrinho();

        service.adicionarItem(carrinho, 1, 1);
        service.adicionarItem(carrinho, 2, 2);

        service.alterarQuantidade(carrinho, 1, 0);
        assertEquals(1, carrinho.getItens().size());
        assertNull(carrinho.buscarItemPorProdutoId(1));

        service.removerItem(carrinho, 2);
        assertTrue(carrinho.isVazio());
        assertEquals(BigDecimal.ZERO, carrinho.getValorTotal());
    }
}
