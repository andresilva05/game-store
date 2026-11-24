package edu.ifsp.games.gamestore.filter;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.model.Usuario;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = {"/checkout", "/meus-pedidos", "/admin", "/admin/*"}
)
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String VIEW_ACESSO_NEGADO = "/WEB-INF/views/acesso-negado.jsp";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Evita cache de páginas privadas pelo navegador após logout (proteção do botão Voltar)
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        HttpSession session = httpRequest.getSession(false);
        Usuario usuarioLogado = (session != null)
                ? (Usuario) session.getAttribute(SessionAttributes.USUARIO_LOGADO)
                : null;

        String caminhoRelativo = extrairCaminhoRelativo(httpRequest);

        if (usuarioLogado == null) {
            LOGGER.info("Acesso não autenticado bloqueado em '{}'. Redirecionando para /login.", caminhoRelativo);

            HttpSession novaSessao = httpRequest.getSession(true);
            if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
                String destino = caminhoRelativo;
                String queryString = httpRequest.getQueryString();
                if (queryString != null && !queryString.isBlank()) {
                    destino += "?" + queryString;
                }
                novaSessao.setAttribute(SessionAttributes.URL_DESTINO, destino);
                novaSessao.setAttribute(SessionAttributes.MENSAGEM_ERRO, "Faça login para acessar esta página.");
            } else {
                novaSessao.setAttribute(SessionAttributes.MENSAGEM_ERRO, "Faça login para continuar esta operação.");
            }

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (isRotaAdmin(caminhoRelativo) && !usuarioLogado.isAdmin()) {
            LOGGER.warn(
                    "Acesso negado (403): usuário '{}' (papel={}) tentou acessar '{}'.",
                    usuarioLogado.getEmail(),
                    usuarioLogado.getPapel(),
                    caminhoRelativo
            );
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpRequest.getRequestDispatcher(VIEW_ACESSO_NEGADO).forward(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    private String extrairCaminhoRelativo(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private boolean isRotaAdmin(String caminhoRelativo) {
        return "/admin".equals(caminhoRelativo) || caminhoRelativo.startsWith("/admin/");
    }
}
