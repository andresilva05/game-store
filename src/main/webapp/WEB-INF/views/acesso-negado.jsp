<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameStore - Acesso Negado</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #121214;
            color: #e1e1e6;
            margin: 0;
            padding: 0 0 40px 0;
        }
        .denied-wrapper {
            max-width: 520px;
            margin: 48px auto;
            padding: 0 16px;
        }
        .denied-card {
            background-color: #202024;
            border: 1px solid #f75a68;
            border-radius: 10px;
            padding: 32px;
            text-align: center;
        }
        .denied-code {
            font-size: 3rem;
            font-weight: bold;
            color: #f75a68;
            margin: 0 0 8px 0;
        }
        .denied-card h1 {
            margin: 0 0 12px 0;
            font-size: 1.4rem;
            color: #e1e1e6;
        }
        .denied-card p {
            color: #a8a8b3;
            line-height: 1.5;
            margin: 0 0 24px 0;
        }
        .actions {
            display: flex;
            justify-content: center;
            gap: 12px;
            flex-wrap: wrap;
        }
        .btn {
            display: inline-block;
            padding: 10px 18px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: bold;
            font-size: 0.95rem;
        }
        .btn-primary {
            background-color: #00b37e;
            color: #121214;
        }
        .btn-primary:hover {
            background-color: #04d361;
        }
        .btn-secondary {
            border: 1px solid #323238;
            color: #e1e1e6;
        }
        .btn-secondary:hover {
            background-color: #29292e;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/fragments/navbar.jsp"/>

<div class="denied-wrapper">
    <div class="denied-card">
        <p class="denied-code">403</p>
        <h1>Acesso Negado</h1>
        <p>
            Esta área é restrita a administradores da GameStore.
            <c:if test="${not empty sessionScope.usuarioLogado}">
                Sua conta (<strong><c:out value="${sessionScope.usuarioLogado.email}"/></strong>)
                possui perfil <strong><c:out value="${sessionScope.usuarioLogado.papel}"/></strong>.
            </c:if>
        </p>
        <div class="actions">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/meus-pedidos">Ir para Meus Gift Cards</a>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/">Voltar à Loja</a>
        </div>
    </div>
</div>

</body>
</html>
