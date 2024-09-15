# Projeto API e Frontend

[![codecov](https://codecov.io/github/Danielfp13/projeto-servico-prestado-cliente/branch/main/graph/badge.svg?token=XDYE6BLRZE)](https://codecov.io/github/Danielfp13/projeto-servico-prestado-cliente)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/Danielfp13/projeto-servico-prestado-cliente/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/Danielfp13/projeto-servico-prestado-cliente/tree/main)

Este projeto é uma API desenvolvida em Spring Boot 3.0.5 com um frontend em Angular 13.3.0. A aplicação gerencia usuários, clientes e serviços, oferecendo uma camada de segurança robusta usando Spring Security e JWT. Inclui também testes unitários utilizando JUnit e Mockito para garantir a qualidade do código.

## Índice

- [Descrição do Projeto](#descrição-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Telas](#telas)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Estrutura de Projeto](#estrutura-de-projeto)
- [Collections para Teste](#collections-para-teste)
- [Scripts SQL](#scripts-sql)
- [Instalação e Configuração](#instalação-e-configuração)
- [Deploy Backend](#deploy-backend)
- [Deploy Frontend](#deploy-frontend)
- [Uso](#uso)
- [Testes](#testes)
- [Contribuição](#contribuição)
- [Licença](#licença)

## Descrição do Projeto

Este projeto é uma aplicação completa que consiste em uma API backend desenvolvida com Spring Boot e um frontend interativo desenvolvido com Angular. A API fornece endpoints para gerenciar usuários, clientes e serviços, enquanto o frontend oferece uma interface de usuário amigável para interagir com esses dados.

## Funcionalidades

- **Gerenciamento de Usuários:** Cadastro, atualização e exclusão de usuários.
- **Gerenciamento de Clientes:** Adição e visualização de clientes.
- **Segurança:** Autenticação e autorização usando Spring Security e JWT.
- **Testes Unitários:** Cobertura de código com JUnit e Mockito.

## Tecnologias Utilizadas

### Backend

- **[Spring Boot 3.0.5](https://spring.io/projects/spring-boot)**: Framework principal para desenvolvimento da aplicação backend, fornecendo uma configuração simplificada e suporte a diversas funcionalidades.
- **[Spring Security](https://spring.io/projects/spring-security)**: Framework para configuração de segurança e autenticação, utilizado para proteger endpoints e gerenciar permissões de acesso.
- **[JWT (JSON Web Token)](https://jwt.io/)**: Padrão de token utilizado para autenticação e autorização segura entre o cliente e o servidor.
- **[JUnit 5](https://junit.org/junit5/)**: Framework de testes unitários para garantir a qualidade e integridade do código.
- **[Mockito](https://site.mockito.org/)**: Framework para criação de mocks e testes unitários, permitindo a simulação de comportamentos e dependências.
- **[Maven](https://maven.apache.org/)**: Ferramenta de gerenciamento de build e dependências, utilizada para compilar, testar e empacotar a aplicação.
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)**: Facilita a integração com bancos de dados relacionais, fornecendo uma abstração de repositório para operações CRUD e consultas.
- **[H2 Database](https://www.h2database.com/html/main.html)**: Banco de dados em memória utilizado para testes e desenvolvimento, facilitando a configuração e a execução da aplicação localmente.
- **[Swagger/OpenAPI](https://swagger.io/tools/open-source/)**: Ferramenta para documentação e testes da API, permitindo a visualização e interação com os endpoints da API de forma intuitiva.
- **[Spring Boot DevTools](https://spring.io/projects/spring-boot#project-devtools)**: Ferramenta para desenvolvimento que oferece recursos como recarga automática e melhora a experiência de desenvolvimento.

### Frontend

- **[Angular 13.3.0](https://angular.io/)**: Framework principal para o desenvolvimento do frontend, com módulos e ferramentas para construir aplicações web de forma escalável e eficiente.
- **[Angular Material 13.3.9](https://material.angular.io/)**: Biblioteca de componentes UI baseada no Material Design, oferecendo componentes prontos e estilizados para aplicações Angular.
- **[Angular CDK 13.2.1](https://angular.cdk.dev/)**: Conjunto de ferramentas para ajudar no desenvolvimento de componentes personalizados e comportamentos comuns no Angular.
- **[RxJS 7.5.0](https://rxjs.dev/)**: Biblioteca para programação reativa usando Observables, amplamente utilizada no Angular para manipulação de eventos assíncronos e fluxos de dados.
- **[Bootstrap 5.2.0](https://getbootstrap.com/)**: Framework CSS para desenvolvimento responsivo e estilização da interface do usuário.
- **[jQuery 3.6.0](https://jquery.com/)**: Biblioteca JavaScript que simplifica a manipulação de DOM, eventos e AJAX.
- **[ngx-pagination 6.0.3](https://github.com/swimlane/ngx-pagination)**: Biblioteca para implementação de paginação em Angular.
- **[@auth0/angular-jwt 5.0.2](https://auth0.com/docs/quickstart/spa/angular)**: Biblioteca para gerenciamento de JWT (JSON Web Tokens) no Angular.
- **[universal-lexer 2.0.6](https://www.npmjs.com/package/universal-lexer)**: Biblioteca para análise léxica e manipulação de strings.

## Pré-requisitos

Para rodar este projeto, você precisa ter as seguintes ferramentas instaladas:

- **Java 17 ou superior**: Necessário para executar o Spring Boot.
- **Maven 3.6.3 ou superior**: Utilizado para compilar e gerenciar dependências do backend.
- **Node.js 16.13 ou superior**: Necessário para rodar o frontend em Angular.
- **Angular CLI 13.3.0 ou superior**: Para compilar e executar o projeto frontend.
- **Git**: Para clonar o repositório.
- **Docker** (Opcional): Para deploy mais fácil da aplicação.

## Estrutura de Projeto

A estrutura do projeto está organizada da seguinte forma:

```plaintext
projeto-servico-prestado-cliente/
│
├── backend/                  # Diretório do backend (API)
│   ├── src/                  # Código-fonte da API
│   ├── pom.xml               # Configurações do Maven
│   └── Dockerfile            # Dockerfile para backend
│
├── frontend/                 # Diretório do frontend (Angular)
│   ├── src/                  # Código-fonte do frontend
│   ├── angular.json          # Configuração do Angular
│   └── Dockerfile            # Dockerfile para frontend
│
├── collections/              # Coleções de teste para Postman
├── script-SQL/               # Scripts SQL para configuração do banco de dados
├── README.md                 # Documentação do projeto
└── .circleci/                # Configurações do CircleCI para integração contínua
```

## Collections para Teste

As collections para teste da API estão localizadas na pasta [**collections para teste no postman**](./collections%20para%20teste%20no%20postman). Você pode importar esses arquivos no Postman para realizar os testes.

## Scripts SQL

Os scripts DDL e DML necessários para a configuração e povoamento do banco de dados estão na pasta [**script-SQL**](./script-SQL). Eles incluem definições de tabelas e inserções de dados para a aplicação.
