import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class NetUniApp extends JFrame {

    private final Color COLOR_GREEN = new Color(85, 179, 104);
    private final Color COLOR_BLUE = new Color(42, 107, 181);
    private final Color COLOR_BG = new Color(230, 235, 238);

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    // Componentes dinâmicos da Home
    private JPanel topBar;
    private JPanel bottomNav;
    private JLabel fabIconLabel;

    public NetUniApp() {
        setTitle("NetUni - Protótipo Mobile Completo");
        setSize(360, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        // Criando as telas (Mapeamento dos Requisitos)
        mainCardPanel.add(createLoginScreen(), "LOGIN");      // RF01, RF02
        mainCardPanel.add(createHomeScreen(), "HOME");        // RF04, RF05, RF06, RF07
        mainCardPanel.add(createProfileScreen(), "PROFILE");  // RF03, RF12
        mainCardPanel.add(createChatScreen(), "CHAT");        // RF08, RF09, RF10

        setContentPane(mainCardPanel);
        cardLayout.show(mainCardPanel, "LOGIN"); // Inicia no Login
    }

    // ==========================================
    // TELA 1: LOGIN E CADASTRO (RF01 e RF02) - CORRIGIDA PARA MAC
    // ==========================================
    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Agrupando tudo num único Grid central para não cortar no rodapé
        JPanel centerPanel = new JPanel(new GridLayout(8, 1, 10, 15));
        centerPanel.setBorder(new EmptyBorder(70, 40, 70, 40));
        centerPanel.setBackground(Color.WHITE);

        JLabel logo = new JLabel("NetUni", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 36));
        logo.setForeground(COLOR_BLUE);

        JTextField emailField = new JTextField("aluno@una.br");
        JPasswordField passField = new JPasswordField("123456");

        JButton btnLogin = new JButton("Entrar (RF02)");
        btnLogin.setBackground(COLOR_GREEN);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);         // Força a cor aparecer no Mac
        btnLogin.setBorderPainted(false); // Força a cor aparecer no Mac
        btnLogin.addActionListener(e -> cardLayout.show(mainCardPanel, "HOME"));

        JButton btnCadastro = new JButton("Criar Conta (RF01)");
        btnCadastro.setBackground(COLOR_BLUE);
        btnCadastro.setForeground(Color.WHITE);
        btnCadastro.setOpaque(true);         // Força a cor aparecer no Mac
        btnCadastro.setBorderPainted(false); // Força a cor aparecer no Mac
        btnCadastro.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Redirecionando para Cadastro..."));

        // Adicionando na ordem de cima para baixo
        centerPanel.add(logo);
        centerPanel.add(new JLabel("E-mail Institucional:"));
        centerPanel.add(emailField);
        centerPanel.add(new JLabel("Senha:"));
        centerPanel.add(passField);
        centerPanel.add(new JLabel("")); // Espaço vazio para separar os campos dos botões
        centerPanel.add(btnLogin);
        centerPanel.add(btnCadastro);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // TELA 2: HOME - PEDIDOS E OFERTAS (RF06, RF07)
    // ==========================================
    private JPanel createHomeScreen() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(360, 640));

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBounds(0, 0, 345, 600);

        // Cabeçalho e Abas
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(createTopBar(), BorderLayout.NORTH);
        headerPanel.add(createTabs(), BorderLayout.SOUTH);
        mainContent.add(headerPanel, BorderLayout.NORTH);

        // Lista de Anúncios (Busca RF06)
        mainContent.add(createListScroll(), BorderLayout.CENTER);

        // Menu Inferior
        mainContent.add(createBottomNav(), BorderLayout.SOUTH);

        // Botão Flutuante (Publicação RF04)
        JPanel fab = createFAB();
        fab.setBounds(270, 480, 60, 60);

        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(fab, JLayeredPane.PALETTE_LAYER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(layeredPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createTopBar() {
        topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBorder(new EmptyBorder(15, 10, 15, 10));
        topBar.setBackground(COLOR_GREEN);

        JLabel backBtn = new JLabel(" Sair ", SwingConstants.CENTER);
        backBtn.setForeground(Color.WHITE);
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainCardPanel, "LOGIN"); }
        });

        JTextField searchField = new JTextField("Pesquisar... (RF06)");

        JLabel profileIcon = new JLabel(" 👤 ");
        profileIcon.setForeground(Color.PINK);
        profileIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        profileIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainCardPanel, "PROFILE"); } // Vai pro Perfil
        });

        topBar.add(backBtn, BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(profileIcon, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createTabs() {
        JPanel tabs = new JPanel(new GridLayout(1, 2));
        JButton btnPedidos = createTabButton("PEDIDOS", COLOR_GREEN);
        JButton btnOfertas = createTabButton("OFERTAS", COLOR_BLUE);

        btnPedidos.addActionListener(e -> switchTheme(true));
        btnOfertas.addActionListener(e -> switchTheme(false));

        tabs.add(btnPedidos);
        tabs.add(btnOfertas);
        return tabs;
    }

    private JButton createTabButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true); // Mac fix
        return btn;
    }

    private JScrollPane createListScroll() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(COLOR_BG);

        // Simulando anúncios. Ao clicar, vai para o CHAT (Visualização e Contato - RF07, RF08)
        listPanel.add(createListItem("Formatação de TCC ABNT", "Ana Silva", new Color(255, 204, 102)));
        listPanel.add(createListItem("Aulas de Cálculo 1", "João Pedro", new Color(204, 102, 255)));
        listPanel.add(createListItem("Criação de Logo (Design)", "Maria Clara", new Color(102, 153, 255)));

        return new JScrollPane(listPanel);
    }

    private JPanel createListItem(String servico, String nome, Color avatarColor) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setBackground(COLOR_BG);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        item.setMaximumSize(new Dimension(400, 80));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Evento: Clicou no anúncio, vai pro Chat negociar
        item.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainCardPanel, "CHAT"); }
        });

        // Avatar
        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(avatarColor);
                g.fillOval(0, 0, 40, 40);
            }
        };
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setOpaque(false);

        // Textos
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(new JLabel("<html><b>" + servico + "</b></html>"));
        textPanel.add(new JLabel(nome));

        item.add(avatar, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        return item;
    }

    private JPanel createBottomNav() {
        bottomNav = new JPanel(new GridLayout(1, 4));
        bottomNav.setBackground(COLOR_GREEN);
        bottomNav.setPreferredSize(new Dimension(360, 50));
        String[] icons = {" 👥 ", " 🏠 ", " ✉️ ", " 🔔 "};
        for (String icon : icons) {
            JLabel lbl = new JLabel(icon, SwingConstants.CENTER);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            bottomNav.add(lbl);
        }
        return bottomNav;
    }

    private JPanel createFAB() {
        JPanel fabPanel = new JPanel(new BorderLayout());
        fabPanel.setOpaque(false);
        JPanel circle = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(210, 215, 218));
                g.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        circle.setOpaque(false);
        circle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Evento de Publicação (RF04)
        circle.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Abrir formulário de Publicação de Serviço (RF04)");
            }
        });

        fabIconLabel = new JLabel("+", SwingConstants.CENTER);
        fabIconLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        fabIconLabel.setForeground(COLOR_GREEN);
        circle.add(fabIconLabel, BorderLayout.CENTER);
        fabPanel.add(circle, BorderLayout.CENTER);
        return fabPanel;
    }

    private void switchTheme(boolean isPedidos) {
        Color c = isPedidos ? COLOR_GREEN : COLOR_BLUE;
        topBar.setBackground(c);
        bottomNav.setBackground(c);
        fabIconLabel.setForeground(c);
        repaint();
    }

    // ==========================================
    // TELA 3: CHAT E CONTRATAÇÃO (RF08, RF09, RF10, RF11)
    // ==========================================
    private JPanel createChatScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header do Chat
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BLUE);
        header.setBorder(new EmptyBorder(15, 10, 15, 10));
        
        JLabel backBtn = new JLabel(" < Voltar ");
        backBtn.setForeground(Color.WHITE);
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainCardPanel, "HOME"); }
        });
        
        JLabel title = new JLabel("Negociação (RF08)", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);

        // Botão de Contratar (RF09)
        JButton btnContratar = new JButton("Contratar (RF09)");
        btnContratar.setBackground(COLOR_GREEN);
        btnContratar.setForeground(Color.WHITE);
        btnContratar.setOpaque(true); // Mac fix
        btnContratar.setBorderPainted(false); // Mac fix
        btnContratar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Serviço Solicitado com sucesso! (RF10)");
            JOptionPane.showMessageDialog(this, "Simulação: Serviço finalizado. Deixe sua Avaliação (RF11)");
        });

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(btnContratar, BorderLayout.EAST);

        // Área de Mensagens
        JTextArea chatArea = new JTextArea("Ana: Olá! Vi seu serviço de ABNT.\n\nVocê: Oi Ana! Cobro R$50 pelo PIX, pode ser?\n\nAna: Fechado! Vou clicar em Contratar.\n");
        chatArea.setEditable(false);
        chatArea.setMargin(new Insets(10,10,10,10));
        
        // Input de Mensagem
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JTextField("Digite aqui..."), BorderLayout.CENTER);
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.setOpaque(true); // Mac fix
        inputPanel.add(btnEnviar, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // TELA 4: PERFIL E PORTFÓLIO (RF03, RF12)
    // ==========================================
    private JPanel createProfileScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_GREEN);
        header.setBorder(new EmptyBorder(15, 10, 15, 10));
        JLabel backBtn = new JLabel(" < Voltar ");
        backBtn.setForeground(Color.WHITE);
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainCardPanel, "HOME"); }
        });
        JLabel title = new JLabel("Meu Perfil (RF03)", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        // Info do Perfil e Portfólio
        JPanel infoPanel = new JPanel(new GridLayout(6, 1));
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        infoPanel.setBackground(Color.WHITE);
        
        infoPanel.add(new JLabel("<html><h2>Seu Nome</h2><p>Ciência da Computação - 5º Semestre</p></html>"));
        infoPanel.add(new JLabel("<html><b>Minhas Habilidades:</b> Java, UI Design, Redação</html>"));
        infoPanel.add(new JLabel(" "));
        infoPanel.add(new JLabel("<html><h3>Meu Portfólio (RF12)</h3></html>"));
        infoPanel.add(new JLabel("⭐ 5.0 - 'Ótimo trabalho no logo!' - Maria Clara"));
        infoPanel.add(new JLabel("⭐ 4.5 - 'Me ajudou muito com ABNT' - Ana Silva"));

        panel.add(header, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new NetUniApp().setVisible(true));
    }
}