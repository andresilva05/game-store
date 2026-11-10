package edu.ifsp.games.gamestore.dao;

import edu.ifsp.games.gamestore.model.ItemPedido;
import edu.ifsp.games.gamestore.model.Pedido;

import java.sql.*;

public class PedidoDAO {


    public void inserirPedido(Pedido pedido, Connection conn) throws SQLException {

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
}
