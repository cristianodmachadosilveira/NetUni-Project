# NetUni

**Conectando habilidades, oportunidades e estudantes.**

App mobile para estudantes da UNA oferecerem e contratarem serviços entre si: pedidos, ofertas,
negociação, contratação, avaliação e portfólio. Projeto da A3 de Ciência da Computação (2026).

> **Regra de pagamento:** o NetUni só registra o valor e a forma de pagamento combinados.
> O pagamento acontece fora do app (Pix, dinheiro, link). Não há carteira, checkout nem comissão.

## Estrutura

| Pasta | O que é |
|---|---|
| `backend/` | API REST em Java 17 + Spring Boot 3 + Spring Data JPA/Hibernate + H2 (ou PostgreSQL) |
| `mobile/` | App Flutter com as 14 telas, consumindo a API ([como rodar](mobile/README.md)) |
| `docs/` | [API](docs/API.md), [rastreabilidade do RF09](docs/rastreabilidade.md), [algoritmos](docs/algoritmos.md), [testes](docs/testes.md) |
| `prototipo-desktop:/` | Protótipo visual antigo em Java Swing (veja abaixo) |

Camadas do backend (`backend/src/main/java/br/una/netuni/`):
`controller` → `service` (regras) → `repository` (JPA) → `model` (entidades), com `dto`, `exception` e `config`.

## Requisitos funcionais

| RF | Requisito | Backend | Tela |
|---|---|---|---|
| RF01 | Cadastro de usuário | `UsuarioService.cadastrar` | Cadastro |
| RF02 | Login | `UsuarioService.login` + `AutenticacaoInterceptor` | Login |
| RF03 | Gerenciamento do perfil | `UsuarioService.atualizarPerfil` | Perfil |
| RF04 | Publicação de serviços | `ServicoService.criar` | Criar serviço |
| RF05 | Gerenciamento de serviços | `ServicoService.atualizar/encerrar` | Detalhes / Perfil |
| RF06 | Busca de serviços | `ServicoService.buscar` | Home / Busca |
| RF07 | Visualização do serviço | `ServicoService.visualizar` | Detalhes |
| RF08 | Comunicação | `ContratacaoService.enviarMensagem` | Conversa |
| RF09 | Solicitação de serviço | `ContratacaoService.solicitar` | Solicitação |
| RF10 | Gerenciamento da contratação | `ContratacaoService.negociar/alterarStatus` | Contratação |
| RF11 | Avaliação | `AvaliacaoService.avaliar` | Avaliação |
| RF12 | Portfólio | `PortfolioService` | Portfólio |

## Como rodar o backend

Pré-requisitos: JDK 17+ e Maven.

```bash
cd backend
mvn test              # roda os testes automatizados
mvn spring-boot:run   # sobe a API em http://localhost:8080
```

O banco H2 fica em `backend/data/` e é criado sozinho. Na primeira execução são criados alunos de
demonstração (senha `123456`): `ana@una.br`, `bruno@una.br`, `carla@una.br`.
Console do banco: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/netuni`, usuário `sa`).

Teste rápido:

```bash
curl -X POST localhost:8080/login -H "Content-Type: application/json" \
     -d '{"email":"bruno@una.br","senha":"123456"}'
```

Para usar PostgreSQL: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`
(variáveis `DB_URL`, `DB_USER`, `DB_PASS`).

## Publicar a API na internet (Render, gratuito)

Assim o app funciona em qualquer celular, sem o computador ligado.

1. Crie uma conta em https://render.com entrando com o GitHub.
2. Clique em **New → Blueprint** e escolha o repositório `NetUni-Project`. O Render lê o arquivo
   `render.yaml`, monta a imagem com o `backend/Dockerfile` (os testes rodam durante a montagem) e publica.
3. Ao terminar, copie o endereço, algo como `https://netuni-api.onrender.com`.
4. Rode o app apontando para ele:
   `flutter run --dart-define=API_URL=https://netuni-api.onrender.com`
   (com HTTPS não precisa liberar `usesCleartextTraffic`).

Limites do plano gratuito: a API "dorme" após 15 minutos sem uso e a primeira chamada demora
cerca de 1 minuto para acordar; o banco H2 é apagado quando o serviço reinicia (os dados de
demonstração são recriados sozinhos). Para guardar dados de verdade, crie um PostgreSQL no Render e
defina `SPRING_PROFILES_ACTIVE=postgres`, `DB_URL`, `DB_USER` e `DB_PASS` no serviço.
Antes da apresentação, abra o endereço da API uma vez para "acordá-la".

