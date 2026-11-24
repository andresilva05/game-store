package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.service.PlataformaService;
import edu.ifsp.games.gamestore.service.ProdutoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"", "/vitrine"})
public class VitrineServlet extends HttpServlet {

    private ProdutoService produtoService;
    private PlataformaService plataformaService;

    @Override
    public void init() {
        this.produtoService = new ProdutoService();
        this.plataformaService = new PlataformaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute(SessionAttributes.MENSAGEM_SUCESSO) != null) {
                request.setAttribute("sucesso", session.getAttribute(SessionAttributes.MENSAGEM_SUCESSO));
                session.removeAttribute(SessionAttributes.MENSAGEM_SUCESSO);
            }
            if (session.getAttribute(SessionAttributes.MENSAGEM_ERRO) != null) {
                request.setAttribute("erro", session.getAttribute(SessionAttributes.MENSAGEM_ERRO));
                session.removeAttribute(SessionAttributes.MENSAGEM_ERRO);
            }
        }

        Integer plataformaId = extrairPlataformaId(request.getParameter("plataformaId"));
        String busca = request.getParameter("busca");
        if (busca != null) {
            busca = busca.trim();
        }

        request.setAttribute("plataformas", plataformaService.listarTodas());
        request.setAttribute("produtos", produtoService.listarAtivos(plataformaId, busca));
        request.setAttribute("plataformaSelecionada", plataformaId);
        request.setAttribute("busca", busca);

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }

    private Integer extrairPlataformaId(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            int id = Integer.parseInt(valor.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
