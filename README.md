# 🍽️ springfood

API REST para um sistema de pedidos de comida, inspirada em plataformas como iFood. A aplicação permite o gerenciamento completo de restaurantes, produtos, usuários, pedidos e entregas, seguindo boas práticas de arquitetura, segurança e desenvolvimento com o ecossistema Spring.

---

## 🚀 Tecnologias e Ferramentas

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security (JWT/OAuth2)**
- **Bean Validation**
- **MapStruct**
- **Flyway**
- **Amazon S3**
- **Docker**
- **TestContainers + JUnit + Mockito**

---

## ⚙️ Funcionalidades Principais

- 📋 Cadastro de restaurantes, produtos, cidades e formas de pagamento  
- 📦 Gerenciamento de pedidos: emissão, confirmação, entrega e cancelamento  
- 🔐 Autenticação e autorização com JWT  
- 👥 Controle de acesso baseado em permissões e grupos de usuários  
- 🖼️ Upload de fotos de produtos com integração ao Amazon S3  
- 📊 Relatórios e estatísticas com filtros dinâmicos  
- 📧 Envio de e-mails transacionais

---

## 🔧 Arquitetura

- Arquitetura em camadas (Controller, Service, Domain, Repository)
- Padrões de **DDD** e **Clean Architecture**
- Separação entre entidades de domínio e DTOs
- Versionamento de API e suporte a **HATEOAS**
- Tratamento global de exceções

---

## ▶️ Como executar

1. Clone o repositório:
```bash
git clone https://github.com/profelienai/springfood.git
cd springfood
```

2. Configure as variáveis no `application.yml` (dados do banco, e-mail, S3, etc.)

3. Execute com Maven:
```bash
./mvnw spring-boot:run
```

---

## 🧪 Testes

- Testes unitários com **JUnit** e **Mockito**
- Testes de integração com banco real via **TestContainers**

---

## 📂 Estrutura do projeto

```
src
 └── main
      ├── java
      │   └── com.elienai.springfood
      │        ├── api
      │        ├── domain
      │        ├── repository
      │        ├── service
      │        └── core
      └── resources
           ├── application.yml
           └── db/migration
```

---

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

  
