<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Admin - Gerenciar Gift Cards</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/modules.css">
</head>
<body>

<div class="container">
    <div class="header">
        <div>
            <h1>📦 Catálogo de Gift Cards</h1>
            <p>Painel Administrativo da Loja</p>
        </div>
        <div style="display: flex; gap: 10px;">
            <a href="${pageContext.request.contextPath}/admin/plataformas" class="btn btn-secondary">🎮 Plataformas</a>
            <a href="${pageContext.request.contextPath}/admin/produtos?acao=novo" class="btn btn-primary">+ Novo Gift Card</a>
        </div>
    </div>

    <%-- Mensagens Flash do Padrão PRG --%>
    <c:if test="${not empty sucesso}">
        <div class="alert alert-success">✓ ${sucesso}</div>
    </c:if>
    <c:if test="${not empty erro}">
        <div class="alert alert-danger">⚠ ${erro}</div>
    </c:if>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Plataforma</th>
                <th>Título do Card</th>
                <th>Crédito</th>
                <th>Preço Venda</th>
                <th>Estoque</th>
                <th>Status</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty produtos}">
                <tr>
                    <td colspan="8" style="text-align: center; color: #a8a8b3; padding: 30px;">
                        Nenhum Gift Card cadastrado até o momento.
                    </td>
                </tr>
            </c:if>

            <c:forEach var="p" items="${produtos}">
                <tr>
                    <td>#${p.id}</td>
                    <td><span class="badge badge-platform">${p.plataformaNome}</span></td>
                    <td><strong>${p.titulo}</strong></td>
                    <td>R$ <fmt:formatNumber value="${p.valorCredito}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td style="color: #04d361; font-weight: bold;">R$ <fmt:formatNumber value="${p.precoVenda}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.estoque == 0}">
                                <span style="color: #f75a68; font-weight: bold;">Esgotado (0)</span>
                            </c:when>
                            <c:otherwise>${p.estoque} un.</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${p.ativo}">
                                <span class="badge badge-success">Ativo</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-danger">Inativo</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/admin/produtos?acao=editar&id=${p.id}" class="btn btn-secondary btn-sm">Editar</a>

                            <%-- Alternar Status Ativo/Inativo via POST (PRG) --%>
                            <form action="${pageContext.request.contextPath}/admin/produtos" method="post">
                                <input type="hidden" name="acao" value="status">
                                <input type="hidden" name="id" value="${p.id}">
                                <input type="hidden" name="ativo" value="${!p.ativo}">
                                <button type="submit" class="btn btn-warning btn-sm">
                                    ${p.ativo ? 'Inativar' : 'Ativar'}
                                </button>
                            </form>

                            <%-- Excluir via POST (PRG) --%>
                            <form action="${pageContext.request.contextPath}/admin/produtos" method="post" onsubmit="return confirm('Deseja realmente excluir este Gift Card? Se ele possuir vendas registradas, a operação será bloqueada pela regra de negócio.');">
                                <input type="hidden" name="acao" value="excluir">
                                <input type="hidden" name="id" value="${p.id}">
                                <button type="submit" class="btn btn-danger btn-sm">Excluir</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>
