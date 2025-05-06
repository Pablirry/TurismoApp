package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import model.Mensaje;
import model.Usuario;
import services.TurismoService;
import utils.I18n;
import utils.UIUtils;

public class MenuPrincipal extends JFrame {

    private static MenuPrincipal instance;

    private JPanel panelFondo;
    private JPanel panelRutas, panelReservas, panelRestaurantes, panelHistorial, panelChat;
    private JLabel lblTitulo, lblImagenPerfil;
    private JLabel lblRutas, lblReservas, lblRestaurantes, lblHistorial, lblChat;
    private JButton btnTema, btnSoporte;
    private Timer timerNotificaciones;
    private int ultimosMensajesRespondidos = 0;
    private int respuestasPrevias = 0;

    private Usuario usuario;

    public MenuPrincipal(Usuario usuario) {
        this.usuario = usuario;

        setTitle(I18n.t("titulo.menu.principal") + " - " + I18n.t("app.nombre"));
        setMinimumSize(new Dimension(900, 700));
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        iniciarNotificacionesSoporte(usuario);
        iniciarNotificacionesSoporteCliente(usuario);

        // Fondo con degradado
        panelFondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK) {
                    g.setColor(new Color(44, 62, 80));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(52, 152, 219), getWidth(), getHeight(),
                            new Color(44, 62, 80));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelFondo.setLayout(null);

        boolean dark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        Color colorBotonFondo = dark ? new Color(44, 62, 80) : new Color(52, 152, 219);

        final Color colorFijoBtnTema = colorBotonFondo;

        btnTema = UIUtils.crearBotonRedondeado(
                dark ? I18n.t("boton.modo.claro") : I18n.t("boton.modo.oscuro"),
                colorBotonFondo,
                18);
        btnTema.setBounds(20, 20, 130, 36);
        btnTema.setFont(new Font("Arial", Font.BOLD, 16));
        btnTema.setPreferredSize(new Dimension(130, 36));
        btnTema.setForeground(Color.WHITE);
        btnTema.setBackground(colorFijoBtnTema);
        btnTema.setText(dark ? I18n.t("boton.modo.claro") : I18n.t("boton.modo.oscuro"));

        // Elimina los MouseListener antiguos
        for (MouseListener ml : btnTema.getMouseListeners())
            btnTema.removeMouseListener(ml);

