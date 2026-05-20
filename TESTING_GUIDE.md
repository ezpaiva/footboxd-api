# 🧪 Guia de Teste de Compatibilidade

## Como Testar Que Tudo Funciona Igual

Este guia valida que o backend refatorado mantém compatibilidade total com o frontend.

---

## 1️⃣ Compilar e Iniciar

```bash
cd c:\footboxd-api

# Compilar
mvn clean compile

# Executar
mvn spring-boot:run
# OU
java -jar target/footboxd-api-0.0.1-SNAPSHOT.jar
```

Aplicação estará disponível em: `http://localhost:8080`

---

## 2️⃣ Testar Endpoints com cURL

### A. Registro de Usuário

#### ✅ Registro Válido
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "login": "joao@example.com",
    "senha": "senha123",
    "role": "USER"
  }'

# Resposta esperada: 201 ou 204 (sem body)
```

#### ❌ Erro: Nome muito curto
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Jo",
    "login": "joao@example.com",
    "senha": "senha123"
  }'

# Resposta esperada: 400 Bad Request
# {
#   "message": "Nome deve ter entre 3 e 255 caracteres",
#   "timestamp": "2024-05-20T10:30:00.123456",
#   "status": 400
# }
```

#### ❌ Erro: Email inválido
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "login": "joao-invalido",
    "senha": "senha123"
  }'

# Resposta esperada: 400 Bad Request
# {
#   "message": "Login deve ser um e-mail válido",
#   "timestamp": "2024-05-20T10:30:00.123456",
#   "status": 400
# }
```

---

### B. Login

#### ✅ Login Válido
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "joao@example.com",
    "senha": "senha123"
  }'

# Resposta esperada: 200 OK
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "nome": "João Silva",
#   "login": "joao@example.com"
# }
```

Copie o `token` para usar nos próximos testes!

#### ❌ Erro: Credenciais inválidas
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "joao@example.com",
    "senha": "senha-errada"
  }'

# Resposta esperada: 400 Bad Request
# {
#   "message": "Credenciais inválidas",
#   "timestamp": "2024-05-20T10:30:00.123456",
#   "status": 400
# }
```

---

### C. Avaliações (com Token)

#### ✅ Criar Avaliação
```bash
# Substitua TOKEN pelo token obtido no login
TOKEN="seu_token_aqui"

curl -X POST http://localhost:8080/avaliacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "fixtureId": 123,
    "tipo": "JOGO",
    "referenciaId": 0,
    "nota": 8.5
  }'

# Resposta esperada: 200 OK
# {
#   "id": 1,
#   "fixtureId": 123,
#   "tipo": "JOGO",
#   "referenciaId": 0,
#   "nota": 8.5,
#   "criadoEm": "2024-05-20T10:30:00Z"
# }
```

#### ❌ Erro: Nota fora do intervalo
```bash
curl -X POST http://localhost:8080/avaliacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "fixtureId": 123,
    "tipo": "JOGO",
    "referenciaId": 0,
    "nota": 12.5
  }'

# Resposta esperada: 400 Bad Request
# {
#   "message": "nota deve ser no máximo 10.0",
#   "timestamp": "2024-05-20T10:30:00.123456",
#   "status": 400
# }
```

#### ✅ Listar Minhas Avaliações
```bash
curl -X GET http://localhost:8080/avaliacoes/minhas \
  -H "Authorization: Bearer $TOKEN"

# Resposta esperada: 200 OK
# [
#   {
#     "id": 1,
#     "fixtureId": 123,
#     "tipo": "JOGO",
#     "referenciaId": 0,
#     "nota": 8.5,
#     "criadoEm": "2024-05-20T10:30:00Z"
#   }
# ]
```

#### ❌ Erro: Token inválido
```bash
curl -X GET http://localhost:8080/avaliacoes/minhas \
  -H "Authorization: Bearer token-invalido-aqui"

# A requisição passa pelo filter, mas endpoints protegidos retornarão 403/401
```

---

## 3️⃣ Checklist de Compatibilidade

Após os testes acima, valide:

- [ ] Registro retorna `void` (201/204) sem body
- [ ] Login retorna `{ token, nome, login }`
- [ ] Avaliações retornam com mesmos campos
- [ ] Erros retornam `{ message, timestamp, status }`
- [ ] Status HTTP corretos (400, 401, 404, etc)
- [ ] Nenhum field novo nas respostas de sucesso
- [ ] Nenhum field removido nas respostas de sucesso
- [ ] Frontend continua funcionando igual

---

## 4️⃣ Testar com Postman (Recomendado)

Para teste mais completo, use Postman:

1. Importe a coleção (se houver em `C:\Footboxd`)
2. Crie um ambiente com `base_url = http://localhost:8080`
3. Execute os testes na ordem:
   - POST /auth/register
   - POST /auth/login (salve token em variável)
   - POST /avaliacoes (use token salvo)
   - GET /avaliacoes/minhas
   - DELETE /avaliacoes/{id}

---

## 5️⃣ Validar Logs

No console da aplicação, você verá:

```
2024-05-20 10:30:45 - Tentativa de registro para login: joao@example.com
2024-05-20 10:30:46 - Usuário registrado com sucesso: joao@example.com
2024-05-20 10:30:50 - Tentativa de login para: joao@example.com
2024-05-20 10:30:51 - Login bem-sucedido: joao@example.com
2024-05-20 10:30:55 - Salvando/atualizando avaliação para fixture 123 tipo JOGO
2024-05-20 10:30:56 - Avaliação salva/atualizada com ID: 1
```

**Nível de log:** INFO para `com.footboxd`, WARN para resto (configurable)

---

## 6️⃣ Testar do Frontend React

Se tiver o frontend React em `C:\Footboxd`:

1. Certifique-se que backend está rodando em `localhost:8080`
2. Inicie o frontend
3. Teste os flows normalmente:
   - Registrar novo usuário
   - Fazer login
   - Adicionar avaliações
   - Listar avaliações
   - Deletar avaliação

**Esperado:** Tudo funciona exatamente como antes ✅

---

## 🐛 Troubleshooting

### Build falha
```bash
mvn clean install -DskipTests
```

### CORS error
- Verificar `SecurityConfig` → CORS está habilitado para localhost
- Verificar `application.properties` → porta 8080 correta

### Token inválido
- JWT secret em `application.properties` pode estar errado
- Expiration em `jwt.expiration-ms` (padrão: 24h)

### Erro 500 no banco de dados
- Verificar path H2 em `application.properties`
- Verificar permissões na pasta `C:\footboxd-api\data`

---

## ✅ Teste Automático (Opcional)

Para testar via script:

```bash
# Criar teste.sh com os curls acima
chmod +x teste.sh
./teste.sh
```

Ou usar Maven:
```bash
mvn test
# Testes unitários executam com mock
```

---

## 📝 Resultado Esperado

✅ **Todos os testes passam**  
✅ **Respostas idênticas ao código anterior**  
✅ **Erros com formato padronizado**  
✅ **Frontend continua funcionando**  
✅ **Sem breaking changes**  

---

**Refatoração validada! 🎉**
