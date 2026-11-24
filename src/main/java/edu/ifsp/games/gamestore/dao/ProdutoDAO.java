package edu.ifsp.games.gamestore.dao;

import edu.ifsp.games.gamestore.config.ConnectionFactory;
import edu.ifsp.games.gamestore.exception.DataAccessException;
import edu.ifsp.games.gamestore.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (plataforma_id, titulo, valor_credito, preco_venda, estoque, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, produto.getPlataformaId());
            ps.setString(2, produto.getTitulo());
            ps.setBigDecimal(3, produto.getValorCredito());
            ps.setBigDecimal(4, produto.getPrecoVenda());
            ps.setInt(5, produto.getEstoque());
            ps.setBoolean(6, produto.isAtivo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarTodos() {
        String sql = "SELECT p.id, p.plataforma_id, p.titulo, p.valor_credito, p.preco_venda, p.estoque, p.ativo, " +
                     "       plat.nome AS plataforma_nome " +
                     "FROM produto p " +
                     "INNER JOIN plataforma plat ON p.plataforma_id = plat.id " +
                     "ORDER BY p.id DESC";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                produtos.add(extrairProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarAtivos(Integer plataformaId, String busca) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.plataforma_id, p.titulo, p.valor_credito, p.preco_venda, p.estoque, p.ativo, " +
                "       plat.nome AS plataforma_nome " +
                "FROM produto p " +
                "INNER JOIN plataforma plat ON p.plataforma_id = plat.id " +
                "WHERE p.ativo = TRUE "
        );

        if (plataformaId != null && plataformaId > 0) {
            sql.append("AND p.plataforma_id = ? ");
        }
        if (busca != null && !busca.trim().isEmpty()) {
            sql.append("AND LOWER(p.titulo) LIKE ? ");
        }
        sql.append("ORDER BY p.id DESC");

        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int indice = 1;
            if (plataformaId != null && plataformaId > 0) {
                ps.setInt(indice++, plataformaId);
            }
            if (busca != null && !busca.trim().isEmpty()) {
                ps.setString(indice++, "%" + busca.trim().toLowerCase() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    produtos.add(extrairProduto(rs));
                }
            }
            return produtos;
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao listar produtos ativos da vitrine: " + e.getMessage(), e);
        }
    }

    public Produto buscarPorId(int id) {
        String sql = "SELECT p.id, p.plataforma_id, p.titulo, p.valor_credito, p.preco_venda, p.estoque, p.ativo, " +
                     "       plat.nome AS plataforma_nome " +
                     "FROM produto p " +
                     "INNER JOIN plataforma plat ON p.plataforma_id = plat.id " +
                     "WHERE p.id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extrairProduto(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID: " + e.getMessage(), e);
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET plataforma_id = ?, titulo = ?, valor_credito = ?, preco_venda = ?, estoque = ?, ativo = ? " +
                     "WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, produto.getPlataformaId());
            ps.setString(2, produto.getTitulo());
            ps.setBigDecimal(3, produto.getValorCredito());
            ps.setBigDecimal(4, produto.getPrecoVenda());
            ps.setInt(5, produto.getEstoque());
            ps.setBoolean(6, produto.isAtivo());
            ps.setInt(7, produto.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void alternarStatus(int id, boolean ativo) {
        String sql = "UPDATE produto SET ativo = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, ativo);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar status do produto: " + e.getMessage(), e);
        }
    }

    public boolean possuiVendas(int produtoId) {
        String sql = "SELECT COUNT(*) FROM item_pedido WHERE produto_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar vendas do produto: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    private Produto extrairProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setPlataformaId(rs.getInt("plataforma_id"));
        p.setPlataformaNome(rs.getString("plataforma_nome"));
        p.setTitulo(rs.getString("titulo"));
        p.setValorCredito(rs.getBigDecimal("valor_credito"));
        p.setPrecoVenda(rs.getBigDecimal("preco_venda"));
        p.setEstoque(rs.getInt("estoque"));
        p.setAtivo(rs.getBoolean("ativo"));
        return p;
    }
}