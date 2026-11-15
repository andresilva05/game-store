# 🎮 GameStore — Loja Virtual de Gift Cards

> Projeto prático didático desenvolvido para a disciplina de **WEB 2 (Java EE Web)** — 4º Semestre / TSI - IFSP.

[![Java EE Web CI](https://github.com/andresilva05/game-store/actions/workflows/ci.yml/badge.svg)](https://github.com/andresilva05/game-store/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-10-blue?logo=eclipseide&logoColor=white)
![Tomcat](https://img.shields.io/badge/Apache_Tomcat-10.1+-F8DC75?logo=apachetomcat&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?logo=mysql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.14+-02303A?logo=gradle&logoColor=white)

---

## 📌 1. Visão Geral do Produto

O **GameStore** é um e-commerce especializado na venda e entrega instantânea de **Gift Cards e Créditos Digitais** (Steam, PlayStation Network, Xbox Game Pass, Riot Games, Nintendo eShop, etc.).

### Fluxo de Compra e Entrega:
1. **Catálogo & Vitrine:** O cliente navega pelos produtos disponíveis, visualiza saldo de crédito, preço de venda e disponibilidade de estoque.
2. **Carrinho de Compras:** Gestão do estado dos itens selecionados em memória via `HttpSession`.
3. **Checkout Transacional (ACID):** No momento da confirmação da compra, o sistema executa uma transação atômica que:
   - Valida regras de negócio e integridade de saldo/estoque.
   - Persiste a capa do `pedido`.
   - Gera chaves exclusivas de resgate (**Códigos PIN** no formato `XXXX-YYYY-ZZZZ`) persistidas na tabela `item_pedido`.
   - Atualiza a baixa de estoque do produto.
   - Executa `commit()` ou `rollback()` em caso de falha.
4. **Meus Gift Cards:** Área privada onde o cliente autenticado consulta o histórico de compras e visualiza seus PINs adquiridos.

---

## 🏛️ 2. Arquitetura do Sistema

A aplicação adota rigorosamente o modelo arquitetural em camadas com separação estrita de responsabilidades:

```
[ Navegador / Cliente HTTP ]
           │
           │  1. Requisições (GET/POST)
           ▼
┌─────────────────────────────────────────────────────────────┐
│ 🛡️ FILTROS & SEGURANÇA (@WebFilter & @WebListener)          │
│ • AuthenticationFilter: intercepta e protege rotas privadas  │
│ • SessionListener: auditoria de eventos e ciclo de sessão   │
└──────────────────────────────┬──────────────────────────────┘
                               │ 2. Requisição autorizada
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 🎮 CAMADA DE CONTROLE (Controller / Servlets)               │
│ • Mapeamento de rotas com @WebServlet                        │
│ • Padrão PRG (Post-Redirect-Get) contra reenvio de forms    │
│ • Despacho de requisições com RequestDispatcher (forward)   │
└──────────────────────────────┬──────────────────────────────┘
                               │ 3. Trafega JavaBeans / DTOs
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 🧠 CAMADA DE NEGÓCIO (Service Layer)                        │
│ • "A regra de negócio mora AQUI e APENAS aqui!"             │
│ • Validações de domínio disparando RegraNegocioException     │
│ • Orquestração de transações atômicas (commit/rollback)     │
└──────────────────────────────┬──────────────────────────────┘
                               │ 4. Chamadas de persistência
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 💾 CAMADA DE PERSISTÊNCIA (Padrão DAO)                      │
│ • Isolamento completo de SQL via PreparedStatement         │
│ • Prevenção estrita de SQL Injection                         │
│ • Resgate de chaves auto-incremento (RETURN_GENERATED_KEYS) │
└──────────────────────────────┬──────────────────────────────┘
                               │ 5. Pool de conexões de alta performance
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ ⚡ POOL DE CONEXÕES (HikariCP / ConnectionFactory)           │
└──────────────────────────────┬──────────────────────────────┘
                               │ 6. Operações relacionais
                               ▼
                 [ Banco de Dados MySQL 8.0+ ]
```

### Camada de Apresentação (View):
* **Páginas JSP** protegidas no diretório `/WEB-INF/` para impedir acesso direto pela URL.
* Utilização exclusiva de **JSTL** (`<c:forEach>`, `<c:if>`) e **Expression Language (`${...}`)**, eliminando *scriptlets* Java (`<% ... %>`).

---

## 👥 3. Fatiamento por Módulos & Responsabilidades da Equipe

Para garantir que todos os participantes trabalhem em todas as camadas (DAO, Service, Controller e View) e dominem a defesa técnica do projeto:

| Módulo | Responsável | Escopo Funcional | Padrões Aplicados |
| :--- | :--- | :--- | :--- |
| **Módulo 1: Catálogo Admin** | **Gustavo** | CRUD de Plataformas e Gift Cards, controle de preços e reposição de estoque. | DAO, Service, JSPs de Administração, HikariCP. |
| **Módulo 2: Autenticação & Segurança** | **Davi** | Cadastro de clientes, Login, Logout e proteção de rotas privadas. | `AuthenticationFilter` (`@WebFilter`), `SessionListener`, Sessão HTTP. |
| **Módulo 3: Vitrine & Carrinho** | **Alcindo** | Listagem inicial, filtros por plataforma, busca e gestão do carrinho de compras. | Gestão de estado em `HttpSession`, JSTL, EL, Padrão PRG. |
| **Módulo 4: Checkout & Transação ACID** | **André** | Fechamento do pedido, transação atômica, baixa de estoque e tela "Meus Gift Cards". | Transações explícitas (`commit`/`rollback`), `RETURN_GENERATED_KEYS`, `RegraNegocioException`. |

---

## ⚙️ 4. Infraestrutura & Esteira de CI/CD (GitHub Actions)

O repositório possui uma esteira automatizada configurada em `.github/workflows/ci.yml`:

* **Compilação Contínua:** Compilação do código no runner Linux com **Java 21 (Temurin)** e Gradle.
* **Execução de Testes Automatizados:** Execução obrigatória dos testes unitários em **JUnit 5** (`./gradlew test`).
* **Entrega Contínua (Artefato WAR):** Geração e publicação do arquivo `game-store-1.0-SNAPSHOT.war` na aba **Actions** para download imediato.
* **Governança de Branch (Ruleset):** A branch `main` é blindada contra *direct push*. Todo código entra via Pull Request validado com o CI verde.

---

## 🚀 5. Como Executar a Aplicação

### Pré-requisitos:
* **Java JDK 21+** instalado.
* **MySQL 8.0+** em execução.
* **Apache Tomcat 10.1+** (compatível com a especificação **Jakarta EE 10**).

### Passo 1: Inicializar o Banco de Dados
Execute o script DDL e os dados iniciais contidos no projeto:
```bash
mysql -u root -p < src/main/resources/database.sql
```

### Passo 2: Configurar Credenciais da Conexão
Caso suas credenciais locais do MySQL sejam diferentes do padrão (`root`/`root`), você pode definir variáveis de ambiente ou ajustar na classe `ConnectionFactory.java`:
```bash
export DB_URL="jdbc:mysql://localhost:3306/gamestore"
export DB_USER="seu_usuario"
export DB_PASS="sua_senha"
```

### Passo 3: Compilar e Empacotar o WAR
No diretório raiz do projeto, execute o wrapper do Gradle:
```bash
# No Windows:
.\gradlew clean test war

# No Linux/macOS:
./gradlew clean test war
```
O pacote compilado será gerado em: `build/libs/game-store-1.0-SNAPSHOT.war`.

### Passo 4: Deploy no Tomcat
1. Copie o arquivo `.war` gerado para a pasta `webapps/` da sua instalação do Apache Tomcat:
   ```bash
   cp build/libs/game-store-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/game-store.war
   ```
2. Inicie o Tomcat:
   ```bash
   $CATALINA_HOME/bin/startup.sh   # Linux/macOS
   $CATALINA_HOME/bin/startup.bat  # Windows
   ```
3. Acesse no navegador:
   👉 **`http://localhost:8080/game-store/`**

---

## 📄 Licença
Projeto acadêmico desenvolvido para fins didáticos no Instituto Federal de Educação, Ciência e Tecnologia de São Paulo (IFSP) — Câmpus Araraquara.
