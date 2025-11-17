# 🚀 Backend Setup - Sistema de Gestão de Beneficiários

Este guia fornece instruções detalhadas para configurar e executar o backend do sistema de gestão de beneficiários.

## 📋 Índice

- [Pré-requisitos](#pré-requisitos)
- [Arquitetura do Backend](#arquitetura-do-backend)
- [Configuração do Banco de Dados](#configuração-do-banco-de-dados)
- [Compilação do Projeto](#compilação-do-projeto)
- [Execução da Aplicação](#execução-da-aplicação)
- [Testando a API](#testando-a-api)
- [Documentação da API](#documentação-da-api)
- [Solução de Problemas](#solução-de-problemas)

## 🔧 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 17** ou superior
  ```bash
  java -version
  # Deve retornar: openjdk version "17.x.x" ou superior
  ```

- **Maven 3.8+**
  ```bash
  mvn -version
  # Deve retornar: Apache Maven 3.8.x ou superior
  ```

- **PostgreSQL 15** ou superior
  - Ou use Docker: `docker run --name postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15-alpine`

## 🏗️ Arquitetura do Backend

O projeto segue uma arquitetura em camadas com separação clara de responsabilidades:

```
bip-teste-integrado/
├── common-api/           # APIs e entidades compartilhadas
│   ├── entity/          # Entidade Beneficio (JPA)
│   ├── dto/             # TransferRequest
│   ├── exception/       # Exceções customizadas
│   └── service/         # Interface ITransferOperationService
│
├── ejb-module/          # Lógica de negócio EJB
│   └── service/         # BeneficioEjbService (implementação)
│
└── backend-module/      # API REST Spring Boot
    ├── web/            # Controllers REST
    ├── application/    # Services e DTOs
    └── infrastructure/ # Repositories JPA
```

### Módulos

1. **common-api**: Define contratos compartilhados (entidades, DTOs, interfaces)
2. **ejb-module**: Implementa lógica de transferências com locking pessimista
3. **backend-module**: Expõe API REST e gerencia CRUD de beneficiários

## 🗄️ Configuração do Banco de Dados

### Opção 1: PostgreSQL Local

1. **Criar o banco de dados:**
   ```bash
   psql -U postgres
   CREATE DATABASE beneficio_db;
   \q
   ```

2. **Executar scripts SQL:**
   ```bash
   # Schema (cria tabelas)
   psql -U postgres -d beneficio_db -f db/schema.sql

   # Seed (dados iniciais)
   psql -U postgres -d beneficio_db -f db/seed.sql
   ```

3. **Verificar dados:**
   ```bash
   psql -U postgres -d beneficio_db
   SELECT * FROM beneficios;
   # Deve exibir 5 beneficiários cadastrados
   ```

### Opção 2: Docker

1. **Iniciar PostgreSQL:**
   ```bash
   docker-compose up -d postgres
   ```

2. **Verificar logs:**
   ```bash
   docker-compose logs postgres
   # Deve mostrar: "database system is ready to accept connections"
   ```

Os scripts SQL serão executados automaticamente na inicialização.

### Configuração de Conexão

As credenciais padrão estão em `backend-module/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/beneficio_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## 🔨 Compilação do Projeto

O projeto usa Maven multi-módulo. **IMPORTANTE**: Compile sempre a partir do diretório raiz.

### 1. Build Completo

```bash
# A partir do diretório raiz (bip-teste-integrado/)
mvn clean install
```

Isso irá:
- Compilar `common-api` (sem dependências externas)
- Compilar `ejb-module` (depende de common-api)
- Compilar `backend-module` (depende de ejb-module e common-api)
- Executar todos os testes
- Gerar o JAR executável em `backend-module/target/`

### 2. Build sem Testes (mais rápido)

```bash
mvn clean install -DskipTests
```

### 3. Build de Módulo Específico

```bash
# Apenas o backend
mvn -f backend-module clean package

# Apenas o EJB
mvn -f ejb-module clean package
```

### Verificar o Build

Após o build, você deve ver:
```
backend-module/target/backend-module-1.0.0-exec.jar
```

## ▶️ Execução da Aplicação

### Opção 1: Maven Spring Boot Plugin (Desenvolvimento)

```bash
# A partir do diretório raiz
mvn -f backend-module spring-boot:run
```

### Opção 2: JAR Executável (Produção)

```bash
# 1. Build
mvn -f backend-module clean package -DskipTests

# 2. Run
java -jar backend-module/target/backend-module-1.0.0-exec.jar
```

### Opção 3: Docker Compose (Completo)

```bash
# Inicia PostgreSQL + Backend
docker-compose up -d
```

### Verificar Inicialização

Aguarde a mensagem:
```
Started BackendApplication in X.XXX seconds
```

A aplicação estará disponível em **http://localhost:8080**

## 🧪 Testando a API

### 1. Listar Beneficiários

```bash
curl http://localhost:8080/api/beneficios
```

**Resposta esperada:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "balance": 1000.00,
    "version": 0
  },
  ...
]
```

### 2. Buscar Beneficiário por ID

```bash
curl http://localhost:8080/api/beneficios/1
```

### 3. Criar Beneficiário

```bash
curl -X POST http://localhost:8080/api/beneficios \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "balance": 1500.00
  }'
```

**Resposta esperada (HTTP 201):**
```json
{
  "success": true,
  "message": "Beneficiary created successfully",
  "data": {
    "id": 6,
    "name": "Maria Silva",
    "balance": 1500.00,
    "version": 0
  }
}
```

### 4. Realizar Transferência

```bash
curl -X POST http://localhost:8080/api/beneficios/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromBeneficioId": 1,
    "toBeneficioId": 2,
    "amount": 200.00
  }'
```

**Resposta esperada (HTTP 200):**
```json
{
  "success": true,
  "message": "Transfer completed successfully",
  "data": null
}
```

### 5. Testar Validações

**Transferência com saldo insuficiente (HTTP 422):**
```bash
curl -X POST http://localhost:8080/api/beneficios/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromBeneficioId": 1,
    "toBeneficioId": 2,
    "amount": 999999.00
  }'
```

**Resposta esperada:**
```json
{
  "success": false,
  "message": "Saldo insuficiente para realizar a transferência",
  "data": null
}
```

**Transferência para o mesmo beneficiário (HTTP 400):**
```bash
curl -X POST http://localhost:8080/api/beneficios/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromBeneficioId": 1,
    "toBeneficioId": 1,
    "amount": 100.00
  }'
```

## 📚 Documentação da API

### Swagger UI

Acesse a documentação interativa em:

**http://localhost:8080/swagger-ui.html**

Recursos disponíveis:
- Visualizar todos os endpoints
- Testar requisições diretamente no navegador
- Ver schemas de request/response
- Códigos de status HTTP

### OpenAPI Spec

Especificação OpenAPI 3.0 em JSON:

**http://localhost:8080/v3/api-docs**

## 🎯 Endpoints Disponíveis

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/beneficios` | Lista todos os beneficiários | Não |
| GET | `/api/beneficios/{id}` | Busca beneficiário por ID | Não |
| POST | `/api/beneficios` | Cria novo beneficiário | Não |
| POST | `/api/beneficios/transfer` | Realiza transferência entre beneficiários | Não |
| POST | `/api/transferencias` | Alias para `/api/beneficios/transfer` | Não |
| GET | `/swagger-ui.html` | Documentação interativa | Não |

## 🧪 Executando Testes

### Todos os Testes

```bash
mvn test
```

### Testes do Backend

```bash
mvn -f backend-module test
```

### Teste Específico

```bash
mvn -Dtest=TransferServiceTest test -f backend-module
```

### Com Cobertura

```bash
mvn clean verify
# Relatório em: backend-module/target/site/jacoco/index.html
```

## 🐛 Solução de Problemas

### Porta 8080 já está em uso

**Erro:**
```
Port 8080 is already in use
```

**Solução:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

Ou altere a porta em `application.properties`:
```properties
server.port=8081
```

### Erro de Conexão com Banco de Dados

**Erro:**
```
Unable to acquire JDBC Connection
```

**Verificações:**
1. PostgreSQL está rodando?
   ```bash
   docker-compose ps postgres
   # ou
   psql -U postgres -c "SELECT version();"
   ```

2. Credenciais corretas?
   - Verifique `application.properties`
   - Teste conexão: `psql -U postgres -d beneficio_db`

3. Firewall bloqueando porta 5432?
   ```bash
   telnet localhost 5432
   ```

### Erros de Compilação Maven

**Erro:**
```
Could not resolve dependencies for project
```

**Soluções:**
1. Limpar cache local:
   ```bash
   mvn clean
   rm -rf ~/.m2/repository/com/example
   mvn install
   ```

2. Compilar na ordem correta:
   ```bash
   mvn -f common-api clean install
   mvn -f ejb-module clean install
   mvn -f backend-module clean install
   ```

### Testes Falhando

**Solução:**
```bash
# Recriar banco de dados
docker-compose down -v
docker-compose up -d postgres
sleep 10
mvn test
```

## 📊 Monitoramento

### Logs da Aplicação

```bash
# Maven
mvn -f backend-module spring-boot:run

# Docker
docker-compose logs -f backend

# JAR
java -jar backend-module/target/backend-module-1.0.0-exec.jar
```

### Verificar Conexões ao Banco

```bash
docker exec -it beneficio-postgres psql -U postgres -d beneficio_db

-- Ver conexões ativas
SELECT * FROM pg_stat_activity WHERE datname = 'beneficio_db';
```

## 🔐 Profiles Spring

Três profiles estão configurados:

### dev (padrão)
```bash
mvn -f backend-module spring-boot:run
# Conecta em localhost:5432
```

### docker
```bash
mvn -f backend-module spring-boot:run -Dspring-boot.run.profiles=docker
# Conecta no container 'postgres'
```

### test
```bash
mvn test
# Usa configurações de teste
```

## 📝 Notas Importantes

### Locking Pessimista

O EJB usa **locking pessimista** (`PESSIMISTIC_WRITE`) para garantir consistência em transferências concorrentes:

```java
entityManager.lock(origem, LockModeType.PESSIMISTIC_WRITE);
entityManager.lock(destino, LockModeType.PESSIMISTIC_WRITE);
```

### Transações

Todas as operações de transferência são **transacionais** (`@Transactional`). Erros causam rollback automático.

### Validações Implementadas

1. ✅ Saldo suficiente na origem
2. ✅ Valor positivo (> 0)
3. ✅ Origem ≠ Destino
4. ✅ Beneficiários existem
5. ✅ Integridade dos dados (constraints DB)

## 🔗 Links Úteis

- **API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

**Desenvolvido com Spring Boot 3.2.0 + Java 17 + PostgreSQL 15**
