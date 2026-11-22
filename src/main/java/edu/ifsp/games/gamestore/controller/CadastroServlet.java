package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.exception.DataAccessException;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
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

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CadastroServlet.class);
    private static final String VIEW_CADASTRO = "/WEB-INF/views/cadastro.jsp";

    private final UsuarioService usuarioService;

    public CadastroServlet() {
        this(new UsuarioService());
    }

    public CadastroServlet(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionAttributes.USUARIO_LOGADO) != null) {
            response.sendRedirect(request.getContextPath() + "/meus-pedidos");
            return;
        }

        request.getRequestDispatcher(VIEW_CADASTRO).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmarSenha = request.getParameter("confirmarSenha");

        try {
            usuarioService.cadastrar(nome, email, senha, confirmarSenha);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    SessionAttributes.MENSAGEM_SUCESSO,
                    "Cadastro realizado com sucesso! Faça login para continuar."
            );

            // Padrão PRG (Post-Redirect-Get) para evitar reenvio de formulário ao atualizar a página
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (RegraNegocioException e) {
            request.setAttribute("erro", e.getMessage());
            request.setAttribute("nome", nome);
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_CADASTRO).forward(request, response);
        } catch (DataAccessException e) {
            LOGGER.error("Erro ao persistir novo cadastro de usuário", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("erro", "Não foi possível concluir o cadastro no momento. Tente novamente.");
            request.setAttribute("nome", nome);
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_CADASTRO).forward(request, response);
        }
    }
}