        btnTema.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Efecto hover: azul más oscuro para ambos temas
                btnTema.setBackground(new Color(41, 128, 185));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Vuelve al color de fondo según el tema actual
                boolean darkNow = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
                btnTema.setBackground(darkNow ? new Color(44, 62, 80) : new Color(52, 152, 219));
            }
        });

        btnTema.addActionListener(e -> {
            boolean esClaro = ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT;
            ThemeManager.setTheme(esClaro ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT, this);
            boolean darkNow = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
            Color nuevoColor = darkNow ? new Color(44, 62, 80) : new Color(52, 152, 219);
            btnTema.setText(darkNow ? I18n.t("boton.modo.claro") : I18n.t("boton.modo.oscuro"));
            btnTema.setBackground(nuevoColor);
            if (btnSoporte != null)
                btnSoporte.setBackground(nuevoColor);
            panelFondo.setBackground(nuevoColor);
            lblTitulo.setForeground(darkNow ? Color.WHITE : new Color(44, 62, 80));
            panelFondo.repaint();
        });
        panelFondo.add(btnTema);

        // Título
        lblTitulo = new JLabel(I18n.t("app.nombre"), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(dark ? Color.WHITE : new Color(44, 62, 80));
        lblTitulo.setBounds(150, 30, 400, 40);
        panelFondo.add(lblTitulo);

        // Imagen decorativa de fondo (por ejemplo, en la esquina inferior derecha)
        JLabel lblDecorativo = new JLabel();
        lblDecorativo.setOpaque(false);
        ImageIcon iconDecorativo = new ImageIcon("assets/LogoAsturTerra.png"); // Usa tu logo o imagen decorativa
        // Escalado responsivo según tamaño de ventana
        int anchoImg = Math.max(120, getWidth() / 6);
        int altoImg = anchoImg;
        Image imgEscalada = iconDecorativo.getImage().getScaledInstance(anchoImg, altoImg, Image.SCALE_SMOOTH);
        lblDecorativo.setIcon(new ImageIcon(imgEscalada));
        // Posición absoluta en la esquina inferior derecha
        lblDecorativo.setBounds(getWidth() - anchoImg - 30, getHeight() - altoImg - 80, anchoImg, altoImg);
        lblDecorativo.setVisible(true);
        panelFondo.add(lblDecorativo);

        // Actualiza la posición y tamaño de la imagen al redimensionar la ventana
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int anchoImg = Math.max(120, getWidth() / 6);
                int altoImg = anchoImg;
                Image imgEscalada = iconDecorativo.getImage().getScaledInstance(anchoImg, altoImg, Image.SCALE_SMOOTH);
                lblDecorativo.setIcon(new ImageIcon(imgEscalada));
                lblDecorativo.setBounds(getWidth() - anchoImg - 30, getHeight() - altoImg - 80, anchoImg, altoImg);
            }
        });

        // Imagen de perfil
        lblImagenPerfil = new JLabel();
        lblImagenPerfil.setBounds(600, 10, 80, 80);
        lblImagenPerfil.setBorder(BorderFactory.createEmptyBorder());
        cargarImagenPerfil();
        panelFondo.add(lblImagenPerfil);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuIdioma = new JMenu(I18n.t("menu.idioma"));
        JMenuItem itemEsp = new JMenuItem(I18n.t("menu.idioma.es"));
        JMenuItem itemEng = new JMenuItem(I18n.t("menu.idioma.en"));

        itemEsp.addActionListener(e -> {
            I18n.setLocale(new java.util.Locale("es"));
            recargarTextos();
            JOptionPane.showMessageDialog(this, "Idioma cambiado a Español");
        });

        itemEng.addActionListener(e -> {
            I18n.setLocale(new java.util.Locale("en"));
            recargarTextos();
            JOptionPane.showMessageDialog(this, "Language changed to English");
        });

        menuIdioma.add(itemEsp);
        menuIdioma.add(itemEng);
        menuBar.add(menuIdioma);

        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem itemPerfil = new JMenuItem(I18n.t("menu.perfil"));
        JMenuItem itemCerrarSesion = new JMenuItem(I18n.t("menu.cerrar.sesion"));
        menuPerfil.add(itemPerfil);
        menuPerfil.add(itemCerrarSesion);

        itemPerfil.addActionListener(e -> {
            new VentanaPerfil(this, usuario).setVisible(true);
            cargarImagenPerfil();
        });

        itemCerrarSesion.addActionListener(e -> {
            dispose();
            MenuPrincipal.instance = null;
            Login.usuarioActual = null;
            TurismoService.usuarioSesion = null;
            new Login().setVisible(true);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                MenuPrincipal.instance = null;
            }
        });

        lblImagenPerfil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblImagenPerfil.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    menuPerfil.show(lblImagenPerfil, evt.getX(), evt.getY());
                }
            }
        });

        // Paneles de menú y labels internos
        panelRutas = crearPanel(0, 0, I18n.t("titulo.rutas"), "assets/rutas.png");
        lblRutas = getPanelLabel(panelRutas);

        panelReservas = crearPanel(0, 0, I18n.t("titulo.reservas"), "assets/reserva.png");
        lblReservas = getPanelLabel(panelReservas);

        panelRestaurantes = crearPanel(0, 0, I18n.t("titulo.restaurantes"), "assets/restaurante.png");
        lblRestaurantes = getPanelLabel(panelRestaurantes);

        panelHistorial = crearPanel(0, 0, I18n.t("titulo.historial"), "assets/historial.png");
        lblHistorial = getPanelLabel(panelHistorial);

        panelChat = crearPanel(0, 0, I18n.t("boton.soporte"), "assets/chat.png");
        lblChat = getPanelLabel(panelChat);

        panelFondo.add(panelRutas);
        panelFondo.add(panelReservas);
        panelFondo.add(panelRestaurantes);
        panelFondo.add(panelHistorial);
        panelFondo.add(panelChat);

        // Botón Soporte (solo admin)
        if (usuario.getTipo().equalsIgnoreCase("admin")) {
            btnSoporte = UIUtils.crearBotonRedondeado(
                    I18n.t("boton.soporte"),
                    colorBotonFondo,
                    18);
            btnSoporte.setBounds(20, 65, 130, 36);
            btnSoporte.setFont(new Font("Arial", Font.BOLD, 16));
            btnSoporte.setForeground(Color.WHITE);
            btnSoporte.setBackground(colorFijoBtnTema);
            btnSoporte.setText(I18n.t("boton.soporte"));
            for (MouseListener ml : btnSoporte.getMouseListeners())
                btnSoporte.removeMouseListener(ml);
            // MouseListener que NO cambia el color en ningún modo
            btnSoporte.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnSoporte.setBackground(btnSoporte.getBackground());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnSoporte.setBackground(btnSoporte.getBackground());
                }
            });
            btnSoporte.addActionListener(e -> new VistaSoporteAdmin().setVisible(true));
            panelFondo.add(btnSoporte);
        }

        agregarEventos();

        getContentPane().add(panelFondo, BorderLayout.CENTER);
        setVisible(true);
        ThemeManager.setTheme(ThemeManager.getCurrentTheme(), this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarComponentes();
            }
        });
        ajustarComponentes();
    }

    // Recarga los textos de la interfaz tras cambiar el idioma
    private void recargarTextos() {
        setTitle(I18n.t("titulo.menu.principal") + " - " + I18n.t("app.nombre"));
        btnTema.setText(ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK
                ? I18n.t("boton.modo.claro")
                : I18n.t("boton.modo.oscuro"));
        if (btnSoporte != null)
            btnSoporte.setText(I18n.t("boton.soporte"));
        lblTitulo.setText(I18n.t("app.nombre"));
        if (lblRutas != null)
            lblRutas.setText(I18n.t("titulo.rutas"));
        if (lblReservas != null)
            lblReservas.setText(I18n.t("titulo.reservas"));
        if (lblRestaurantes != null)
            lblRestaurantes.setText(I18n.t("titulo.restaurantes"));
        if (lblHistorial != null)
            lblHistorial.setText(I18n.t("titulo.historial"));
        if (lblChat != null)
            lblChat.setText(I18n.t("boton.soporte"));
        repaint();
    }

    public static MenuPrincipal getInstance(Usuario usuario) {
        if (instance == null || instance.usuario == null || instance.usuario.getId() != usuario.getId()) {
            if (instance != null) {
                instance.dispose();
            }
            instance = new MenuPrincipal(usuario);
        }
        return instance;
    }

    private JPanel crearPanel(int x, int y, String texto, String rutaImagen) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 36, 36);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 36, 36);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBounds(x, y, 250, 130);
        panel.setBackground(
                ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? new Color(52, 73, 94)
                        : new Color(236, 240, 241));
        panel.setBorder(new ThemeManager.RoundedBorder(ThemeManager.COLOR_SECUNDARIO, 2, 18));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = new ImageIcon(rutaImagen);
        if (icon.getIconWidth() != -1) {
            JLabel lblImagen = new JLabel(icon);
            lblImagen.setBounds(80, 10, 100, 50);
            panel.add(lblImagen);
        }

        JLabel lblTexto = new JLabel(texto, SwingConstants.CENTER);
        lblTexto.setBounds(0, 80, 250, 30);
        lblTexto.setFont(new Font("Arial", Font.BOLD, 18));
        lblTexto.setForeground(new Color(44, 62, 80));
        panel.add(lblTexto);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK) {
                    panel.setBackground(new Color(41, 128, 185));
                    lblTexto.setForeground(Color.WHITE);
                } else {
                    panel.setBackground(new Color(52, 152, 219));
                    lblTexto.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK) {
                    panel.setBackground(new Color(52, 73, 94));
                    lblTexto.setForeground(new Color(236, 240, 241));
                } else {
                    panel.setBackground(new Color(236, 240, 241));
                    lblTexto.setForeground(new Color(44, 62, 80));
                }
            }
        });

        return panel;
    }

    // Devuelve el JLabel de texto principal de un panel
    private JLabel getPanelLabel(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel && ((JLabel) c).getFont().getSize() == 18) {
                return (JLabel) c;
            }
        }
        return null;
    }

    private void agregarEventos() {
        panelRutas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VistaRutas.getInstance(usuario).setVisible(true);
                dispose();
            }
        });

        panelReservas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VistaReservas.getInstance(usuario).setVisible(true);
                dispose();
            }
        });

        panelRestaurantes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VistaRestaurantes.getInstance(usuario).setVisible(true);
                dispose();
            }
        });

        panelHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new VistaHistorial(usuario).setVisible(true);
                dispose();
            }
        });

        panelChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirSoporte();
                new VistaChat(usuario).setVisible(true);
                dispose();
            }
        });
    }

    private void ajustarComponentes() {
        int w = panelFondo.getWidth();
        int h = panelFondo.getHeight();

        int panelWidth = 270;
        int panelHeight = 140;
        int sepX = (w - 2 * panelWidth) / 3;
        int sepY = 40;
        int top = h / 5;

        panelRutas.setBounds(sepX, top, panelWidth, panelHeight);
        panelReservas.setBounds(2 * sepX + panelWidth, top, panelWidth, panelHeight);

        panelRestaurantes.setBounds(sepX, top + panelHeight + sepY, panelWidth, panelHeight);
        panelHistorial.setBounds(2 * sepX + panelWidth, top + panelHeight + sepY, panelWidth, panelHeight);

        panelChat.setBounds(w / 2 - panelWidth / 2, top + 2 * (panelHeight + sepY), panelWidth, panelHeight);

        lblTitulo.setBounds(w / 2 - 200, 30, 400, 40);
        lblImagenPerfil.setBounds(w - 100, 10, 80, 80);
    }

    private void cargarImagenPerfil() {
        Image img;
        if (usuario.getImagenPerfil() != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(usuario.getImagenPerfil());
                BufferedImage bufferedImage = ImageIO.read(bais);
                img = bufferedImage.getScaledInstance(lblImagenPerfil.getWidth(), lblImagenPerfil.getHeight(),
                        Image.SCALE_SMOOTH);
            } catch (IOException e) {
                img = new ImageIcon("assets/LogoAsturTerra.png").getImage()
                        .getScaledInstance(lblImagenPerfil.getWidth(), lblImagenPerfil.getHeight(), Image.SCALE_SMOOTH);
            }
        } else {
            img = new ImageIcon("assets/LogoAsturTerra.png").getImage().getScaledInstance(lblImagenPerfil.getWidth(),
                    lblImagenPerfil.getHeight(), Image.SCALE_SMOOTH);
        }

        int size = Math.min(lblImagenPerfil.getWidth(), lblImagenPerfil.getHeight());
        BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(img, 0, 0, size, size, null);
        g2.dispose();
        lblImagenPerfil.setIcon(new ImageIcon(circleBuffer));
    }

    private void iniciarNotificacionesSoporte(Usuario usuario) {
        timerNotificaciones = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Mensaje> mensajes = TurismoService.getInstance().obtenerMensajesUsuario(usuario.getId());
                    int respondidos = 0;
                    for (Mensaje m : mensajes) {
                        if (m.getRespuesta() != null && !m.getRespuesta().isEmpty()) {
                            respondidos++;
                        }
                    }
                    if (respondidos > ultimosMensajesRespondidos) {
                        ultimosMensajesRespondidos = respondidos;
                        mostrarPanelNotificacion(I18n.t("notificacion.nueva.respuesta"));
                    }
                } catch (Exception ex) {
                    // Puedes registrar el error o ignorar si es temporal
                }
            }
        });
        timerNotificaciones.start();
    }

    private void iniciarNotificacionesSoporteCliente(Usuario usuario) {
        timerNotificaciones = new Timer(60000, e -> {
            try {
                List<Mensaje> mensajes = TurismoService.getInstance().obtenerMensajesUsuario(usuario.getId());
                int respondidos = 0;
                for (Mensaje m : mensajes) {
                    if (m.getRespuesta() != null && !m.getRespuesta().isEmpty()) {
                        respondidos++;
                    }
                }
                if (respondidos > respuestasPrevias) {
                    mostrarPanelNotificacion(I18n.t("notificacion.nueva.respuesta"));
                }
            } catch (Exception ex) {
                // Manejo de error opcional
            }
        });
        timerNotificaciones.start();
    }

    private void mostrarPanelNotificacion(String mensaje) {
        JLayeredPane layeredPane = getLayeredPane();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true));
        JLabel lbl = new JLabel(mensaje);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        panel.add(lbl);

        panel.setSize(panel.getPreferredSize());
        int x = getWidth() - panel.getWidth() - 30;
        int y = getHeight() - panel.getHeight() - 50;
        panel.setLocation(x, y);

        panel.setOpaque(true);
        panel.setVisible(true);

        layeredPane.add(panel, JLayeredPane.POPUP_LAYER);

        Timer timer = new Timer(4000, evt -> {
            layeredPane.remove(panel);
            layeredPane.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void abrirSoporte() {
        try {
            List<Mensaje> mensajes = TurismoService.getInstance().obtenerMensajesUsuario(usuario.getId());
            int respondidos = 0;
            for (Mensaje m : mensajes) {
                if (m.getRespuesta() != null && !m.getRespuesta().isEmpty()) {
                    respondidos++;
                }
            }
            respuestasPrevias = respondidos;
        } catch (Exception ex) {
            // Manejo de error opcional
        }
    }
}