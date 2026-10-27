# 📱 NetUni - Protótipo Mobile

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

---

## ⚙️ Como Executar o Projeto

Como o projeto está contido em um único arquivo de classe (`NetUniApp.java`), a execução é extremamente simples, dispensando o uso de IDEs complexas se não desejar.

### Pré-requisitos
*   [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) instalado na máquina.

### Passo a passo via Terminal / CMD

1. Clone o repositório ou baixe o arquivo `NetUniApp.java` em uma pasta.
2. Abra o terminal e navegue até a pasta onde o arquivo está salvo.
3. Compile o código executando o comando:
   ```bash
   javac NetUniApp.java
   ```
4. Execute o aplicativo com o comando:
   ```bash
   java NetUniApp
   ```

> **Nota para usuários de macOS:** O código já contém correções (`setOpaque(true)` e `setBorderPainted(false)`) para garantir que as cores de fundo dos botões do Swing sejam exibidas corretamente no visual padrão do Mac.

---

## 📌 Status do Projeto
*Protótipo visual (Mockup interativo).* Dados como nomes de usuários, mensagens no chat e itens do feed estão mockados no código para fins de demonstração da jornada do usuário.
