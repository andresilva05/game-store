package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.model.Produto;
import edu.ifsp.games.gamestore.service.PlataformaService;
import edu.ifsp.games.gamestore.service.ProdutoService;
import edu.ifsp.games.gamestore.service.RegraNegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/admin/produtos")
public class AdminProdutoServlet extends HttpServlet {

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

        String acao = request.getParameter("acao");
        if (acao == null) {
            acao = "listar";
        }

        try {
            switch (acao) {
                case "novo" -> exibirFormularioNovo(request, response);
                case "editar" -> exibirFormularioEdicao(request, response);
                default -> listarProdutos(request, response);
            }
        } catch (RegraNegocioException e) {
            adicionarMensagemErro(request.getSession(), e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/produtos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        HttpSession session = request.getSession();

        try {
            if ("salvar".equalsIgnoreCase(acao)) {
                salvarProduto(request);
                session.setAttribute("sucesso", "Gift Card salvo com sucesso!");
            } else if ("status".equalsIgnoreCase(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ativo = Boolean.parseBoolean(request.getParameter("ativo"));
                produtoService.alternarStatus(id, ativo);
                session.setAttribute("sucesso", "Status do Gift Card atualizado!");
            } else if ("excluir".equalsIgnoreCase(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                produtoService.excluir(id);
                session.setAttribute("sucesso", "Gift Card excluído com sucesso!");
            }
        } catch (RegraNegocioException e) {
            session.setAttribute("erro", e.getMessage());
        } catch (NumberFormatException e) {
            session.setAttribute("erro", "Valores numéricos inválidos informados no formulário.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/produtos");
    }

    private void listarProdutos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Recupera e limpa mensagens da sessão (Flash messages)
        HttpSession session = request.getSession();
        if (session.getAttribute("sucesso") != null) {
            request.setAttribute("sucesso", session.getAttribute("sucesso"));
            session.removeAttribute("sucesso");
        }
        if (session.getAttribute("erro") != null) {
            request.setAttribute("erro", session.getAttribute("erro"));
            session.removeAttribute("erro");
        }

        request.setAttribute("produtos", produtoService.listarTodos());
        request.getRequestDispatcher("/WEB-INF/views/admin/produtos.jsp").forward(request, response);
    }

    private void exibirFormularioNovo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("plataformas", plataformaService.listarTodas());
        request.setAttribute("produto", new Produto());
        request.getRequestDispatcher("/WEB-INF/views/admin/produto-form.jsp").forward(request, response);
    }

    private void exibirFormularioEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        request.setAttribute("produto", produtoService.buscarPorId(id));
        request.setAttribute("plataformas", plataformaService.listarTodas());
        request.getRequestDispatcher("/WEB-INF/views/admin/produto-form.jsp").forward(request, response);
    }

    private void salvarProduto(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        int id = (idStr != null && !idStr.isBlank()) ? Integer.parseInt(idStr) : 0;
        int plataformaId = Integer.parseInt(request.getParameter("plataformaId"));
        String titulo = request.getParameter("titulo");
        BigDecimal valorCredito = new BigDecimal(request.getParameter("valorCredito").replace(",", "."));
        BigDecimal precoVenda = new BigDecimal(request.getParameter("precoVenda").replace(",", "."));
        int estoque = Integer.parseInt(request.getParameter("estoque"));
        boolean ativo = request.getParameter("ativo") != null;

        Produto produto = new Produto(id, plataformaId, titulo, valorCredito, precoVenda, estoque, ativo);
        produtoService.salvar(produto);
    }

    private void adicionarMensagemErro(HttpSession session, String msg) {
        session.setAttribute("erro", msg);
    }
}