## Fluxo da demonstração

1. Cadastro → 2. Login → 3. Perfil → 4. Habilidades → 5. Publicar serviço →
6. Outro aluno busca → 7. Visualiza → 8. Solicita → 9. Conversam → 10. Registram valor e forma de pagamento →
11. Prestador aceita e inicia → 12. Contratante confirma a conclusão → 13. Avalia → 14. Serviço aparece no portfólio.

## Próximas etapas (backlog)

Notificações, autenticação JWT, chat em tempo real (WebSocket), upload de imagem de perfil.

---

# Protótipo desktop (Java Swing)


O **NetUni** é um protótipo de aplicativo mobile desenvolvido inteiramente em **Java Swing**. O objetivo da aplicação é conectar alunos universitários (como a comunidade da UNA, simulada no código), facilitando a oferta, solicitação e negociação de serviços acadêmicos e gerais (ex: formatação ABNT, aulas particulares, design).

Este projeto simula o fluxo de telas de um smartphone (resolução 360x640) utilizando `CardLayout` para a navegação fluida entre os componentes.

---

## 🚀 Funcionalidades e Requisitos (RFs)

O protótipo mapeia visualmente os seguintes Requisitos Funcionais (RF):

*   **RF01 - Criação de Conta:** Botão de cadastro na tela inicial.
*   **RF02 - Autenticação:** Login simulado com e-mail institucional e senha.
*   **RF03 - Perfil de Usuário:** Visualização dos dados do aluno (curso, semestre, habilidades).
*   **RF04 - Publicação de Serviço:** Botão flutuante (FAB - `+`) na Home para criar novos anúncios.
*   **RF05 / RF07 - Feed de Anúncios:** Lista interativa de serviços e pedidos na tela Home.
*   **RF06 - Busca:** Barra de pesquisa no cabeçalho para filtrar serviços.
*   **RF08 - Chat de Negociação:** Tela dedicada para troca de mensagens entre contratante e prestador.
*   **RF09 - Contratação:** Botão de "Contratar" dentro do Chat para oficializar o acordo.
*   **RF10 - Confirmação:** Alerta de sucesso ao solicitar/contratar um serviço.
*   **RF11 - Avaliação:** Simulação de pop-up para deixar avaliação após a conclusão do serviço.
*   **RF12 - Portfólio/Histórico:** Exibição do histórico de avaliações (estrelas) na tela de Perfil.

---

## 🎨 Estrutura de Telas

O app é dividido em 4 telas principais (`Cards`):

1.  **LOGIN (`createLoginScreen`):** Tela inicial de acesso com inputs de texto e botões para entrar ou registrar. Possui ajustes específicos para renderização correta de cores no macOS.
2.  **HOME (`createHomeScreen`):** O "coração" do app. Possui:
    *   **Top Bar:** Botão de sair, barra de busca e atalho para o perfil.
    *   **Tabs:** Abas de "PEDIDOS" (Verde) e "OFERTAS" (Azul). Clicar nas abas altera o tema dinamicamente.
    *   **Feed:** Lista rolável de serviços. Clicar em um item redireciona para o Chat.
    *   **Bottom Nav:** Menu de navegação inferior (simulado).
    *   **FAB:** Botão de adição sobreposto às camadas (`JLayeredPane`).
3.  **CHAT (`createChatScreen`):** Interface de mensagens de texto com cabeçalho de negociação e botão de ação primária ("Contratar").
4.  **PROFILE (`createProfileScreen`):** Resumo acadêmico do aluno, lista de habilidades e histórico do portfólio (comentários e notas).

---

## 🛠️ Tecnologias Utilizadas

*   **Linguagem:** Java 8+
*   **Interface Gráfica:** Java AWT e Swing (`JFrame`, `JPanel`, `CardLayout`, `JLayeredPane`, etc.)
*   **Padrão de Projeto UI:** Utilização de cores institucionais customizadas (`Color RGB`) e `EmptyBorder` para espaçamentos/margins modernos.

---.

### Pré-requisitos
*   [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) instalado na máquina.



## 📌 Status do Projeto
*Protótipo visual (Mockup interativo).* Dados como nomes de usuários, mensagens no chat e itens do feed estão mockados no código para fins de demonstração da jornada do usuário.
