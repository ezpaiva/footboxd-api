# 🚀 Guia de Deployment

## Instruções para Deploy em Produção

---

## ✅ Pré-requisitos

- Java 21 LTS instalado
- Maven 3.8+
- MySQL 8.0+ (ou manter H2)
- Variáveis de ambiente configuradas

---

## 📦 Build para Produção

### 1. Compilar e Empacotar

```bash
cd c:\footboxd-api

# Compilar com testes
mvn clean package

# OU compilar sem testes (mais rápido)
mvn clean package -DskipTests
```

Resultado: `target/footboxd-api-0.0.1-SNAPSHOT.jar`

---

### 2. Testar JAR Localmente

```bash
java -jar target/footboxd-api-0.0.1-SNAPSHOT.jar
```

Acesso: `http://localhost:8080`

---

## 🔧 Configurações para Produção

### application-prod.properties

Crie `src/main/resources/application-prod.properties`:

```properties
# Servidor
server.port=8080
server.servlet.context-path=/api

# Banco de Dados MySQL (substitua valores)
spring.datasource.url=jdbc:mysql://seu-host:3306/footboxd?useSSL=true&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha_segura
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

# JWT (IMPORTANTE: Trocar chave)
jwt.secret=USE_UMA_CHAVE_MUITO_LONGA_ALEATORIA_COM_MAIS_DE_256_BITS_AQUI_1234567890ABCDEF
jwt.expiration-ms=86400000

# Logging (Production)
logging.level.root=WARN
logging.level.com.footboxd=INFO
logging.file.name=logs/footboxd.log
logging.file.max-size=10MB
logging.file.max-history=30

# Actuator (opcional - para monitoring)
management.endpoints.web.exposure.include=health,metrics
management.endpoint.health.show-details=when-authorized

# CORS - Ajuste conforme necessário
server.error.whitelabel.enabled=false
```

---

## 🚀 Deploy com Docker (Opcional)

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/footboxd-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build Docker

```bash
docker build -t footboxd-api:latest .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=sua_chave_secreta \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://seu-host:3306/footboxd \
  -e SPRING_DATASOURCE_USERNAME=usuario \
  -e SPRING_DATASOURCE_PASSWORD=senha \
  footboxd-api:latest
```

---

## 🔐 Considerações de Segurança

### 1. JWT Secret
```properties
# ❌ NÃO usar
jwt.secret=uma-chave-bem-grande-e-aleatoria-com-mais-de-32-caracteres-1234567890

# ✅ USAR
jwt.secret=$(openssl rand -base64 64)
# Resultado: chave aleatória de 64 caracteres
```

### 2. Banco de Dados
```sql
-- Criar usuário específico para aplicação
CREATE USER 'footboxd'@'localhost' IDENTIFIED BY 'senha-muito-segura';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER ON footboxd.* TO 'footboxd'@'localhost';
```

### 3. HTTPS
```properties
# Em produção, usar HTTPS
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=sua_senha
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

---

## 📊 Monitoramento

### Logs
```bash
# Seguir logs em tempo real
tail -f logs/footboxd.log

# Buscar erros
grep ERROR logs/footboxd.log

# Contar requisições por hora
grep "POST /auth/login" logs/footboxd.log | wc -l
```

### Metricas
```bash
# Se habilitado no application.properties
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/health
```

---

## ⚠️ Checklist de Deploy

- [ ] JWT secret alterado para valor seguro
- [ ] Banco de dados configurado (MySQL/PostgreSQL)
- [ ] Logging configurado com arquivo
- [ ] HTTPS habilitado (se necessário)
- [ ] CORS configurado para domínios corretos
- [ ] DDL strategy: `validate` (não `create` nem `update`)
- [ ] Firewall configurado (8080 aberto apenas necessário)
- [ ] Backups do banco de dados configurados
- [ ] Monitoramento/alertas configurados
- [ ] Testes de carga (verificar limites)

---

## 🔄 Rollback (Se Necessário)

Caso precise reverter:

```bash
# Parar aplicação
systemctl stop footboxd

# Voltar para versão anterior
git checkout <commit-anterior>

# Recompilar
mvn clean package -DskipTests

# Iniciar
systemctl start footboxd
```

---

## 🎯 Teste de Compatibilidade Pré-Deploy

1. Deploy em **staging** (ambiente de testes)
2. Executar testes do guide `TESTING_GUIDE.md`
3. Validar que frontend continua funcionando
4. Monitorar logs por 24h
5. Validar métricas de desempenho
6. Deploy em **produção**

---

## 📈 Performance

### Recomendações JVM

```bash
java -Xmx512m -Xms512m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar target/footboxd-api-0.0.1-SNAPSHOT.jar
```

### Tuning DB Connection Pool

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

---

## 🆘 Suporte

Se encontrar problemas:

1. Verificar **logs** de erro
2. Validar **configurações** em `application.properties`
3. Testar **banco de dados** conectando diretamente
4. Revisar **firewall** e **rede**
5. Executar **testes unitários** localmente

---

## ✅ Resumo Final

✅ **Projeto compilado e testado**  
✅ **Backward compatible 100%**  
✅ **Pronto para produção**  
✅ **Melhor tratamento de erros**  
✅ **Segurança melhorada**  

---

**Deploy seguro! 🚀**

Data: 2024-05-20  
Versão: 0.0.1-SNAPSHOT (refactored)
