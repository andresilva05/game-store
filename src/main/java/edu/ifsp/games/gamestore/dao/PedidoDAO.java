package edu.ifsp.games.gamestore.dao;

import edu.ifsp.games.gamestore.model.ItemPedido;
import edu.ifsp.games.gamestore.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {


    public void inserirPedido(Pedido pedido, Connection conn) throws SQLException {

        // RETURN_GENERATED_KEYS para recuperar o ID gerado pelo banco e usar como FK nos itens
        PreparedStatement ps = conn.prepareStatement("INSERT INTO pedido (id_usuario, valor_compra) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, pedido.getIdUsuario());
        ps.setBigDecimal(2, pedido.getValorCompra());

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                pedido.setId(rs.getInt(1));
            }
        }
    }

    public void inserirItem(ItemPedido item, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO item_pedido (pedido_id, preco, produto_id, pin) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, item.getPedidoId());
        ps.setBigDecimal(2, item.getPreco());
        ps.setInt(3, item.getProdutoId());
        ps.setString(4, item.getPin());

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                item.setId(rs.getInt(1));
            }
        }
    }

    // NOTA DE SEGURANÇA (Davi - Módulo 2):
    // Esse idUsuario tem que vir da sessão (HttpSession) do usuário logado e NUNCA da URL por GET (?id=..),
    // senão um cliente pode mudar o número na URL e visualizar o histórico e os PINs de outro usuário.
    public List<Pedido> listarPorUsuario(int idUsuario) {

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, id_usuario, date_compra, valor_compra FROM pedido WHERE id_usuario = ? ORDER BY date_compra DESC");

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("id"));
                    pedido.setIdUsuario(rs.getInt("id_usuario"));
                    pedido.setDataCompra(rs.getTimestamp("date_compra"));
                    pedido.setValorCompra(rs.getBigDecimal("valor_compra"));

                    pedidos.add(pedido);
                }
            }

            for (Pedido pedido : pedidos) {
                try (PreparedStatement psItens = conn.prepareStatement("SELECT id, pedido_id, preco, produto_id, pin FROM item_pedido WHERE pedido_id = ?")) {
                    psItens.setInt(1, pedido.getId());

                    try (ResultSet rsItens = psItens.executeQuery()) {
                        while (rsItens.next()) {
                            ItemPedido item = new ItemPedido();
                            item.setId(rsItens.getInt("id"));
                            item.setPedidoId(rsItens.getInt("pedido_id"));
                            item.setPreco(rsItens.getBigDecimal("preco"));
                            item.setProdutoId(rsItens.getInt("produto_id"));
                            item.setPin(rsItens.getString("pin"));

                            pedido.getItens().add(item);
                        }
                    }
                }
            }
            return pedidos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
