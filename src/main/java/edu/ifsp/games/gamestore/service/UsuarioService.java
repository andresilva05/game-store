package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.UsuarioDAO;
import edu.ifsp.games.gamestore.exception.DataAccessException;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.PapelUsuario;
import edu.ifsp.games.gamestore.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class UsuarioService {

    private static final int MIN_TAMANHO_NOME = 2;
    private static final int MAX_TAMANHO_NOME = 100;
    private static final int MAX_TAMANHO_EMAIL = 100;
    private static final int MIN_TAMANHO_SENHA = 6;
    private static final int MAX_TAMANHO_SENHA = 72;
    private static final int BCRYPT_COST = 10;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this(new UsuarioDAO());
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario cadastrar(String nome, String email, String senha, String confirmacaoSenha) {
        String nomeLimpo = validarNome(nome);
        String emailNormalizado = validarEmail(email);
        validarSenhaCadastro(senha, confirmacaoSenha);

        if (usuarioDAO.existePorEmail(emailNormalizado)) {
            throw new RegraNegocioException("Já existe uma conta cadastrada com este e-mail.");
        }

        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt(BCRYPT_COST));
        // Cadastro público sempre cria contas com perfil CLIENTE (previne escalação de privilégio)
        Usuario novoUsuario = new Usuario(nomeLimpo, emailNormalizado, senhaHash, PapelUsuario.CLIENTE);

        try {
            usuarioDAO.salvar(novoUsuario);
        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new RegraNegocioException("Já existe uma conta cadastrada com este e-mail.", e);
            }
            throw e;
        }

        return novoUsuario.semSenha();
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new RegraNegocioException("Informe o e-mail e a senha para entrar.");
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        Optional<Usuario> usuarioEncontrado = usuarioDAO.buscarPorEmail(emailNormalizado);

        if (usuarioEncontrado.isEmpty()) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }

        Usuario usuario = usuarioEncontrado.get();
        if (!verificarSenha(senha, usuario.getSenha())) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }

        return usuario.semSenha();
    }

    public Optional<Usuario> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return usuarioDAO.buscarPorId(id).map(Usuario::semSenha);
    }

    public boolean possuiPerfil(Usuario usuario, PapelUsuario papelEsperado) {
        return usuario != null && papelEsperado != null && papelEsperado == usuario.getPapel();
    }

    public void validarAcessoAdmin(Usuario usuario) {
        if (!possuiPerfil(usuario, PapelUsuario.ADMIN)) {
            throw new RegraNegocioException("Acesso restrito a administradores.");
        }
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome é obrigatório.");
        }
        String nomeLimpo = nome.trim();
        if (nomeLimpo.length() < MIN_TAMANHO_NOME) {
            throw new RegraNegocioException("O nome deve ter pelo menos " + MIN_TAMANHO_NOME + " caracteres.");
        }
        if (nomeLimpo.length() > MAX_TAMANHO_NOME) {
            throw new RegraNegocioException("O nome deve ter no máximo " + MAX_TAMANHO_NOME + " caracteres.");
        }
        return nomeLimpo;
    }

    private String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("O e-mail é obrigatório.");
        }
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        if (emailNormalizado.length() > MAX_TAMANHO_EMAIL || !EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new RegraNegocioException("Informe um endereço de e-mail válido.");
        }
        return emailNormalizado;
    }

    private void validarSenhaCadastro(String senha, String confirmacaoSenha) {
        if (senha == null || senha.isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória.");
        }
        if (senha.length() < MIN_TAMANHO_SENHA) {
            throw new RegraNegocioException("A senha deve ter pelo menos " + MIN_TAMANHO_SENHA + " caracteres.");
        }
        if (senha.length() > MAX_TAMANHO_SENHA) {
            throw new RegraNegocioException("A senha deve ter no máximo " + MAX_TAMANHO_SENHA + " caracteres.");
        }
        if (confirmacaoSenha == null || !senha.equals(confirmacaoSenha)) {
            throw new RegraNegocioException("As senhas informadas não coincidem.");
        }
    }

    private boolean verificarSenha(String senhaDigitada, String hashArmazenado) {
        if (hashArmazenado == null || hashArmazenado.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(senhaDigitada, hashArmazenado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
