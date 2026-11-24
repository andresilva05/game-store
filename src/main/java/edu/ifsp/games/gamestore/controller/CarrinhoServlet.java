package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Carrinho;
import edu.ifsp.games.gamestore.service.CarrinhoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/carrinho")
public class CarrinhoServlet extends HttpServlet {

    private CarrinhoService carrinhoService;

    @Override
    public void init() {
        this.carrinhoService = new CarrinhoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        Carrinho carrinho = obterCarrinhoDaSessao(session);

        if (session.getAttribute(SessionAttributes.MENSAGEM_SUCESSO) != null) {
            request.setAttribute("sucesso", session.getAttribute(SessionAttributes.MENSAGEM_SUCESSO));
            session.removeAttribute(SessionAttributes.MENSAGEM_SUCESSO);
        }
        if (session.getAttribute(SessionAttributes.MENSAGEM_ERRO) != null) {
            request.setAttribute("erro", session.getAttribute(SessionAttributes.MENSAGEM_ERRO));
            session.removeAttribute(SessionAttributes.MENSAGEM_ERRO);
        }

        request.setAttribute("carrinho", carrinho);
        request.getRequestDispatcher("/WEB-INF/views/carrinho.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);
        Carrinho carrinho = obterCarrinhoDaSessao(session);

        String acao = request.getParameter("acao");
        String origem = request.getParameter("origem");
        String destino = request.getContextPath() + "/carrinho";

        try {
            if ("adicionar".equalsIgnoreCase(acao)) {
                int produtoId = Integer.parseInt(request.getParameter("produtoId"));
                String quantidadeStr = request.getParameter("quantidade");
                int quantidade = (quantidadeStr != null && !quantidadeStr.isBlank())
                        ? Integer.parseInt(quantidadeStr)
                        : 1;

                carrinhoService.adicionarItem(carrinho, produtoId, quantidade);
                session.setAttribute(SessionAttributes.MENSAGEM_SUCESSO, "Gift Card adicionado ao carrinho!");

                if ("vitrine".equalsIgnoreCase(origem)) {
                    destino = request.getContextPath() + "/vitrine";
                }
            } else if ("atualizar".equalsIgnoreCase(acao)) {
                int produtoId = Integer.parseInt(request.getParameter("produtoId"));
                int quantidade = Integer.parseInt(request.getParameter("quantidade"));

                carrinhoService.alterarQuantidade(carrinho, produtoId, quantidade);
                String mensagem = (quantidade == 0)
                        ? "Item removido do carrinho!"
                        : "Quantidade atualizada com sucesso!";
                session.setAttribute(SessionAttributes.MENSAGEM_SUCESSO, mensagem);
            } else if ("remover".equalsIgnoreCase(acao)) {
                int produtoId = Integer.parseInt(request.getParameter("produtoId"));
                carrinhoService.removerItem(carrinho, produtoId);
                session.setAttribute(SessionAttributes.MENSAGEM_SUCESSO, "Item removido do carrinho!");
            } else if ("limpar".equalsIgnoreCase(acao)) {
                carrinhoService.limparCarrinho(carrinho);
                session.setAttribute(SessionAttributes.MENSAGEM_SUCESSO, "Carrinho esvaziado com sucesso!");
            }
        } catch (RegraNegocioException e) {
            session.setAttribute(SessionAttributes.MENSAGEM_ERRO, e.getMessage());
            if ("vitrine".equalsIgnoreCase(origem)) {
                destino = request.getContextPath() + "/vitrine";
            }
        } catch (NumberFormatException e) {
            session.setAttribute(SessionAttributes.MENSAGEM_ERRO, "Valores numéricos inválidos informados.");
            if ("vitrine".equalsIgnoreCase(origem)) {
                destino = request.getContextPath() + "/vitrine";
            }
        }

        // Padrão PRG (Post-Redirect-Get)
        response.sendRedirect(destino);
    }

    private Carrinho obterCarrinhoDaSessao(HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute(SessionAttributes.CARRINHO);
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute(SessionAttributes.CARRINHO, carrinho);
        }
        return carrinho;
    }
}
