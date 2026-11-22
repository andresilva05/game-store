package edu.ifsp.games.gamestore.controller;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.exception.RegraNegocioException;
import edu.ifsp.games.gamestore.model.Pedido;
import edu.ifsp.games.gamestore.model.Usuario;
import edu.ifsp.games.gamestore.service.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null)
                ? (Usuario) session.getAttribute(SessionAttributes.USUARIO_LOGADO)
                : null;

        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        PedidoService service = new PedidoService();

        // TODO (Alcindo - Módulo 3):
        // Quando o carrinho estiver pronto na sessão, vamos extrair os itens daqui
        // para popular o objeto Pedido antes de chamar a finalização.
        Pedido pedido = new Pedido();
        pedido.setIdUsuario(usuarioLogado.getId());

        try {
            // Executa a transação atômica no Service
            service.finalizarCompra(pedido);

            // PADRÃO PRG (Post-Redirect-Get):
            // Redireciono com sendRedirect para evitar que o usuário dê F5/Refresh
            // e reenvie a compra duplicando o pedido no banco.
            response.sendRedirect(request.getContextPath() + "/meus-pedidos");

        } catch (RegraNegocioException e) {
            // Se falhar alguma regra de negócio, capturo a mensagem e devolvo para a tela com forward
            request.setAttribute("erro", e.getMessage());
            request.getRequestDispatcher("/carrinho/carrinho.jsp").forward(request, response);
        }
    }
}
