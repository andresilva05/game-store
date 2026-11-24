<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameStore - Criar Conta</title>
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
            max-width: 450px;
            margin: 28px auto;
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
        .alert-erro {
            padding: 12px 14px;
            border-radius: 6px;
            margin-bottom: 18px;
            font-size: 0.92rem;
            background-color: #2d1216;
            border: 1px solid #f75a68;
            color: #fda4af;
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
        input[type="text"],
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
        .hint {
            display: block;
            margin-top: 5px;
            font-size: 0.8rem;
            color: #8d8d99;
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
        <h1>Criar Conta</h1>
        <p class="auth-subtitle">Cadastre-se gratuitamente para comprar Gift Cards e receber seus PINs na hora.</p>

        <c:if test="${not empty erro}">
            <div class="alert-erro">
                <c:out value="${erro}"/>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/cadastro">
            <div class="form-group">
                <label for="nome">Nome completo</label>
                <input
                        type="text"
                        id="nome"
                        name="nome"
                        value="<c:out value='${nome}'/>"
                        placeholder="Seu nome"
                        minlength="2"
                        maxlength="100"
                        autocomplete="name"
                        required
                />
            </div>

            <div class="form-group">
                <label for="email">E-mail</label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        value="<c:out value='${email}'/>"
                        placeholder="seuemail@exemplo.com"
                        maxlength="100"
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
                        placeholder="Crie uma senha"
                        minlength="6"
                        maxlength="72"
                        autocomplete="new-password"
                        required
                />
                <span class="hint">Mínimo de 6 caracteres.</span>
            </div>

            <div class="form-group">
                <label for="confirmarSenha">Confirmar senha</label>
                <input
                        type="password"
                        id="confirmarSenha"
                        name="confirmarSenha"
                        placeholder="Repita sua senha"
                        minlength="6"
                        maxlength="72"
                        autocomplete="new-password"
                        required
                />
            </div>

            <button type="submit" class="btn-primary">Criar minha conta</button>
        </form>

        <div class="auth-footer">
            Já possui uma conta?
            <a href="${pageContext.request.contextPath}/login">Entrar</a>
        </div>
    </div>
</div>

</body>
</html>
