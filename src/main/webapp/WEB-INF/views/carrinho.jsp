<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameStore - Carrinho de Compras</title>
    <style>
        * {
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #121214;
            color: #e1e1e6;
            margin: 0;
            padding: 0 0 40px 0;
        }
        .container {
            max-width: 960px;
            margin: 0 auto;
            padding: 0 20px;
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
            border-bottom: 2px solid #29292e;
            padding-bottom: 14px;
            margin-bottom: 22px;
        }
        .page-header h1 {
            color: #00b37e;
            margin: 0;
            font-size: 1.7rem;
        }
        .alert {
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 0.94rem;
        }
        .alert-sucesso {
            background-color: #092e20;
            border: 1px solid #04d361;
            color: #86efac;
        }
        .alert-erro {
            background-color: #2d1216;
            border: 1px solid #f75a68;
            color: #fda4af;
        }
        .alerta-vazio {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            padding: 36px 24px;
            text-align: center;
            color: #a8a8b3;
        }
        .alerta-vazio p {
            margin: 0 0 18px 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            overflow: hidden;
            margin-bottom: 22px;
        }
        th, td {
            text-align: left;
            padding: 14px 16px;
            border-bottom: 1px solid #29292e;
        }
        th {
            background-color: #1a1a1e;
            color: #a8a8b3;
            font-size: 0.82rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .badge-plataforma {
            background-color: #323238;
            color: #61dafb;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 0.76rem;
            font-weight: bold;
            display: inline-block;
            margin-bottom: 4px;
        }
        .produto-info-sub {
            color: #a8a8b3;
            font-size: 0.84rem;
            margin-top: 2px;
        }
        .qtd-form {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 0;
        }
        .qtd-form input[type="number"] {
            width: 68px;
            padding: 7px 8px;
            border-radius: 6px;
            border: 1px solid #323238;
            background-color: #121214;
            color: #e1e1e6;
            text-align: center;
            font-size: 0.92rem;
        }
        .estoque-hint {
            display: block;
            color: #a8a8b3;
            font-size: 0.78rem;
            margin-top: 4px;
        }
        .subtotal {
            color: #04d361;
            font-weight: bold;
        }
        .resumo-card {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            padding: 22px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 18px;
        }
        .resumo-valores p {
            margin: 0 0 6px 0;
            color: #a8a8b3;
            font-size: 0.94rem;
        }
        .resumo-total {
            font-size: 1.5rem;
            font-weight: bold;
            color: #04d361;
            margin: 0;
        }
        .resumo-acoes {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }
        .resumo-acoes form {
            margin: 0;
        }
        .aviso-login {
            margin-top: 12px;
            color: #a8a8b3;
            font-size: 0.86rem;
            text-align: right;
        }
        .btn {
            display: inline-block;
            padding: 10px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: bold;
            font-size: 0.92rem;
            cursor: pointer;
            border: none;
            transition: background-color 0.15s ease;
        }
        .btn-sm {
            padding: 7px 12px;
            font-size: 0.84rem;
        }
        .btn-primary {
            background-color: #00b37e;
            color: #121214;
        }
        .btn-primary:hover {
            background-color: #04d361;
        }
        .btn-secondary {
            background-color: #29292e;
            color: #e1e1e6;
        }
        .btn-secondary:hover {
            background-color: #323238;
        }
        .btn-danger {
            background-color: #f75a68;
            color: #121214;
        }
        .btn-danger:hover {
            background-color: #e04452;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/fragments/navbar.jsp"/>

<div class="container">
    <div class="page-header">
        <h1>🛒 Meu Carrinho de Compras</h1>
        <a href="${pageContext.request.contextPath}/vitrine" class="btn btn-secondary">← Continuar Comprando</a>
    </div>

    <c:if test="${not empty sucesso}">
        <div class="alert alert-sucesso">
            ✓ <c:out value="${sucesso}"/>
        </div>
    </c:if>

    <c:if test="${not empty erro}">
        <div class="alert alert-erro">
            ⚠ <c:out value="${erro}"/>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty carrinho or carrinho.vazio}">
            <div class="alerta-vazio">
                <p>Seu carrinho de compras está vazio no momento.</p>
                <a href="${pageContext.request.contextPath}/vitrine" class="btn btn-primary">Explorar Vitrine</a>
            </div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Gift Card</th>
                        <th>Preço Unitário</th>
                        <th>Quantidade</th>
                        <th>Subtotal</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${carrinho.itens}">
                        <tr>
                            <td>
                                <span class="badge-plataforma"><c:out value="${item.produto.plataformaNome}"/></span>
                                <div><strong><c:out value="${item.produto.titulo}"/></strong></div>
                                <div class="produto-info-sub">
                                    Crédito: R$
                                    <fmt:formatNumber value="${item.produto.valorCredito}" minFractionDigits="2" maxFractionDigits="2"/>
                                </div>
                            </td>
                            <td>
                                R$ <fmt:formatNumber value="${item.produto.precoVenda}" minFractionDigits="2" maxFractionDigits="2"/>
                            </td>
                            <td>
                                <form class="qtd-form" method="post" action="${pageContext.request.contextPath}/carrinho">
                                    <input type="hidden" name="acao" value="atualizar">
                                    <input type="hidden" name="produtoId" value="${item.produto.id}">
                                    <input
                                            type="number"
                                            name="quantidade"
                                            value="${item.quantidade}"
                                            min="1"
                                            max="${item.produto.estoque}"
                                            aria-label="Quantidade"
                                            required
                                    />
                                    <button type="submit" class="btn btn-secondary btn-sm">Atualizar</button>
                                </form>
                                <span class="estoque-hint">Disponível: ${item.produto.estoque} un.</span>
                            </td>
                            <td class="subtotal">
                                R$ <fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/>
                            </td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/carrinho">
                                    <input type="hidden" name="acao" value="remover">
                                    <input type="hidden" name="produtoId" value="${item.produto.id}">
                                    <button type="submit" class="btn btn-danger btn-sm">Remover</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="resumo-card">
                <div class="resumo-valores">
                    <p>Total de itens: <strong>${carrinho.quantidadeTotal}</strong></p>
                    <div class="resumo-total">
                        Total: R$ <fmt:formatNumber value="${carrinho.valorTotal}" minFractionDigits="2" maxFractionDigits="2"/>
                    </div>
                </div>

                <div class="resumo-acoes">
                    <form method="post" action="${pageContext.request.contextPath}/carrinho">
                        <input type="hidden" name="acao" value="limpar">
                        <button type="submit" class="btn btn-secondary">Esvaziar Carrinho</button>
                    </form>

                    <form method="post" action="${pageContext.request.contextPath}/checkout">
                        <button type="submit" class="btn btn-primary">Finalizar Compra</button>
                    </form>
                </div>
            </div>

            <c:if test="${empty sessionScope.usuarioLogado}">
                <p class="aviso-login">
                    Você ainda não está logado. Ao clicar em "Finalizar Compra", será direcionado para a tela de login.
                </p>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
