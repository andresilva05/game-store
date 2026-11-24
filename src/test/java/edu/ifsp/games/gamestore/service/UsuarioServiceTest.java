package edu.ifsp.games.gamestore.service;

import edu.ifsp.games.gamestore.dao.UsuarioDAO;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.PapelUsuario;
import edu.ifsp.games.gamestore.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private InMemoryUsuarioDAO usuarioDAO;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioDAO = new InMemoryUsuarioDAO();
        usuarioService = new UsuarioService(usuarioDAO);
    }

    @Test
    @DisplayName("Deve cadastrar cliente com e-mail normalizado, papel CLIENTE e senha criptografada com BCrypt")
    void deveCadastrarClienteComSucesso() {
        Usuario cadastrado = usuarioService.cadastrar(
                "  Davi Ruan  ",
                "  Davi.Ruan@Email.COM ",
                "senhaSegura123",
                "senhaSegura123"
        );

        assertNotNull(cadastrado.getId());
        assertEquals("Davi Ruan", cadastrado.getNome());
        assertEquals("davi.ruan@email.com", cadastrado.getEmail());
        assertEquals(PapelUsuario.CLIENTE, cadastrado.getPapel());
        assertNull(cadastrado.getSenha(), "A senha não deve ser devolvida no objeto sanitizado");

        Usuario persistido = usuarioDAO.buscarPorEmail("davi.ruan@email.com").orElseThrow();
        assertNotEquals("senhaSegura123", persistido.getSenha());
        assertTrue(BCrypt.checkpw("senhaSegura123", persistido.getSenha()));
    }

    @Test
    @DisplayName("Não deve permitir cadastro com e-mail duplicado mesmo com variações de caixa ou espaços")
    void naoDevePermitirEmailDuplicado() {
        usuarioService.cadastrar("Davi", "davi@gamestore.com", "123456", "123456");

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.cadastrar("Outro Usuario", "  DAVI@GameStore.COM ", "abcdef", "abcdef")
        );

        assertEquals("Já existe uma conta cadastrada com este e-mail.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome vazio, e-mail inválido, senha curta ou confirmação divergente")
    void deveValidarCamposObrigatoriosNoCadastro() {
        assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar(" ", "a@b.com", "123456", "123456"));

        assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar("D", "a@b.com", "123456", "123456"));

        assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar("Davi", "email-invalido", "123456", "123456"));

        assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar("Davi", "davi@gamestore.com", "12345", "12345"));

        RegraNegocioException divergente = assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar("Davi", "davi@gamestore.com", "123456", "654321"));
        assertEquals("As senhas informadas não coincidem.", divergente.getMessage());
    }

    @Test
    @DisplayName("Deve autenticar usuário válido e retornar objeto sem senha")
    void deveAutenticarUsuarioValido() {
        usuarioService.cadastrar("Maria", "maria@gamestore.com", "minhaSenha99", "minhaSenha99");

        Usuario autenticado = usuarioService.autenticar("  MARIA@GameStore.com ", "minhaSenha99");

        assertNotNull(autenticado);
        assertNotNull(autenticado.getId());
        assertEquals("Maria", autenticado.getNome());
        assertEquals("maria@gamestore.com", autenticado.getEmail());
        assertEquals(PapelUsuario.CLIENTE, autenticado.getPapel());
        assertNull(autenticado.getSenha());
    }

    @Test
    @DisplayName("Deve lançar a mesma mensagem genérica para e-mail inexistente ou senha incorreta")
    void deveRetornarMensagemGenericaParaCredenciaisInvalidas() {
        usuarioService.cadastrar("Maria", "maria@gamestore.com", "minhaSenha99", "minhaSenha99");

        RegraNegocioException erroEmail = assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.autenticar("inexistente@gamestore.com", "minhaSenha99")
        );

        RegraNegocioException erroSenha = assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.autenticar("maria@gamestore.com", "senhaErrada")
        );

        assertEquals("E-mail ou senha inválidos.", erroEmail.getMessage());
        assertEquals(erroEmail.getMessage(), erroSenha.getMessage());
    }

    @Test
    @DisplayName("Deve exigir e-mail e senha preenchidos no login")
    void deveRejeitarLoginComCamposVazios() {
        assertThrows(RegraNegocioException.class, () -> usuarioService.autenticar("", "123456"));
        assertThrows(RegraNegocioException.class, () -> usuarioService.autenticar("a@b.com", ""));
        assertThrows(RegraNegocioException.class, () -> usuarioService.autenticar(null, null));
    }

    @Test
    @DisplayName("Deve validar corretamente perfis CLIENTE e ADMIN")
    void deveValidarPerfisDeAcesso() {
        Usuario cliente = new Usuario(1, "Cliente", "c@g.com", null, PapelUsuario.CLIENTE);
        Usuario admin = new Usuario(2, "Admin", "a@g.com", null, PapelUsuario.ADMIN);

        assertTrue(usuarioService.possuiPerfil(cliente, PapelUsuario.CLIENTE));
        assertFalse(usuarioService.possuiPerfil(cliente, PapelUsuario.ADMIN));
        assertTrue(usuarioService.possuiPerfil(admin, PapelUsuario.ADMIN));
        assertFalse(usuarioService.possuiPerfil(null, PapelUsuario.ADMIN));

        assertDoesNotThrow(() -> usuarioService.validarAcessoAdmin(admin));
        assertThrows(RegraNegocioException.class, () -> usuarioService.validarAcessoAdmin(cliente));
        assertThrows(RegraNegocioException.class, () -> usuarioService.validarAcessoAdmin(null));
    }

    @Test
    @DisplayName("Hashes BCrypt do seed em database.sql devem corresponder às credenciais de demonstração")
    void hashesDoSeedDevemSerValidos() {
        assertTrue(BCrypt.checkpw("admin123", "$2a$10$qh04Zqs.q.x6KaWX1EDOaez1cgFri98577KV3AIEJDo.7nCCkdIEy"));
        assertTrue(BCrypt.checkpw("cliente123", "$2a$10$e7gkPr/nSse9i/p6mVRgfuBaxeYemeDnwUOfeqZX8H4mehXXgeuJ."));
    }

    private static class InMemoryUsuarioDAO extends UsuarioDAO {
        private final Map<String, Usuario> porEmail = new HashMap<>();
        private final Map<Integer, Usuario> porId = new HashMap<>();
        private final AtomicInteger sequencia = new AtomicInteger(1);

        @Override
        public void salvar(Usuario usuario) {
            int id = sequencia.getAndIncrement();
            usuario.setId(id);
            Usuario copia = new Usuario(id, usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getPapel());
            porEmail.put(copia.getEmail(), copia);
            porId.put(id, copia);
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.ofNullable(porEmail.get(email));
        }

        @Override
        public Optional<Usuario> buscarPorId(int id) {
            return Optional.ofNullable(porId.get(id));
        }

        @Override
        public boolean existePorEmail(String email) {
            return porEmail.containsKey(email);
        }
    }
}
