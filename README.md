# ✈️ FlightOnTime API (Backend Core)

O **FlightOnTime API** é o núcleo de processamento do sistema, desenvolvido com **Java 17** e a versão mais recente do **Spring Boot 4**.

Esta API RESTful atua como o orquestrador central da aplicação, garantindo a validação de dados, a lógica de negócios e a integridade na comunicação com o serviço de Machine Learning.

### 🛠️ Destaques Técnicos

* **Arquitetura Limpa:** Projeto estruturado com separação clara de responsabilidades (Controllers, Services, Repositories).
* **Integração de Microsserviços:** Comunicação síncrona com o motor de Inteligência Artificial (Python) utilizando **Spring Cloud OpenFeign**.
* **Segurança & Validação:** Implementação rigorosa de **Bean Validation** e DTOs para garantir a integridade dos inputs de voo.
* **Tratamento de Erros:** Sistema global de exceções (`@ControllerAdvice`), garantindo respostas HTTP padronizadas e amigáveis para o Frontend.
* **Documentação Viva:** Swagger UI (OpenAPI 3.1) integrado para exploração e testes de endpoints em tempo real.

---

# ☕ Backend API (Java Core)

Instruções para execução do servidor de aplicação e regras de negócio.

## 📋 Pré-requisitos
Para executar o projeto, seu ambiente deve possuir:

* **Java JDK 17** (ou superior).
* **Maven 3.8+** (O projeto possui wrapper `mvnw` embutido).
* **Porta 8080** disponível.

## 🚀 Como Rodar a Aplicação

### Opção 1: Via Linha de Comando (Terminal)
Na raiz do projeto, execute o comando do Maven Wrapper para baixar as dependências e subir o servidor:

```bash
# Windows (PowerShell/CMD)
.\mvnw spring-boot:run

# Linux / Mac
./mvnw spring-boot:run

```

### Opção 2: Via IDE (IntelliJ / Eclipse)

1. Importe o projeto como **Maven Project**.
2. Aguarde a indexação e download das dependências.
3. Localize a classe principal `FlightontimeApplication.java` (em `src/main/java`).
4. Execute como **Java Application**.

---

## 🔍 Verificando a Execução

Após a inicialização (procure por `Started FlightontimeApplication` no log), a API estará disponível em:

* **Base URL:** [http://localhost:8080](https://www.google.com/search?q=http://localhost:8080)
* **Documentação Interativa (Swagger UI):** [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)
* **Banco de Dados (H2 Console):** [http://localhost:8080/h2-console](https://www.google.com/search?q=http://localhost:8080/h2-console)

```

```
