<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>GameStore - Meus Gift Cards</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #121214;
            color: #e1e1e6;
            margin: 0;
            padding: 0 0 20px 0;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            padding: 0 20px;
        }
        h1 {
            color: #00b37e;
            border-bottom: 2px solid #29292e;
            padding-bottom: 10px;
        }
        .alerta-vazio {
            background-color: #202024;
            border: 1px solid #323238;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            color: #a8a8b3;
        }
        .card-pedido {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .cabecalho-pedido {
            display: flex;
            justify-content: space-between;
            border-bottom: 1px solid #323238;
            padding-bottom: 10px;
            margin-bottom: 15px;
        }
        .numero-pedido {
            font-size: 1.1em;
            font-weight: bold;
            color: #00b37e;
        }
        .total-pedido {
            font-weight: bold;
            color: #e1e1e6;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            text-align: left;
            padding: 10px;
            border-bottom: 1px solid #29292e;
        }
        th {
            background-color: #121214;
            color: #a8a8b3;
        }
        .pin-badge {
            background-color: #092e20;
            color: #04d361;
            padding: 6px 12px;
            border-radius: 4px;
            font-family: monospace;
            font-size: 1.1em;
            letter-spacing: 1px;
            display: inline-block;
            border: 1px dashed #04d361;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/fragments/navbar.jsp"/>

<div class="container">
    <h1>🎮 Meus Pedidos & Códigos PIN</h1>

    <%-- Se a lista de pedidos estiver vazia ou nula --%>
    <c:if test="${empty pedidos}">
        <div class="alerta-vazio">
            <p>Você ainda não realizou compras na GameStore.</p>
        </div>
    </c:if>

    <%-- Se existirem pedidos cadastrados para o usuário --%>
    <c:if test="${not empty pedidos}">
        <c:forEach var="pedido" items="${pedidos}">
            <div class="card-pedido">
                <div class="cabecalho-pedido">
                    <span class="numero-pedido">Pedido #${pedido.id}</span>
                    <span>Data: ${pedido.dataCompra}</span>
                    <span class="total-pedido">Total: R$ ${pedido.valorCompra}</span>
                </div>

                <h3>Gift Cards & Códigos de Ativação:</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Produto ID</th>
                            <th>Preço Unitário</th>
                            <th>Código PIN (Resgate)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${pedido.itens}">
                            <tr>
                                <td>#${item.produtoId}</td>
                                <td>R$ ${item.preco}</td>
                                <td>
                                    <span class="pin-badge">${item.pin}</span>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:forEach>
    </c:if>
</div>

</body>
</html>
