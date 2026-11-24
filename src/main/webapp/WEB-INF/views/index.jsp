<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameStore - Vitrine de Gift Cards</title>
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
            border-bottom: 2px solid #29292e;
            padding-bottom: 14px;
            margin-bottom: 22px;
        }
        .page-header h1 {
            color: #00b37e;
            margin: 0 0 6px 0;
            font-size: 1.7rem;
        }
        .page-header p {
            color: #a8a8b3;
            margin: 0;
            font-size: 0.95rem;
        }
        .alert {
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 0.94rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
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
        .alert a {
            color: #04d361;
            font-weight: bold;
            text-decoration: none;
        }
        .alert a:hover {
            text-decoration: underline;
        }
        .filtro-card {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 24px;
        }
        .filtro-form {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            align-items: flex-end;
        }
        .filtro-grupo {
            flex: 1;
            min-width: 200px;
        }
        .filtro-grupo label {
            display: block;
            margin-bottom: 6px;
            color: #a8a8b3;
            font-size: 0.85rem;
            font-weight: bold;
        }
        .filtro-grupo input,
        .filtro-grupo select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid #323238;
            background-color: #121214;
            color: #e1e1e6;
            font-size: 0.95rem;
        }
        .filtro-grupo input:focus,
        .filtro-grupo select:focus {
            outline: none;
            border-color: #00b37e;
        }
        .filtro-acoes {
            display: flex;
            gap: 8px;
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
        .btn-disabled {
            background-color: #29292e;
            color: #7c7c8a;
            cursor: not-allowed;
            width: 100%;
        }
        .vitrine-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
            gap: 18px;
        }
        .produto-card {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            padding: 20px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            gap: 14px;
        }
        .produto-topo {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 8px;
        }
        .badge-plataforma {
            background-color: #323238;
            color: #61dafb;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.78rem;
            font-weight: bold;
        }
        .estoque-ok {
            color: #a8a8b3;
            font-size: 0.82rem;
        }
        .estoque-zero {
            color: #f75a68;
            font-size: 0.82rem;
            font-weight: bold;
        }
        .produto-titulo {
            margin: 8px 0 6px 0;
            font-size: 1.1rem;
            color: #e1e1e6;
        }
        .produto-credito {
            margin: 0;
            color: #a8a8b3;
            font-size: 0.9rem;
        }
        .produto-preco {
            margin: 10px 0 0 0;
            font-size: 1.4rem;
            font-weight: bold;
            color: #04d361;
        }
        .comprar-form {
            display: flex;
            gap: 8px;
            align-items: center;
            margin: 0;
        }
        .comprar-form input[type="number"] {
            width: 64px;
            padding: 9px 8px;
            border-radius: 6px;
            border: 1px solid #323238;
            background-color: #121214;
            color: #e1e1e6;
            font-size: 0.92rem;
            text-align: center;
        }
        .comprar-form .btn-primary {
            flex: 1;
        }
        .alerta-vazio {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 8px;
            padding: 32px;
            text-align: center;
            color: #a8a8b3;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/fragments/navbar.jsp"/>

<div class="container">
    <div class="page-header">
        <h1>🎮 Vitrine de Gift Cards</h1>
        <p>Créditos digitais para suas plataformas favoritas com entrega instantânea de código PIN.</p>
    </div>

    <c:if test="${not empty sucesso}">
        <div class="alert alert-sucesso">
            <span>✓ <c:out value="${sucesso}"/></span>
            <a href="${pageContext.request.contextPath}/carrinho">Ver Carrinho →</a>
        </div>
    </c:if>

    <c:if test="${not empty erro}">
        <div class="alert alert-erro">
            <span>⚠ <c:out value="${erro}"/></span>
        </div>
    </c:if>

    <div class="filtro-card">
        <form class="filtro-form" method="get" action="${pageContext.request.contextPath}/vitrine">
            <div class="filtro-grupo">
                <label for="busca">Buscar Gift Card</label>
                <input
                        type="text"
                        id="busca"
                        name="busca"
                        value="<c:out value='${busca}'/>"
                        placeholder="Ex.: Steam, PlayStation, Valorant..."
                />
            </div>

            <div class="filtro-grupo">
                <label for="plataformaId">Plataforma</label>
                <select id="plataformaId" name="plataformaId">
                    <option value="">Todas as plataformas</option>
                    <c:forEach var="plat" items="${plataformas}">
                        <option value="${plat.id}" ${plat.id == plataformaSelecionada ? 'selected' : ''}>
                            <c:out value="${plat.nome}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="filtro-acoes">
                <button type="submit" class="btn btn-primary">Filtrar</button>
                <c:if test="${not empty busca or not empty plataformaSelecionada}">
                    <a href="${pageContext.request.contextPath}/vitrine" class="btn btn-secondary">Limpar</a>
                </c:if>
            </div>
        </form>
    </div>

    <c:choose>
        <c:when test="${empty produtos}">
            <div class="alerta-vazio">
                <p>Nenhum Gift Card ativo encontrado para os filtros selecionados.</p>
                <c:if test="${not empty busca or not empty plataformaSelecionada}">
                    <a href="${pageContext.request.contextPath}/vitrine" class="btn btn-secondary">Ver todos os Gift Cards</a>
                </c:if>
            </div>
        </c:when>
        <c:otherwise>
            <div class="vitrine-grid">
                <c:forEach var="p" items="${produtos}">
                    <div class="produto-card">
                        <div>
                            <div class="produto-topo">
                                <span class="badge-plataforma"><c:out value="${p.plataformaNome}"/></span>
                                <c:choose>
                                    <c:when test="${p.estoque > 0}">
                                        <span class="estoque-ok">${p.estoque} em estoque</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="estoque-zero">Esgotado</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <h2 class="produto-titulo"><c:out value="${p.titulo}"/></h2>
                            <p class="produto-credito">
                                Valor de crédito: R$
                                <fmt:formatNumber value="${p.valorCredito}" minFractionDigits="2" maxFractionDigits="2"/>
                            </p>
                            <p class="produto-preco">
                                R$ <fmt:formatNumber value="${p.precoVenda}" minFractionDigits="2" maxFractionDigits="2"/>
                            </p>
                        </div>

                        <c:choose>
                            <c:when test="${p.estoque > 0}">
                                <form class="comprar-form" method="post" action="${pageContext.request.contextPath}/carrinho">
                                    <input type="hidden" name="acao" value="adicionar">
                                    <input type="hidden" name="produtoId" value="${p.id}">
                                    <input type="hidden" name="origem" value="vitrine">
                                    <input
                                            type="number"
                                            name="quantidade"
                                            value="1"
                                            min="1"
                                            max="${p.estoque}"
                                            aria-label="Quantidade"
                                            required
                                    />
                                    <button type="submit" class="btn btn-primary">+ Adicionar</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button type="button" class="btn btn-disabled" disabled>Indisponível</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
