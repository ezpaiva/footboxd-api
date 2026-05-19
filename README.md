# Footboxd API

Backend da aplicação Footboxd responsável pela autenticação de usuários e gerenciamento de avaliações de jogos de futebol. A API permite que usuários se registrem, realizem login utilizando JWT e avaliem jogos, jogadores e técnicos.

---

## Frameworks e Tecnologias no Projeto

### Spring Boot

O Spring Boot é o framework principal da aplicação, responsável por estruturar todo o backend e facilitar o desenvolvimento da API.

No projeto Footboxd, ele é utilizado para:

- Inicializar a aplicação rapidamente com configuração automática
- Criar endpoints REST através de anotações como `@RestController`
- Gerenciar o ciclo de vida da aplicação
- Executar o servidor web embutido (Tomcat)

Com isso, não é necessário configurar manualmente um servidor ou estrutura base da aplicação. [1](https://stackoverflow.com/questions/23862994/whats-the-difference-between-hibernate-and-spring-data-jpa)  

---

### Spring Framework

O Spring Framework é a base sobre a qual o Spring Boot é construído.

No projeto, ele é utilizado principalmente para:

- Injeção de dependências (`@Autowired`, `@Service`)
- Organização em camadas (controller, service, repository)
- Gerenciamento de componentes através do container Spring

Isso permite uma arquitetura desacoplada, facilitando manutenção e evolução do sistema. [2](https://tutoriais.edu.lat/pub/h2-database/h2-database-introduction/banco-de-dados-h2-introducao)  

---

### Spring Security + JWT

O Spring Security é responsável por toda a parte de segurança da API.

No projeto, ele é utilizado para:

- Proteger endpoints REST
- Gerenciar autenticação de usuários
- Interceptar requisições via filtros
- Integrar com JWT para autenticação stateless

O JWT (JSON Web Token) é utilizado da seguinte forma:

1. O usuário realiza login (`/auth/login`)
2. O backend valida as credenciais
3. Um token JWT é gerado
4. O cliente envia o token no header das requisições
5. O backend valida o token e autoriza o acesso

Isso elimina a necessidade de sessões no servidor e melhora a escalabilidade da API. [3](https://oneuptime.com/blog/post/2025-12-22-port-already-in-use-spring-boot/view)  

---

### Spring Data JPA

O Spring Data JPA é utilizado como camada de acesso a dados.

No projeto, ele é responsável por:

- Criar automaticamente consultas ao banco
- Reduzir código repetitivo (boilerplate)
- Permitir acesso ao banco através de interfaces (repositories)
- Executar operações CRUD sem SQL manual

Exemplo de uso no projeto:

- `UsuarioRepository`
- `AvaliacaoRepository`

Com isso, a aplicação trabalha diretamente com objetos Java ao invés de queries SQL. [4](https://www.javaguides.net/2023/08/port-already-in-use-spring-boot.html)  

---

### JPA (Java Persistence API)

A JPA define como os dados são mapeados entre objetos Java e o banco de dados.

No projeto, ela é utilizada para:

- Mapear entidades (`@Entity`)
- Definir tabelas e relações
- Representar dados como objetos Java

Exemplos:

- Classe `Usuario`
- Classe `Avaliacao`

A JPA funciona como ponte entre o código e o banco de dados. [4](https://www.javaguides.net/2023/08/port-already-in-use-spring-boot.html)  

---

### Hibernate

O Hibernate é a implementação da JPA utilizada pelo Spring Boot.

No projeto, ele é responsável por:

- Converter objetos Java em registros de banco
- Gerar automaticamente comandos SQL
- Gerenciar persistência de dados
- Controlar transações

Ou seja, toda a comunicação com o banco de dados é executada através do Hibernate de forma transparente. [4](https://www.javaguides.net/2023/08/port-already-in-use-spring-boot.html)  

---

### H2 Database

O banco H2 é utilizado como banco de dados da aplicação em ambiente de desenvolvimento.

No projeto, ele é usado para:

- Armazenar usuários e avaliações
- Persistir dados em arquivo local (`/data`)
- Permitir acesso via console web (`/h2-console`)

O H2 foi escolhido por:

- Não exigir instalação externa
- Ser leve e rápido
- Integrar facilmente com Spring Boot

Além disso, pode ser executado em memória ou arquivo persistente, sendo ideal para projetos acadêmicos e testes. [5](https://www.postman.com/postman/free-public-apis/request/4olcd2u/fixture-by-id)  

---

### Maven

O Maven é utilizado para gerenciamento do projeto.

No Footboxd API, ele é responsável por:

- Gerenciar dependências (Spring, JPA, Security, etc.)
- Compilar o projeto
- Executar a aplicação (`spring-boot:run`)
- Organizar a estrutura padrão do projeto

O arquivo `pom.xml` centraliza todas as configurações do projeto.

---

## Integração entre as tecnologias

No projeto Footboxd API, as tecnologias trabalham de forma integrada:

- **Spring Boot** inicia a aplicação
- **Spring Security + JWT** protege os endpoints
- **Controllers** recebem requisições HTTP
- **Services** aplicam regras de negócio
- **Repositories (Spring Data JPA)** acessam o banco
- **Hibernate/JPA** realizam persistência
- **H2 Database** armazena os dados

Essa arquitetura segue o padrão em camadas e permite escalabilidade, organização e fácil manutenção.

---

## Funcionalidades

Cadastro de usuários  
Login com autenticação JWT  
Criação de avaliações  
Atualização de avaliações (upsert)  
Avaliação de jogos  
Avaliação de jogadores  
Avaliação de técnicos  
Listagem de avaliações do usuário  
Exclusão de avaliações  

---

## Estrutura do projeto

src/
 ├── controller/  
 ├── service/  
 ├── repository/  
 ├── model/  
 ├── dto/  
 ├── security/  
 └── config/  

---

## Execução do projeto

Clone o repositório:

git clone https://github.com/ezpaiva/footboxd-api.git  
cd footboxd-api  

Execute a aplicação:

./mvnw clean spring-boot:run  

A API estará disponível em:

http://localhost:8080  

---

## Autenticação

A API utiliza autenticação JWT. Endpoints protegidos exigem:

Authorization: Bearer TOKEN  

---

## Endpoints

Autenticação:

POST /auth/register  
POST /auth/login  

Avaliações:

POST /avaliacoes  
GET /avaliacoes?fixtureId={id}  
GET /avaliacoes/minhas  
DELETE /avaliacoes/{id}  

---

## Regras de negócio

Nota entre 0 e 10  
Nota com precisão de 1 casa decimal  
Avaliação única por usuário + fixture + tipo + referenciaId  
O endpoint de criação funciona como upsert  

---

## Tipos de avaliação

JOGO - avaliação geral (referenciaId = 0)  
JOGADOR - avaliação de jogador (referenciaId = player.id)  
TECNICO - avaliação de técnico (referenciaId = coach.id)  

---

## Banco de dados

Persistência em arquivo local:

data/footboxd.mv.db  

Console H2:

http://localhost:8080/h2-console  

Configuração:

JDBC URL: jdbc:h2:file:./data/footboxd  
User: sa  
Password:  

---

## Arquivos ignorados

/target/  
data/  
*.mv.db  
*.trace.db  
*.lock.db  

---

## Observações

Arquitetura stateless com JWT  
Sem gestão de sessão no servidor  
Backend não armazena dados de jogos  
Integração com dados esportivos ocorre no frontend  
Projeto preparado para evolução com banco relacional  

---

## Autor

Ezequiel Ribeiro de Paiva