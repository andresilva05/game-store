<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameStore - Entrar</title>
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
        .auth-wrapper {
            max-width: 420px;
            margin: 32px auto;
            padding: 0 16px;
        }
        .auth-card {
            background-color: #202024;
            border: 1px solid #323238;
            border-radius: 10px;
            padding: 28px;
        }
        .auth-card h1 {
            margin: 0 0 8px 0;
            color: #00b37e;
            font-size: 1.5rem;
        }
        .auth-subtitle {
            margin: 0 0 22px 0;
            color: #a8a8b3;
            font-size: 0.92rem;
        }
        .alert {
            padding: 12px 14px;
            border-radius: 6px;
            margin-bottom: 18px;
            font-size: 0.92rem;
        }
        .alert-erro {
            background-color: #2d1216;
            border: 1px solid #f75a68;
            color: #fda4af;
        }
        .alert-sucesso {
            background-color: #092e20;
            border: 1px solid #04d361;
            color: #86efac;
        }
        .form-group {
            margin-bottom: 16px;
        }
        label {
            display: block;
            margin-bottom: 6px;
            color: #c4c4cc;
            font-size: 0.9rem;
            font-weight: bold;
        }
        input[type="email"],
        input[type="password"] {
            width: 100%;
            padding: 11px 12px;
            border-radius: 6px;
            border: 1px solid #323238;
            background-color: #121214;
            color: #e1e1e6;
            font-size: 0.95rem;
        }
        input:focus {
            outline: none;
            border-color: #00b37e;
        }
        .btn-primary {
            width: 100%;
            padding: 12px;
            background-color: #00b37e;
            color: #121214;
            border: none;
            border-radius: 6px;
            font-size: 1rem;
            font-weight: bold;
            cursor: pointer;
            margin-top: 6px;
            transition: background-color 0.15s ease;
        }
        .btn-primary:hover {
            background-color: #04d361;
        }
        .auth-footer {
            margin-top: 20px;
            text-align: center;
            color: #a8a8b3;
            font-size: 0.9rem;
        }
        .auth-footer a {
            color: #00b37e;
            text-decoration: none;
            font-weight: bold;
        }
        .auth-footer a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/fragments/navbar.jsp"/>

<div class="auth-wrapper">
    <div class="auth-card">
        <h1>Entrar na GameStore</h1>
        <p class="auth-subtitle">Acesse sua conta para finalizar compras e visualizar seus códigos PIN.</p>

        <c:if test="${not empty sucesso}">
            <div class="alert alert-sucesso">
                <c:out value="${sucesso}"/>
            </div>
        </c:if>

        <c:if test="${not empty erro}">
            <div class="alert alert-erro">
                <c:out value="${erro}"/>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="email">E-mail</label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        value="<c:out value='${email}'/>"
                        placeholder="seuemail@exemplo.com"
                        autocomplete="email"
                        required
                />
            </div>

            <div class="form-group">
                <label for="senha">Senha</label>
                <input
                        type="password"
                        id="senha"
                        name="senha"
                        placeholder="Digite sua senha"
                        autocomplete="current-password"
                        required
                />
            </div>

            <button type="submit" class="btn-primary">Entrar</button>
        </form>

        <div class="auth-footer">
            Ainda não possui conta?
            <a href="${pageContext.request.contextPath}/cadastro">Cadastre-se</a>
        </div>
    </div>
</div>

</body>
</html>
