package edu.ifsp.games.gamestore.listener;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.model.PapelUsuario;
import edu.ifsp.games.gamestore.model.Usuario;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionListenerTest {

    private SessionListener listener;

    @BeforeEach
    void setUp() {
        SessionListener.resetarContadorParaTestes();
        listener = new SessionListener();
    }

    @Test
    @DisplayName("Deve incrementar e decrementar sessões ativas corretamente no ServletContext")
    void deveContabilizarSessoesAtivas() {
        Map<String, Object> contextAttrs = new HashMap<>();
        ServletContext context = criarContextFake(contextAttrs);
        HttpSession s1 = criarSessaoFake("s1", context, new HashMap<>());
        HttpSession s2 = criarSessaoFake("s2", context, new HashMap<>());

        listener.sessionCreated(new HttpSessionEvent(s1));
        assertEquals(1, SessionListener.getSessoesAtivas());
        assertEquals(1, contextAttrs.get(SessionAttributes.SESSOES_ATIVAS));

        listener.sessionCreated(new HttpSessionEvent(s2));
        assertEquals(2, SessionListener.getSessoesAtivas());
        assertEquals(2, contextAttrs.get(SessionAttributes.SESSOES_ATIVAS));

        listener.sessionDestroyed(new HttpSessionEvent(s1));
        assertEquals(1, SessionListener.getSessoesAtivas());
        assertEquals(1, contextAttrs.get(SessionAttributes.SESSOES_ATIVAS));

        listener.sessionDestroyed(new HttpSessionEvent(s2));
        listener.sessionDestroyed(new HttpSessionEvent(s2));
        assertEquals(0, SessionListener.getSessoesAtivas());
        assertEquals(0, contextAttrs.get(SessionAttributes.SESSOES_ATIVAS));
    }

    @Test
    @DisplayName("Deve auditar login e logout via eventos de atributo da sessão sem lançar exceção")
    void deveAuditarEventosDeAtributoSemErro() {
        ServletContext context = criarContextFake(new HashMap<>());
        Map<String, Object> sessionAttrs = new HashMap<>();
        Usuario usuario = new Usuario(10, "Davi", "davi@gamestore.com", null, PapelUsuario.CLIENTE);
        sessionAttrs.put(SessionAttributes.USUARIO_LOGADO, usuario);
        HttpSession session = criarSessaoFake("s-auth", context, sessionAttrs);

        assertDoesNotThrow(() -> listener.attributeAdded(
                new HttpSessionBindingEvent(session, SessionAttributes.USUARIO_LOGADO, usuario)
        ));
        assertDoesNotThrow(() -> listener.attributeRemoved(
                new HttpSessionBindingEvent(session, SessionAttributes.USUARIO_LOGADO, usuario)
        ));
        assertDoesNotThrow(() -> listener.sessionDestroyed(new HttpSessionEvent(session)));
    }

    private ServletContext criarContextFake(Map<String, Object> attrs) {
        return (ServletContext) Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class<?>[]{ServletContext.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "setAttribute" -> {
                        attrs.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "getAttribute" -> attrs.get(args[0]);
                    default -> null;
                }
        );
    }

    private HttpSession criarSessaoFake(String id, ServletContext context, Map<String, Object> attrs) {
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getId" -> id;
                    case "getServletContext" -> context;
                    case "getAttribute" -> attrs.get(args[0]);
                    default -> null;
                }
        );
    }
}
