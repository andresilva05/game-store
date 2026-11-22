package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.exception.DataAccessException;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Usuario;
import edu.ifsp.games.gamestore.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);
    private static final String VIEW_LOGIN = "/WEB-INF/views/login.jsp";
    private static final String DESTINO_PADRAO = "/meus-pedidos";

    private final UsuarioService usuarioService;

    public LoginServlet() {
        this(new UsuarioService());
    }

    public LoginServlet(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionAttributes.USUARIO_LOGADO) != null) {
            response.sendRedirect(request.getContextPath() + DESTINO_PADRAO);
            return;
        }

        consumirMensagensFlash(request, session);
        request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(email, senha);

            HttpSession session = request.getSession(true);
            // Rotaciona o ID da sessão (previne Session Fixation mantendo eventual carrinho na sessão)
            request.changeSessionId();
            session.setAttribute(SessionAttributes.USUARIO_LOGADO, usuarioAutenticado);

            String destino = resolverDestinoPosLogin(session);

            // Padrão PRG (Post-Redirect-Get)
            response.sendRedirect(request.getContextPath() + destino);
        } catch (RegraNegocioException e) {
            request.setAttribute("erro", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
        } catch (DataAccessException e) {
            LOGGER.error("Erro ao consultar credenciais do usuário durante o login", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("erro", "Serviço temporariamente indisponível. Tente novamente em instantes.");
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
        }
    }

    private void consumirMensagensFlash(HttpServletRequest request, HttpSession session) {
        if (session != null) {
            Object sucesso = session.getAttribute(SessionAttributes.MENSAGEM_SUCESSO);
            if (sucesso != null) {
                request.setAttribute("sucesso", sucesso);
                session.removeAttribute(SessionAttributes.MENSAGEM_SUCESSO);
            }

            Object erro = session.getAttribute(SessionAttributes.MENSAGEM_ERRO);
            if (erro != null) {
                request.setAttribute("erro", erro);
                session.removeAttribute(SessionAttributes.MENSAGEM_ERRO);
            }
        }

        if ("true".equals(request.getParameter("logout")) && request.getAttribute("sucesso") == null) {
            request.setAttribute("sucesso", "Você saiu da sua conta com segurança.");
        }
    }

    private String resolverDestinoPosLogin(HttpSession session) {
        Object destinoAttr = session.getAttribute(SessionAttributes.URL_DESTINO);
        session.removeAttribute(SessionAttributes.URL_DESTINO);

        if (destinoAttr instanceof String urlDestino && isDestinoInternoSeguro(urlDestino)) {
            return urlDestino;
        }
        return DESTINO_PADRAO;
    }

    private boolean isDestinoInternoSeguro(String url) {
        return url != null
                && url.startsWith("/")
                && !url.startsWith("//")
                && !url.contains("\r")
                && !url.contains("\n");
    }
}
