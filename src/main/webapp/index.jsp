<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>GameStore - Início</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/modules.css">
</head>
<body>

<div class="container-sm" style="text-align: center; margin-top: 60px;">
    <div class="card">
        <h1 style="color: #00b37e; margin-top: 0;">🎮 GameStore</h1>
        <p style="color: #a8a8b3;">Loja Virtual de Gift Cards & Créditos Digitais</p>
        
        <div style="display: flex; flex-direction: column; gap: 12px; margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/admin/produtos" class="btn btn-primary" style="padding: 14px;">
                ⚙️ Acessar Painel Admin (Módulo 1)
            </a>
            <a href="${pageContext.request.contextPath}/meus-pedidos" class="btn btn-secondary" style="padding: 14px;">
                📜 Meus Pedidos & PINs (Módulo 4)
            </a>
        </div>
    </div>
</div>

</body>
</html>
