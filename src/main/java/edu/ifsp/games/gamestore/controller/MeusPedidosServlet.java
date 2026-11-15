package edu.ifsp.games.gamestore.controller;


import edu.ifsp.games.gamestore.model.Pedido;
import edu.ifsp.games.gamestore.service.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/meus-pedidos")
public class MeusPedidosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        int idUsuario = 1;

        PedidoService service = new PedidoService();
        List<Pedido> pedidos = service.listarPorUsuario(idUsuario);

        request.setAttribute("pedidos", pedidos);

        request.getRequestDispatcher("/WEB-INF/views/meus-pedidos.jsp").forward(request, response);

    }
}
