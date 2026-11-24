<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Admin - Plataformas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/modules.css">
</head>
<body>

<div class="container-md">
    <div class="header">
        <div>
            <h1>🎮 Gerenciar Plataformas</h1>
            <p>Cadastro e listagem de plataformas para os Gift Cards</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/produtos" class="btn btn-secondary">← Voltar aos Produtos</a>
    </div>

    <%-- Mensagens Flash do Padrão PRG --%>
    <c:if test="${not empty sucesso}">
        <div class="alert alert-success">✓ ${sucesso}</div>
    </c:if>
    <c:if test="${not empty erro}">
        <div class="alert alert-danger">⚠ ${erro}</div>
    </c:if>

    <div class="card">
        <h3 style="margin-top: 0; color: #00b37e;">Cadastrar Nova Plataforma</h3>
        <form action="${pageContext.request.contextPath}/admin/plataformas" method="post" style="display: flex; gap: 12px; align-items: center;">
            <input type="hidden" name="acao" value="salvar">
            <input type="text" name="nome" placeholder="Ex: Steam, PlayStation, Xbox, Nintendo, Riot..." required style="flex: 1;">
            <button type="submit" class="btn btn-primary">+ Adicionar</button>
        </form>
    </div>

    <table>
        <thead>
            <tr>
                <th style="width: 80px;">ID</th>
                <th>Nome da Plataforma</th>
                <th style="width: 120px; text-align: center;">Ações</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty plataformas}">
                <tr>
                    <td colspan="3" style="text-align: center; color: #a8a8b3; padding: 25px;">
                        Nenhuma plataforma cadastrada.
                    </td>
                </tr>
            </c:if>

            <c:forEach var="plat" items="${plataformas}">
                <tr>
                    <td>#${plat.id}</td>
                    <td><strong>${plat.nome}</strong></td>
                    <td style="text-align: center;">
                        <form action="${pageContext.request.contextPath}/admin/plataformas" method="post" onsubmit="return confirm('Deseja realmente excluir esta plataforma? Se houver produtos associados a ela, a exclusão será bloqueada.');">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${plat.id}">
                            <button type="submit" class="btn btn-danger btn-sm">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>
