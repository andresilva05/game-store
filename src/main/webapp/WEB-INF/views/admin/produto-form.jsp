<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Admin - ${produto.id == 0 ? 'Novo' : 'Editar'} Gift Card</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/modules.css">
</head>
<body>

<div class="container-sm">
    <div class="card">
        <div class="header" style="margin-bottom: 20px;">
            <h1>${produto.id == 0 ? '➕ Novo Gift Card' : '✏️ Editar Gift Card'}</h1>
        </div>

        <form action="${pageContext.request.contextPath}/admin/produtos" method="post">
            <input type="hidden" name="acao" value="salvar">
            <input type="hidden" name="id" value="${produto.id}">

            <div class="form-group">
                <label for="titulo">Título do Gift Card:</label>
                <input type="text" id="titulo" name="titulo" value="${produto.titulo}" placeholder="Ex: Steam Wallet R$ 50" required>
            </div>

            <div class="form-group">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                    <label for="plataformaId" style="margin: 0;">Plataforma:</label>
                    <a href="${pageContext.request.contextPath}/admin/plataformas" style="color: #00b37e; font-size: 0.85em; text-decoration: none; font-weight: bold;">+ Cadastrar Plataforma</a>
                </div>
                <select id="plataformaId" name="plataformaId" required>
                    <option value="">Selecione uma plataforma...</option>
                    <c:forEach var="plat" items="${plataformas}">
                        <option value="${plat.id}" ${produto.plataformaId == plat.id ? 'selected' : ''}>
                            ${plat.nome}
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${empty plataformas}">
                    <small style="color: #f75a68; display: block; margin-top: 5px;">
                        ⚠ Nenhuma plataforma encontrada no banco. <a href="${pageContext.request.contextPath}/admin/plataformas" style="color: #61dafb;">Clique aqui para cadastrar</a> antes de criar o Gift Card.
                    </small>
                </c:if>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="valorCredito">Valor de Crédito (R$):</label>
                    <input type="number" step="0.01" min="0.01" id="valorCredito" name="valorCredito" value="${produto.valorCredito}" placeholder="50.00" required>
                </div>
                <div class="form-group">
                    <label for="precoVenda">Preço de Venda (R$):</label>
                    <input type="number" step="0.01" min="0" id="precoVenda" name="precoVenda" value="${produto.precoVenda}" placeholder="47.90" required>
                </div>
            </div>

            <div class="form-group">
                <label for="estoque">Quantidade em Estoque:</label>
                <input type="number" min="0" id="estoque" name="estoque" value="${produto.estoque}" placeholder="10" required>
            </div>

            <div class="form-group checkbox-group">
                <input type="checkbox" id="ativo" name="ativo" value="true" ${produto.ativo || produto.id == 0 ? 'checked' : ''}>
                <label for="ativo" style="margin: 0; cursor: pointer; color: #fff;">Produto Ativo (disponível para exibição e compra)</label>
            </div>

            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Salvar Gift Card</button>
                <a href="${pageContext.request.contextPath}/admin/produtos" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
