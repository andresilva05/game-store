<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<style>
    .gs-navbar {
        background-color: #202024;
        border-bottom: 1px solid #323238;
        padding: 14px 24px;
        margin-bottom: 28px;
    }
    .gs-navbar-inner {
        max-width: 960px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 12px;
    }
    .gs-brand {
        color: #00b37e;
        text-decoration: none;
        font-size: 1.2rem;
        font-weight: bold;
        letter-spacing: 0.4px;
    }
    .gs-brand:hover {
        color: #04d361;
    }
    .gs-nav-links {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 14px;
    }
    .gs-nav-link {
        color: #e1e1e6;
        text-decoration: none;
        font-size: 0.95rem;
        padding: 6px 10px;
        border-radius: 6px;
        transition: background-color 0.15s ease;
    }
    .gs-nav-link:hover {
        background-color: #29292e;
        color: #04d361;
    }
    .gs-nav-cta {
        background-color: #00b37e;
        color: #121214;
        font-weight: bold;
    }
    .gs-nav-cta:hover {
        background-color: #04d361;
        color: #121214;
    }
    .gs-user-chip {
        color: #a8a8b3;
        font-size: 0.9rem;
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }
    .gs-badge-admin {
        background-color: #3a260a;
        color: #fba94c;
        border: 1px solid #fba94c;
        font-size: 0.72rem;
        font-weight: bold;
        padding: 2px 6px;
        border-radius: 4px;
        text-transform: uppercase;
    }
    .gs-logout-form {
        margin: 0;
        display: inline;
    }
    .gs-btn-logout {
        background-color: transparent;
        color: #f75a68;
        border: 1px solid #f75a68;
        padding: 6px 12px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.9rem;
        font-weight: bold;
        transition: background-color 0.15s ease, color 0.15s ease;
    }
    .gs-btn-logout:hover {
        background-color: #f75a68;
        color: #121214;
    }
</style>

<nav class="gs-navbar">
    <div class="gs-navbar-inner">
        <a class="gs-brand" href="${pageContext.request.contextPath}/">🎮 GameStore</a>

        <div class="gs-nav-links">
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogado}">
                    <a class="gs-nav-link" href="${pageContext.request.contextPath}/meus-pedidos">Meus Gift Cards</a>

                    <c:if test="${sessionScope.usuarioLogado.admin}">
                        <a class="gs-nav-link" href="${pageContext.request.contextPath}/admin">Painel Admin</a>
                    </c:if>

                    <span class="gs-user-chip">
                        Olá, <strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong>
                        <c:if test="${sessionScope.usuarioLogado.admin}">
                            <span class="gs-badge-admin">Admin</span>
                        </c:if>
                    </span>

                    <form class="gs-logout-form" method="post" action="${pageContext.request.contextPath}/logout">
                        <button type="submit" class="gs-btn-logout">Sair</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a class="gs-nav-link" href="${pageContext.request.contextPath}/login">Entrar</a>
                    <a class="gs-nav-link gs-nav-cta" href="${pageContext.request.contextPath}/cadastro">Criar Conta</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
