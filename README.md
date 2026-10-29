# NetUni

Conectando habilidades, oportunidades e estudantes.

Projeto da A3 de Ciência da Computação (UNA, 2026), para as matérias de Algoritmos e Programação e
Projeto e Engenharia de Software. É um app para estudantes da UNA oferecerem e contratarem serviços
entre si (aulas de reforço, design, planilhas, formatação de trabalho, etc.).

Importante: o app **não processa pagamento**. O valor e a forma de pagamento (Pix, dinheiro...) são
combinados no chat e só ficam registrados. O pagamento acontece fora do NetUni.

## O que tem no repositório

- `backend/`: API em Java (Spring Boot + JPA + banco H2)
- `mobile/`: app em Flutter (ainda em teste, ver `mobile/README.md`)
- `web/`: versão web (uma página só, `web/index.html`) com apresentação da ideia e acesso à mesma API. O app mobile continua sendo o app principal
- `docs/`: rotas da API, rastreabilidade do RF09, onde ficam if/else e for no código, e os testes
- `prototipo-desktop:/`: primeiro protótipo visual feito em Java Swing (fica no final deste arquivo)

## Requisitos funcionais

| RF | O que faz | Onde está no backend |
|---|---|---|
| RF01 | Cadastro | `UsuarioService.cadastrar` |
| RF02 | Login | `UsuarioService.login` |
| RF03 | Perfil e habilidades | `UsuarioService.atualizarPerfil` |
| RF04 | Publicar serviço (pedido ou oferta) | `ServicoService.criar` |
| RF05 | Editar e encerrar serviço | `ServicoService.atualizar` e `encerrar` |
| RF06 | Buscar serviços | `ServicoService.buscar` |
| RF07 | Ver detalhes do serviço | `ServicoService.visualizar` |
| RF08 | Chat da contratação | `ContratacaoService.enviarMensagem` |
| RF09 | Solicitar serviço | `ContratacaoService.solicitar` |
| RF10 | Acompanhar a contratação | `ContratacaoService.negociar` e `alterarStatus` |
| RF11 | Avaliar | `AvaliacaoService.avaliar` |
| RF12 | Portfólio | `PortfolioService` |
| RF13 | Comunidades (criar, entrar, sair) | `ComunidadeService` |
| RF14 | Postar foto, vídeo curto ou texto | `PostService.publicar` e `MidiaService.salvar` |
| RF15 | Feed vertical ranqueado, curtir e comentar | `PostService.feed`, `alternarCurtida` e `comentar` |

## Como rodar o backend

Precisa de JDK 17 ou mais novo e Maven.

```bash
cd backend
mvn test              # roda os testes
mvn spring-boot:run   # sobe a API em http://localhost:8080
```

O banco (H2) é criado sozinho na pasta `backend/data`. Na primeira vez entram alguns dados de
exemplo. A senha de todos é `123456`:

- ana@una.br
- bruno@una.br
- carla@una.br

Para testar o login:

```bash
curl -X POST localhost:8080/login -H "Content-Type: application/json" \
     -d '{"email":"bruno@una.br","senha":"123456"}'
```

As rotas estão em `docs/API.md`. Também dá para ver o banco em http://localhost:8080/h2-console (URL `jdbc:h2:file:./data/netuni`,
usuário `sa`, senha em branco).

Se quiser usar PostgreSQL em vez do H2: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`
(configure `DB_URL`, `DB_USER` e `DB_PASS`).

## Fluxo da demonstração

1. Cadastrar e fazer login
2. Preencher o perfil e as habilidades
3. Publicar um serviço
4. Com outra conta, buscar e abrir o serviço
5. Solicitar o serviço
6. Conversar e combinar valor e forma de pagamento
7. O prestador aceita e inicia
8. O contratante confirma a conclusão
9. O contratante avalia
10. O serviço aparece no portfólio do prestador

## Versão web

Abra `web/index.html` no navegador (é um arquivo só, o logo já vem dentro dele) (ou rode `python3 -m http.server -d web 5500` e acesse `http://localhost:5500`).
Ela usa a API publicada no Render. Para usar o backend local, abra `index.html?api=http://localhost:8080`.
Login de teste: `ana@una.br` / `123456`.

Telas da versão web: **Feed** (vídeos curtos, fotos e textos em tela cheia, rolando de um em um; toque para pausar,
dois toques para curtir), **Comunidades**, **Serviços**, **Chat** (as conversas das contratações, atualizando a cada
4 segundos) e **Perfil** (grade de posts). Feed e comunidades precisam do backend novo publicado.

## Publicar a API na internet (opcional)

Tem um `Dockerfile` em `backend/` e um `render.yaml` na raiz para publicar no Render (plano gratuito):
New, Blueprint, escolher este repositório. No plano gratuito a API dorme depois de um tempo sem uso
(a primeira chamada demora ~1 minuto) e o banco H2 é apagado quando ela reinicia.

## O que ainda falta

- Testar o app Flutter no emulador de ponta a ponta
- Notificações (o sino do app ainda não faz nada)
- Login com JWT (hoje é um token simples guardado no banco)
- Chat em tempo real (hoje precisa atualizar a tela)

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
