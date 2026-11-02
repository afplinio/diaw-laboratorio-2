# 🚗 AlugaCar: Sistema de Aluguel de Carros

> [!NOTE]
> Sistema web para **efetuar, modificar, cancelar e avaliar pedidos de aluguel de automóveis pela Internet**, do pedido do cliente até a execução do contrato.

<table>
  <tr>
    <td width="800px">
      <div align="justify">
        Clientes criam e acompanham pedidos de aluguel. Agentes, que podem ser empresas ou bancos, analisam esses pedidos do ponto de vista financeiro e, em caso de parecer positivo, executam o contrato, que pode incluir um contrato de crédito. Projeto desenvolvido no <b>Laboratório 02</b> da disciplina de <b>DIAW</b> da PUC Minas.
      </div>
    </td>
    <td>
      <div>
        <img src="frontend/public/logo.svg" alt="Logo do AlugaCar" width="120px"/>
      </div>
    </td>
  </tr>
</table>

---

## 🚧 Status do Projeto

![Versão](https://img.shields.io/badge/Versão-v1.0.0-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-25-007ec6?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.9-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-8-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-3FCF8E?style=for-the-badge&logo=supabase&logoColor=white)

---

## 📚 Índice

- [Links Úteis](#-links-úteis)
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Instalação e Execução](#-instalação-e-execução)
- [API](#-api)
- [Estrutura de Pastas](#-estrutura-de-pastas)
- [Demonstração](#-demonstração)
- [Testes](#-testes)
- [Documentações utilizadas](#-documentações-utilizadas)
- [Autores](#-autores)
- [Contribuição](#-contribuição)
- [Agradecimentos](#-agradecimentos)
- [Licença](#-licença)

---

## 🔗 Links Úteis

- 📖 **Histórias de usuário:** [`docs/historias-de-usuario.md`](docs/historias-de-usuario.md)
- 📐 **Modelos UML, versões e modificações:** [`docs/uml/README.md`](docs/uml/README.md)

---

## 📝 Sobre o Projeto

O objetivo é apoiar a gestão de aluguéis de automóveis, levando para a Internet um processo que envolve clientes, empresas e bancos. O sistema centraliza o pedido, a análise financeira e o contrato, e permite que cada participante acompanhe a situação do aluguel.

O uso do sistema exige cadastro prévio e login. Depois de criado, o pedido segue este ciclo:

```mermaid
stateDiagram-v2
    [*] --> EM_ANALISE: cliente cria
    EM_ANALISE --> APROVADO: agente aprova
    EM_ANALISE --> REPROVADO: agente reprova
    EM_ANALISE --> CANCELADO: cliente cancela
    APROVADO --> CANCELADO: cliente cancela
    APROVADO --> CONTRATADO: agente executa o contrato
```

Na execução do contrato, o automóvel pode ser registrado como propriedade do **cliente**, da **empresa** ou do **banco**. O aluguel pode ser associado a um **contrato de crédito** concedido por um dos bancos agentes.

---

## ✨ Funcionalidades Principais

- 🔐 **Autenticação:** cadastro prévio e login com usuário e senha para clientes e agentes.
- 👤 **CRUD de cliente:** identificação (RG, CPF, nome e endereço), profissão e até 3 entidades empregadoras com rendimentos.
- 📝 **Pedidos do cliente:** criar, consultar, modificar e cancelar pedidos, acompanhando o status.
- 📊 **Análise financeira:** agentes consultam a renda do cliente, modificam pedidos e registram um parecer.
- 📄 **Contratos:** execução de pedidos aprovados, com registro do proprietário do automóvel.
- 🏦 **Crédito:** contrato de crédito opcional, concedido por um banco agente.
- 🚘 **Automóveis:** cadastro de matrícula, ano, marca, modelo e placa.
- 🛡️ **Controle de acesso:** cada usuário só acessa as funções e os dados permitidos ao seu perfil.

---

## 🛠 Tecnologias Utilizadas

### 💻 Front-end

- **Biblioteca:** React 19
- **Linguagem:** JavaScript (ES6+)
- **Rotas:** React Router 7
- **Estilização:** CSS
- **Gerenciamento de estado:** Context API
- **Build:** Vite 8

### 🖥️ Back-end

- **Linguagem:** Java (JDK 25)
- **Framework:** Spring Boot 4.0.9
- **Build e dependências:** Apache Maven
- **Banco de dados:** Supabase (PostgreSQL)
- **ORM e migrações:** Spring Data JPA, Hibernate e Flyway
- **Autenticação:** Spring Security, JWT e BCrypt
- **Testes:** JUnit 5, MockMvc e H2

---

## 🏗 Arquitetura

O sistema é uma aplicação cliente-servidor dividida nos dois subsistemas pedidos na especificação:

- **Construção dinâmica das páginas web (`frontend`):** aplicação React que monta as telas a partir dos dados da API.
- **Gestão de pedidos e contratos (`backend`):** API em Spring Boot com a arquitetura MVC e as regras de negócio.

No MVC, o `model` guarda as entidades, os `controller` recebem as requisições e o React atua como a camada de visão. A camada `service` concentra as regras, e `repository` faz o acesso ao banco. Os `dto` separam a API das entidades, o que impede, por exemplo, a exposição do hash da senha.

```mermaid
flowchart LR
    Navegador["Navegador<br/>React + Vite"] -- "HTTP/JSON + JWT" --> Controller
    subgraph API["Spring Boot"]
        Controller --> Service --> Repository --> Model
    end
    Repository -- "JDBC" --> Banco[("Supabase<br/>PostgreSQL")]
```

### Decisões importantes

- **Login sem sessão no servidor:** após o login, cada requisição envia um token JWT válido por 8 horas. Contas excluídas perdem o acesso imediatamente, pois o usuário é relido do banco.
- **Conflitos de reserva:** um bloqueio no automóvel impede dois contratos simultâneos no mesmo período, e a coluna `versao` evita que alterações simultâneas em um pedido se sobrescrevam.
- **Banco versionado:** o Flyway cria e atualiza as tabelas, e o Hibernate apenas valida se elas correspondem às entidades.
- **Limitação do protótipo:** o cadastro de agentes é público. Em produção, ele precisaria de uma aprovação.

### Diagramas (v2)

| Casos de uso | Pacotes (visão lógica) |
| :---: | :---: |
| <img src="docs/uml/v2/casos-de-uso.png" alt="Diagrama de casos de uso" width="420px"> | <img src="docs/uml/v2/pacotes.png" alt="Diagrama de pacotes" width="320px"> |

<img src="docs/uml/v2/classes.png" alt="Diagrama de classes" width="800px">

As versões anteriores e a comparação entre os modelos estão em [`docs/uml/README.md`](docs/uml/README.md).

---

## 🔧 Instalação e Execução

### Pré-requisitos

- **Java JDK:** versão 25
- **Node.js:** versão 24 (LTS) e npm
- **Supabase:** um projeto próprio

O Maven não precisa ser instalado, pois o repositório inclui o Maven Wrapper (`./mvnw`).

### 🔑 Variáveis de Ambiente

#### 1 Back-end (Spring Boot)

Copie o arquivo de exemplo e preencha os valores:

```bash
cp backend/.env.example backend/.env
```

No Supabase, os dados de conexão ficam em **Connect > Direct > Session pooler > JDBC**.

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | URL JDBC do Session pooler | `jdbc:postgresql://aws-1-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres.<PROJECT_REF>` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `<SENHA>` |
| `JWT_SECRET` | Chave Base64 com pelo menos 32 bytes (`openssl rand -base64 48`) | `<CHAVE>` |
| `SERVER_PORT` | Porta do back-end (opcional) | `8080` |
| `CORS_ORIGENS` | Origens permitidas (opcional) | `http://localhost:5173` |

> [!IMPORTANT]
> O arquivo `backend/.env` contém segredos e é ignorado pelo Git. Nunca faça commit dele.

#### 2 Front-end (React, Vite)

Nenhuma configuração é necessária em desenvolvimento, pois o Vite encaminha `/api` para `http://localhost:8080`. Se o back-end estiver em outro endereço, defina `VITE_API_URL` em `frontend/.env` (veja `frontend/.env.example`).

### 📦 Instalação de Dependências

```bash
git clone https://github.com/afplinio/diaw-laboratorio-2.git
cd diaw-laboratorio-2
cd frontend && npm install && cd ..
```

O Maven Wrapper baixa as dependências do back-end na primeira execução.

### 💾 Inicialização do Banco de Dados

Nenhum script manual é necessário. Ao iniciar, o back-end aplica as migrações de `backend/src/main/resources/db/migration` no Supabase.

### ▶️ Como Executar a Aplicação

#### Terminal 1: Back-end (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

#### Terminal 2: Front-end (React, Vite)

```bash
cd frontend
npm run dev
```

Acesse **http://localhost:5173**.

### 🧭 Primeiro uso

1. Cadastre um agente do tipo **Empresa** e cadastre um automóvel.
2. Cadastre um **Cliente** e crie um pedido.
3. Entre como agente, avalie o pedido e execute o contrato.
4. Para testar o crédito, cadastre também um agente do tipo **Banco**.

---

## 🔌 API

Todas as rotas exigem o cabeçalho `Authorization: Bearer <token>`, exceto as marcadas como públicas.

| Método | Rota | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/login` | Público | Autentica e devolve o token |
| GET | `/api/auth/me` | Logado | Dados do usuário atual |
| POST | `/api/clientes` | Público | Cadastra um cliente |
| GET | `/api/clientes` | Agente | Lista os clientes |
| GET | `/api/clientes/{id}` | Próprio ou agente | Consulta um cliente |
| PUT | `/api/clientes/{id}` | Próprio | Altera os dados |
| DELETE | `/api/clientes/{id}` | Próprio | Exclui a conta |
| POST | `/api/agentes` | Público | Cadastra um agente |
| GET | `/api/agentes/bancos` | Agente | Lista os bancos |
| GET | `/api/automoveis` | Logado | Lista os automóveis (`?disponiveis=true`) |
| GET | `/api/automoveis/{id}` | Logado | Consulta um automóvel |
| POST | `/api/automoveis` | Agente | Cadastra um automóvel |
| PUT | `/api/automoveis/{id}` | Proprietário | Altera um automóvel |
| DELETE | `/api/automoveis/{id}` | Proprietário | Exclui um automóvel |
| POST | `/api/pedidos` | Cliente | Cria um pedido |
| GET | `/api/pedidos` | Logado | Lista os pedidos (`?status=` para agentes) |
| GET | `/api/pedidos/{id}` | Dono ou agente | Consulta um pedido |
| PUT | `/api/pedidos/{id}` | Dono ou agente | Modifica um pedido |
| POST | `/api/pedidos/{id}/cancelamento` | Dono | Cancela um pedido |
| POST | `/api/pedidos/{id}/avaliacao` | Agente | Registra o parecer financeiro |
| POST | `/api/pedidos/{id}/contrato` | Agente | Executa o contrato |
| GET | `/api/contratos` | Logado | Lista os contratos do usuário |

---

## 📂 Estrutura de Pastas

```
.
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/br/pucminas/diaw/aluguel/
│       │   │   ├── controller/   # Endpoints da API
│       │   │   ├── dto/          # Dados de entrada e saída
│       │   │   ├── exception/    # Respostas de erro
│       │   │   ├── model/        # Entidades do domínio
│       │   │   ├── repository/   # Acesso ao banco
│       │   │   ├── security/     # Login e JWT
│       │   │   └── service/      # Regras de negócio
│       │   └── resources/db/migration/  # Migrações do banco
│       └── test/                 # Testes da API
├── docs/
│   ├── historias-de-usuario.md
│   ├── imagens/                  # Capturas de tela
│   └── uml/                      # Diagramas v1 e v2
└── frontend/
    └── src/
        ├── api/                  # Comunicação com o back-end
        ├── components/           # Componentes reutilizáveis
        ├── context/              # Estado de autenticação
        └── pages/                # Telas do sistema
```

---

## 🎥 Demonstração

As telas abaixo usam dados fictícios.

| Login | Cadastro |
| :---: | :---: |
| <img src="docs/imagens/login.png" alt="Tela de login" width="420px"> | <img src="docs/imagens/cadastro.png" alt="Tela de cadastro" width="420px"> |

| Pedidos do cliente | Análise financeira |
| :---: | :---: |
| <img src="docs/imagens/pedidos-cliente.png" alt="Lista de pedidos do cliente" width="420px"> | <img src="docs/imagens/analise-financeira.png" alt="Tela de análise financeira" width="420px"> |

| Execução do contrato | Contratos |
| :---: | :---: |
| <img src="docs/imagens/execucao-contrato.png" alt="Tela de execução do contrato" width="420px"> | <img src="docs/imagens/contratos.png" alt="Lista de contratos" width="420px"> |

| Automóveis | Celular |
| :---: | :---: |
| <img src="docs/imagens/automoveis.png" alt="Lista de automóveis" width="420px"> | <img src="docs/imagens/mobile-novo-pedido.png" alt="Novo pedido em tela de celular" width="220px"> |

---

## 🧪 Testes

Os testes percorrem a API completa, com um banco H2 em memória. Eles não acessam o Supabase.

```bash
cd backend
./mvnw test
```

São 38 testes de autenticação, permissões, CRUD de cliente, automóveis, pedidos, contratos e crédito.

---

## 🔗 Documentações utilizadas

- [Spring Boot](https://docs.spring.io/spring-boot/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [React](https://react.dev/)
- [Vite](https://vite.dev/)
- [Supabase](https://supabase.com/docs)
- [Flyway](https://documentation.red-gate.com/flyway)
- [PlantUML](https://plantuml.com/)

---

## 👥 Autores

| Nome | GitHub |
| :--- | :--- |
| Plínio Ângelo Ferreira | [@afplinio](https://github.com/afplinio) |

---

## 🤝 Contribuição

Este é um projeto acadêmico. Sugestões podem ser enviadas por meio de [issues](https://github.com/afplinio/diaw-laboratorio-2/issues).

---

## 🙏 Agradecimentos

Ao professor João Paulo Carneiro Aramuni e à PUC Minas, pela proposta do laboratório e pelo template deste README.

---

## 📄 Licença

Projeto acadêmico, sem licença de uso definida.
