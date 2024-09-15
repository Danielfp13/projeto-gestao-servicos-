# projeto-servico-prestado-cliente
[![codecov](https://codecov.io/github/Danielfp13/projeto-servico-prestado-cliente/branch/main/graph/badge.svg?token=XDYE6BLRZE)](https://codecov.io/github/Danielfp13/projeto-servico-prestado-cliente)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/Danielfp13/projeto-servico-prestado-cliente/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/Danielfp13/projeto-servico-prestado-cliente/tree/main)

# Projeto API e Frontend

Este projeto é uma API desenvolvida em Spring Boot 3.0.5 com um frontend em Angular 13.3.0. A aplicação gerencia usuários, clientes e serviços, oferecendo uma camada de segurança robusta usando Spring Security e JWT. Inclui também testes unitários utilizando JUnit e Mockito para garantir a qualidade do código.

## Índice

- [Descrição do Projeto](#descrição-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Instalação e Configuração](#instalação-e-configuração)
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

- **[Spring Boot 3.0.5](https://spring.io/projects/spring-boot)**
  - Framework principal para desenvolvimento da aplicação backend, fornecendo uma configuração simplificada e suporte a diversas funcionalidades.

- **[Spring Security](https://spring.io/projects/spring-security)**
  - Framework para configuração de segurança e autenticação, utilizado para proteger endpoints e gerenciar permissões de acesso.

- **[JWT (JSON Web Token)](https://jwt.io/)**
  - Padrão de token utilizado para autenticação e autorização segura entre o cliente e o servidor.

- **[JUnit 5](https://junit.org/junit5/)**
  - Framework de testes unitários para garantir a qualidade e integridade do código.

- **[Mockito](https://site.mockito.org/)**
  - Framework para criação de mocks e testes unitários, permitindo a simulação de comportamentos e dependências.

- **[Maven](https://maven.apache.org/)**
  - Ferramenta de gerenciamento de build e dependências, utilizada para compilar, testar e empacotar a aplicação.

- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)**
  - Facilita a integração com bancos de dados relacionais, fornecendo uma abstração de repositório para operações CRUD e consultas.

- **[H2 Database](https://www.h2database.com/html/main.html) (para desenvolvimento e testes)**
  - Banco de dados em memória utilizado para testes e desenvolvimento, facilitando a configuração e a execução da aplicação localmente.

- **[Swagger/OpenAPI](https://swagger.io/tools/open-source/)**
  - Ferramenta para documentação e testes da API, permitindo a visualização e interação com os endpoints da API de forma intuitiva.

- **[Spring Boot DevTools](https://spring.io/projects/spring-boot#project-devtools)**
  - Ferramenta para desenvolvimento que oferece recursos como recarga automática e melhora a experiência de desenvolvimento.

### Frontend

- **[Angular 13.3.0](https://angular.io/)**
  - Framework principal para o desenvolvimento do frontend, com módulos e ferramentas para construir aplicações web de forma escalável e eficiente.

- **[Angular Material 13.3.9](https://material.angular.io/)**
  - Biblioteca de componentes UI baseada no Material Design, oferecendo componentes prontos e estilizados para aplicações Angular.

- **[Angular CDK 13.2.1](https://angular.cdk.dev/)**
  - Conjunto de ferramentas para ajudar no desenvolvimento de componentes personalizados e comportamentos comuns no Angular.

- **[RxJS 7.5.0](https://rxjs.dev/)**
  - Biblioteca para programação reativa usando Observables, amplamente utilizada no Angular para manipulação de eventos assíncronos e fluxos de dados.

- **[Bootstrap 5.2.0](https://getbootstrap.com/)**
  - Framework CSS para desenvolvimento responsivo e estilização da interface do usuário.

- **[jQuery 3.6.0](https://jquery.com/)**
  - Biblioteca JavaScript que simplifica a manipulação de DOM, eventos e AJAX.

- **[ngx-pagination 6.0.3](https://github.com/swimlane/ngx-pagination)**
  - Biblioteca para implementação de paginação em Angular.

- **[@auth0/angular-jwt 5.0.2](https://auth0.com/docs/quickstart/spa/angular)**
  - Biblioteca para gerenciamento de JWT (JSON Web Tokens) no Angular.

- **[universal-lexer 2.0.6](https://www.npmjs.com/package/universal-lexer)**
  - Biblioteca para análise léxica e manipulação de strings.

### DevDependencies

- **[Angular CLI 13.3.3](https://angular.io/cli)**
  - Ferramenta de linha de comando para criar, gerenciar e fazer build em projetos Angular.

- **[TypeScript 4.6.2](https://www.typescriptlang.org/)**
  - Superset do JavaScript que adiciona tipagem estática e outras funcionalidades para o desenvolvimento de aplicações robustas.

- **[Karma 6.3.0](https://karma-runner.github.io/latest/index.html)**
  - Ferramenta de execução de testes para Angular.

- **[Jasmine 4.0.0](https://jasmine.github.io/)**
  - Framework de testes para JavaScript utilizado em conjunto com o Karma para testes unitários.

- **[Jasmine-core 4.0.0](https://jasmine.github.io/)**
  - Parte central do framework Jasmine para a execução de testes unitários.

- **[karma-chrome-launcher 3.1.0](https://github.com/karma-runner/karma-chrome-launcher)**
  - Lançador de testes para o Karma que permite a execução de testes no navegador Google Chrome.

- **[karma-coverage 2.1.0](https://github.com/karma-runner/karma-coverage)**
  - Relatório de cobertura de testes para Karma, mostrando a porcentagem de código coberto pelos testes.

- **[karma-jasmine 4.0.0](https://github.com/karma-runner/karma-jasmine)**
  - Adaptador do Jasmine para o Karma, permitindo que o Karma execute testes escritos com Jasmine.

- **[karma-jasmine-html-reporter 1.7.0](https://github.com/karma-runner/karma-jasmine-html-reporter)**
  - Receptor de relatório HTML para testes Karma, fornecendo uma interface visual para os resultados dos testes.

## Instalação e Configuração

### Backend

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/seu-repositorio.git
