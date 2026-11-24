package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.model.Plataforma;
import edu.ifsp.games.gamestore.service.PlataformaService;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/plataformas")
public class AdminPlataformaServlet extends HttpServlet {

    private PlataformaService plataformaService;

    @Override
    public void init() {
        this.plataformaService = new PlataformaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (session.getAttribute("sucesso") != null) {
            request.setAttribute("sucesso", session.getAttribute("sucesso"));
            session.removeAttribute("sucesso");
        }
        if (session.getAttribute("erro") != null) {
            request.setAttribute("erro", session.getAttribute("erro"));
            session.removeAttribute("erro");
        }

        request.setAttribute("plataformas", plataformaService.listarTodas());
        request.getRequestDispatcher("/WEB-INF/views/admin/plataformas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        HttpSession session = request.getSession();

        try {
            if ("salvar".equalsIgnoreCase(acao)) {
                String idStr = request.getParameter("id");
                int id = (idStr != null && !idStr.isBlank()) ? Integer.parseInt(idStr) : 0;
                String nome = request.getParameter("nome");

                Plataforma p = new Plataforma(id, nome);
                plataformaService.salvar(p);
                session.setAttribute("sucesso", "Plataforma salva com sucesso!");

            } else if ("excluir".equalsIgnoreCase(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                plataformaService.excluir(id);
                session.setAttribute("sucesso", "Plataforma removida com sucesso!");
            }
        } catch (RegraNegocioException e) {
            session.setAttribute("erro", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("erro", "Erro ao processar plataforma: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/plataformas");
    }
}