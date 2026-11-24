package edu.ifsp.games.gamestore.dao;

import edu.ifsp.games.gamestore.model.Plataforma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlataformaDAO {

    public void inserir(Plataforma plataforma) {
        String sql = "INSERT INTO plataforma (nome) VALUES (?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, plataforma.getNome());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    plataforma.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar plataforma: " + e.getMessage(), e);
        }
    }

    public List<Plataforma> listarTodas() {
        String sql = "SELECT id, nome FROM plataforma ORDER BY nome ASC";
        List<Plataforma> plataformas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plataforma p = new Plataforma();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                plataformas.add(p);
            }
            return plataformas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar plataformas: " + e.getMessage(), e);
        }
    }

    public Plataforma buscarPorId(int id) {
        String sql = "SELECT id, nome FROM plataforma WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Plataforma p = new Plataforma();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    return p;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar plataforma por ID: " + e.getMessage(), e);
        }
    }

    public void atualizar(Plataforma plataforma) {
        String sql = "UPDATE plataforma SET nome = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plataforma.getNome());
            ps.setInt(2, plataforma.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar plataforma: " + e.getMessage(), e);
        }
    }

    public boolean possuiProdutos(int plataformaId) {
        String sql = "SELECT COUNT(*) FROM produto WHERE plataforma_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plataformaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar produtos da plataforma: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM plataforma WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir plataforma: " + e.getMessage(), e);
        }
    }
}