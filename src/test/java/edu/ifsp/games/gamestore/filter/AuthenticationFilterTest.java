package edu.ifsp.games.gamestore.filter;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.model.PapelUsuario;
import edu.ifsp.games.gamestore.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationFilterTest {

    private final AuthenticationFilter filter = new AuthenticationFilter();

    @Test
    @DisplayName("Deve redirecionar usuário não autenticado para /login e salvar urlDestino com query string")
    void deveRedirecionarUsuarioAnonimoParaLogin() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        HttpSession session = criarSessaoFake(sessionAttrs);
        AtomicReference<String> redirectUrl = new AtomicReference<>();
        Map<String, String> headers = new HashMap<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        HttpServletRequest req = criarRequestFake("GET", "/game-store", "/game-store/meus-pedidos", "pagina=2", session, false, null);
        HttpServletResponse resp = criarResponseFake(redirectUrl, new AtomicInteger(200), headers);
        FilterChain chain = (r, s) -> chainCalled.set(true);

        filter.doFilter(req, resp, chain);

        assertFalse(chainCalled.get());
        assertEquals("/game-store/login", redirectUrl.get());
        assertEquals("/meus-pedidos?pagina=2", sessionAttrs.get(SessionAttributes.URL_DESTINO));
        assertNotNull(sessionAttrs.get(SessionAttributes.MENSAGEM_ERRO));
        assertEquals("no-cache, no-store, must-revalidate", headers.get("Cache-Control"));
    }

    @Test
    @DisplayName("Deve permitir acesso de CLIENTE autenticado a /meus-pedidos")
    void devePermitirClienteEmRotaAutenticada() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put(SessionAttributes.USUARIO_LOGADO, new Usuario(1, "Cliente", "c@g.com", null, PapelUsuario.CLIENTE));
        HttpSession session = criarSessaoFake(sessionAttrs);

        AtomicBoolean chainCalled = new AtomicBoolean(false);
        HttpServletRequest req = criarRequestFake("GET", "/game-store", "/game-store/meus-pedidos", null, session, true, null);
        HttpServletResponse resp = criarResponseFake(new AtomicReference<>(), new AtomicInteger(200), new HashMap<>());
        FilterChain chain = (r, s) -> chainCalled.set(true);

        filter.doFilter(req, resp, chain);

        assertTrue(chainCalled.get());
    }

    @Test
    @DisplayName("Deve retornar 403 e encaminhar para acesso-negado.jsp quando CLIENTE tenta acessar /admin/*")
    void deveBloquearClienteEmRotaAdminCom403() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put(SessionAttributes.USUARIO_LOGADO, new Usuario(1, "Cliente", "c@g.com", null, PapelUsuario.CLIENTE));
        HttpSession session = criarSessaoFake(sessionAttrs);

        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicInteger status = new AtomicInteger(200);
        AtomicReference<String> forwardedPath = new AtomicReference<>();

        HttpServletRequest req = criarRequestFake("GET", "/game-store", "/game-store/admin/produtos", null, session, true, forwardedPath);
        HttpServletResponse resp = criarResponseFake(new AtomicReference<>(), status, new HashMap<>());
        FilterChain chain = (r, s) -> chainCalled.set(true);

        filter.doFilter(req, resp, chain);

        assertFalse(chainCalled.get());
        assertEquals(HttpServletResponse.SC_FORBIDDEN, status.get());
        assertEquals("/WEB-INF/views/acesso-negado.jsp", forwardedPath.get());
    }

    @Test
    @DisplayName("Deve permitir acesso de ADMIN autenticado em /admin/*")
    void devePermitirAdminEmRotaAdmin() throws Exception {
        Map<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put(SessionAttributes.USUARIO_LOGADO, new Usuario(2, "Admin", "a@g.com", null, PapelUsuario.ADMIN));
        HttpSession session = criarSessaoFake(sessionAttrs);

        AtomicBoolean chainCalled = new AtomicBoolean(false);
        HttpServletRequest req = criarRequestFake("GET", "/game-store", "/game-store/admin/produtos", null, session, true, null);
        HttpServletResponse resp = criarResponseFake(new AtomicReference<>(), new AtomicInteger(200), new HashMap<>());
        FilterChain chain = (r, s) -> chainCalled.set(true);

        filter.doFilter(req, resp, chain);

        assertTrue(chainCalled.get());
    }

    private HttpSession criarSessaoFake(Map<String, Object> attrs) {
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getId" -> "sessao-teste-123";
                    case "getAttribute" -> attrs.get(args[0]);
                    case "setAttribute" -> {
                        attrs.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "removeAttribute" -> {
                        attrs.remove(args[0]);
                        yield null;
                    }
                    default -> null;
                }
        );
    }

    private HttpServletRequest criarRequestFake(
            String httpMethod,
            String contextPath,
            String requestUri,
            String queryString,
            HttpSession session,
            boolean sessionExistsInitially,
            AtomicReference<String> forwardedPath
    ) {
        AtomicBoolean exists = new AtomicBoolean(sessionExistsInitially);
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getMethod" -> httpMethod;
                    case "getContextPath" -> contextPath;
                    case "getRequestURI" -> requestUri;
                    case "getQueryString" -> queryString;
                    case "getSession" -> {
                        boolean create = args == null || args.length == 0 || (Boolean) args[0];
                        if (exists.get()) {
                            yield session;
                        }
                        if (create) {
                            exists.set(true);
                            yield session;
                        }
                        yield null;
                    }
                    case "getRequestDispatcher" -> criarDispatcherFake((String) args[0], forwardedPath);
                    default -> null;
                }
        );
    }

    private RequestDispatcher criarDispatcherFake(String path, AtomicReference<String> forwardedPath) {
        return (RequestDispatcher) Proxy.newProxyInstance(
                RequestDispatcher.class.getClassLoader(),
                new Class<?>[]{RequestDispatcher.class},
                (proxy, method, args) -> {
                    if ("forward".equals(method.getName()) && forwardedPath != null) {
                        forwardedPath.set(path);
                    }
                    return null;
                }
        );
    }

    private HttpServletResponse criarResponseFake(
            AtomicReference<String> redirectUrl,
            AtomicInteger status,
            Map<String, String> headers
    ) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "sendRedirect" -> {
                        redirectUrl.set((String) args[0]);
                        yield null;
                    }
                    case "setStatus" -> {
                        status.set((Integer) args[0]);
                        yield null;
                    }
                    case "setHeader" -> {
                        headers.put((String) args[0], (String) args[1]);
                        yield null;
                    }
                    default -> null;
                }
        );
    }
}
