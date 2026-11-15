package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.ConnectionFactory;
import edu.ifsp.games.gamestore.dao.PedidoDAO;
import edu.ifsp.games.gamestore.model.ItemPedido;
import edu.ifsp.games.gamestore.model.Pedido;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class PedidoService {
    private PedidoDAO pedidoDAO = new PedidoDAO();

    public void finalizarCompra(Pedido pedido) {

        if (pedido == null || pedido.getItens().isEmpty()) {
            throw new RegraNegocioException("O carrinho está vazio");
        } else if (pedido.getValorCompra() == null || pedido.getValorCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor inválido.");
        }

        for (ItemPedido item : pedido.getItens()) {
            String pin = UUID.randomUUID().toString().substring(0, 19).toUpperCase();
            item.setPin(pin);
        }

        try (Connection conn = ConnectionFactory.getConnection()) {
            // INÍCIO DA TRANSAÇÃO (Regra do Tudo ou Nada):
            // Desativo o autocommit para gravar o pedido e todos os itens na mesma transação atômica.
            conn.setAutoCommit(false);

            try {
                pedidoDAO.inserirPedido(pedido, conn);

                for (ItemPedido item : pedido.getItens()) {
                    item.setPedidoId(pedido.getId());
                    pedidoDAO.inserirItem(item, conn);

                }
                // Só comito se o pedido e todos os itens foram inseridos com sucesso
                conn.commit();

            } catch (SQLException e) {
                // Se der erro em qualquer item, desfaz tudo para não deixar venda pela metade
                conn.rollback();

                throw new RegraNegocioException("Erro ao processar o pedido. A compra foi cancelada por segurança.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
