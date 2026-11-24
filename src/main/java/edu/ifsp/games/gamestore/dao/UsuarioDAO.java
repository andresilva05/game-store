package edu.ifsp.games.gamestore.dao;

import edu.ifsp.games.gamestore.config.ConnectionFactory;
import edu.ifsp.games.gamestore.exception.DataAccessException;
import edu.ifsp.games.gamestore.model.PapelUsuario;
import edu.ifsp.games.gamestore.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UsuarioDAO {

    private static final String SQL_INSERT =
            "INSERT INTO usuario (nome, email, senha, papel) VALUES (?, ?, ?, ?)";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT id, nome, email, senha, papel FROM usuario WHERE email = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, nome, email, senha, papel FROM usuario WHERE id = ?";

    private static final String SQL_EXISTS_BY_EMAIL =
            "SELECT 1 FROM usuario WHERE email = ? LIMIT 1";

    public void salvar(Usuario usuario) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPapel().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao cadastrar usuário no banco de dados.", e);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar usuário por e-mail.", e);
        }
    }

    public Optional<Usuario> buscarPorId(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar usuário por ID.", e);
        }
    }

    public boolean existePorEmail(String email) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao verificar existência de e-mail.", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPapel(PapelUsuario.valueOf(rs.getString("papel")));
        return usuario;
    }
}
