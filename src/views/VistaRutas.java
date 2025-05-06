package views;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

import utils.I18n;
import utils.UIUtils;
import model.Ruta;
import model.Usuario;
import model.Reserva;
import services.TurismoService;
import dao.ReservarDAO;

public class VistaRutas extends JFrame {
    private static VistaRutas instance;

    private JPanel panelCuadricula;
    private JButton btnAgregar, btnVolver;
    private Usuario usuario;
    private JPanel panelFiltrosLateral;
    private boolean filtrosVisibles = false;
    private JTextField txtBuscarNombre = new JTextField(14);
    private JSlider sliderPrecioMax = new JSlider(0, 500, 500);
    private JLabel lblPrecioMax = new JLabel("Máximo: $500");

    public static VistaRutas getInstance(Usuario usuario) {
        if (instance == null || !instance.isVisible()) {
            instance = new VistaRutas(usuario);
        }
        instance.toFront();
        return instance;
    }

    public VistaRutas(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Gestión de Rutas");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        inicializarComponentes();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                instance = null;
            }
        });
        ThemeManager.setTheme(ThemeManager.getCurrentTheme(), this);
    }

    private void inicializarComponentes() {
        ThemeManager.Theme theme = ThemeManager.getCurrentTheme();
        Color bgMain = theme == ThemeManager.Theme.DARK ? new Color(34, 40, 49) : new Color(236, 240, 241);

        // Panel cabecera reducido y más simple
        JPanel panelCabecera = new JPanel(new BorderLayout());
        panelCabecera.setBackground(bgMain);

        JLabel lblTitulo = new JLabel(I18n.t("titulo.rutas"), SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Más pequeño
        lblTitulo.setForeground(theme == ThemeManager.Theme.DARK ? new Color(0x7ed6df) : new Color(44, 62, 80));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));

        JButton btnMostrarFiltros = new JButton("🔎 Filtros");
        btnMostrarFiltros.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMostrarFiltros.setBackground(new Color(52, 152, 219));
        btnMostrarFiltros.setForeground(Color.WHITE);
        btnMostrarFiltros.setFocusPainted(false);
        btnMostrarFiltros.setPreferredSize(new Dimension(100, 30));
        btnMostrarFiltros.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMostrarFiltros.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        btnMostrarFiltros.addActionListener(e -> togglePanelFiltros());

        panelCabecera.add(lblTitulo, BorderLayout.WEST);
        panelCabecera.add(btnMostrarFiltros, BorderLayout.EAST);

        // Panel principal sin paneles extra
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(bgMain);

        // Panel de filtros lateral
        panelFiltrosLateral = crearPanelFiltrosLateral();
        panelFiltrosLateral.setVisible(false);
        panelPrincipal.add(panelFiltrosLateral, BorderLayout.WEST);

        // Panel de cuadrícula para las tarjetas de rutas (sin panel extra)
        panelCuadricula = new JPanel();
        panelCuadricula.setLayout(new WrapLayout(FlowLayout.LEFT, 32, 32));
        panelCuadricula.setBackground(bgMain);

        JScrollPane scrollCuadricula = new JScrollPane(panelCuadricula);
        scrollCuadricula.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollCuadricula.getVerticalScrollBar().setUnitIncrement(16);
        scrollCuadricula.getViewport().setBackground(bgMain);
        scrollCuadricula.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panelPrincipal.add(scrollCuadricula, BorderLayout.CENTER);

        // Panel de botones inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(bgMain);

        btnAgregar = UIUtils.crearBotonRedondeado(I18n.t("boton.agregar"), new Color(52, 152, 219), 24);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.setPreferredSize(new Dimension(140, 40));
        ThemeManager.setComponentTheme(btnAgregar, theme);

        btnVolver = UIUtils.crearBotonRedondeado(I18n.t("boton.volver"), new Color(52, 152, 219), 24);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnVolver.setPreferredSize(new Dimension(140, 40));
        ThemeManager.setComponentTheme(btnVolver, theme);

        panelBotones.add(btnAgregar);
        panelBotones.add(btnVolver);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(panelCabecera, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> {
            AgregarRuta ventana = new AgregarRuta() {
                @Override
                public void dispose() {
                    super.dispose();
                    cargarRutas();
                }
            };
            ThemeManager.setTheme(ThemeManager.getCurrentTheme(), ventana);
            ventana.setVisible(true);
        });
        btnVolver.addActionListener(e -> {
            MenuPrincipal.getInstance(usuario).setVisible(true);
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                instance = null;
            }
        });
        setVisible(true);

        cargarRutas();
    }

    private JPanel crearPanelFiltrosLateral() {
        ThemeManager.Theme theme = ThemeManager.getCurrentTheme();
        Color bg = theme == ThemeManager.Theme.DARK ? new Color(44, 62, 80) : new Color(225, 232, 239);
        Color fg = theme == ThemeManager.Theme.DARK ? Color.WHITE : Color.BLACK;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(52, 152, 219)));
        panel.setPreferredSize(new Dimension(220, 0)); // Más estrecho

        JLabel lblFiltros = new JLabel("Filtros");
        lblFiltros.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFiltros.setForeground(fg);
        lblFiltros.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFiltros.setBorder(BorderFactory.createEmptyBorder(14, 0, 10, 0));
        panel.add(lblFiltros);

        JLabel lblBuscar = new JLabel("🔍 Nombre:");
        lblBuscar.setFont(new Font("Dialog", Font.PLAIN, 13));
        lblBuscar.setForeground(fg);
        lblBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblBuscar);

        txtBuscarNombre.setMaximumSize(new Dimension(180, 28));
        txtBuscarNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        panel.add(txtBuscarNombre);

        panel.add(Box.createVerticalStrut(10));

        JLabel lblSlider = new JLabel("💰 Precio máximo:");
        lblSlider.setFont(new Font("Dialog", Font.PLAIN, 13));
        lblSlider.setForeground(fg);
        lblSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSlider);

        sliderPrecioMax.setMajorTickSpacing(200);
        sliderPrecioMax.setMinorTickSpacing(50);
        sliderPrecioMax.setPaintTicks(true);
        sliderPrecioMax.setPaintLabels(true);
        sliderPrecioMax.setBackground(bg);
        sliderPrecioMax.setMaximumSize(new Dimension(180, 36));
        panel.add(sliderPrecioMax);

        lblPrecioMax.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrecioMax.setForeground(fg);
        lblPrecioMax.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblPrecioMax);

        sliderPrecioMax.addChangeListener(e -> lblPrecioMax.setText("Máximo: $" + sliderPrecioMax.getValue()));

        panel.add(Box.createVerticalStrut(12));

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFiltrar.setBackground(new Color(52, 152, 219));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFiltrar.setMaximumSize(new Dimension(120, 28));
        btnFiltrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFiltrar.addActionListener(e -> {
            cargarRutas();
            togglePanelFiltros();
        });
        panel.add(btnFiltrar);

        panel.add(Box.createVerticalStrut(8));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrar.setBackground(new Color(189, 195, 199));
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrar.setMaximumSize(new Dimension(120, 24));
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> togglePanelFiltros());
        panel.add(btnCerrar);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void togglePanelFiltros() {
        filtrosVisibles = !filtrosVisibles;
        panelFiltrosLateral.setVisible(filtrosVisibles);
        revalidate();
        repaint();
    }

    private void cargarRutas() {
        ThemeManager.Theme theme = ThemeManager.getCurrentTheme();
        Color bgPanel = theme == ThemeManager.Theme.DARK ? new Color(44, 62, 80) : new Color(245, 247, 250);

        panelCuadricula.removeAll();
        try {
            String filtroNombre = txtBuscarNombre.getText().trim().toLowerCase();
            int precioMax = sliderPrecioMax.getValue();

            List<Ruta> rutas = TurismoService.getInstance().obtenerRutas();
            for (Ruta r : rutas) {
                boolean coincide = true;
                if (!filtroNombre.isEmpty() && !r.getNombre().toLowerCase().contains(filtroNombre)) {
                    coincide = false;
                }
                if (r.getPrecio() > precioMax) {
                    coincide = false;
                }
                if (coincide) {
                    panelCuadricula.add(crearCuadroRuta(r, bgPanel));
                }
            }
            panelCuadricula.revalidate();
            panelCuadricula.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar rutas: " + ex.getMessage());
        }
    }

    private JPanel crearCuadroRuta(Ruta ruta, Color bgPanel) {
        boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;

        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fondo y borde RECTO (NO redondeado)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fondo = isDark ? new Color(44, 62, 80) : new Color(220, 230, 241);
                g2.setColor(fondo);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(52, 152, 219));
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(320, 420)); // Más pequeño
        panel.setBackground(isDark ? new Color(44, 62, 80) : new Color(220, 230, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setOpaque(false);

        // Imagen circular
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        lblImagen.setVerticalAlignment(JLabel.CENTER);
        lblImagen.setBounds(90, 20, 140, 140);
        try {
            BufferedImage img;
            if (ruta.getImagen() != null) {
                img = ImageIO.read(new ByteArrayInputStream(ruta.getImagen()));
            } else {
                img = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(new Color(220, 220, 220));
                g.fillOval(0, 0, 140, 140);
                g.setColor(new Color(160, 160, 160));
                g.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g.getFontMetrics();
                String texto = "Sin imagen";
                int x = (140 - fm.stringWidth(texto)) / 2;
                int y = (140 - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(texto, x, y);
                g.dispose();
            }
            if (img != null) {
                img = resizeImage(img, 140, 140);
            }
            int size = 140;
            BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleBuffer.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(img, 0, 0, size, size, null);
            g2.setClip(null);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(52, 152, 219));
            g2.drawOval(2, 2, size - 4, size - 4);
            g2.dispose();
            lblImagen.setIcon(new ImageIcon(circleBuffer));
        } catch (Exception ex) {
            // Si hay error, no se muestra imagen
        }
        panel.add(lblImagen);

        // Panel central con nombre, precio y dificultad (RECTO, NO redondeado, SIN
        // paintComponent)
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(true);
        panelCentral.setBackground(isDark ? new Color(36, 45, 60) : Color.WHITE);
        panelCentral.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
        panelCentral.setBounds(20, 180, 280, 100);

        JLabel lblNombre = new JLabel(ruta.getNombre());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 20));
        lblNombre.setForeground(new Color(44, 62, 80));
        lblNombre.setOpaque(false);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrecio = new JLabel("Precio: $" + ruta.getPrecio());
        lblPrecio.setFont(new Font("Arial", Font.PLAIN, 17));
        lblPrecio.setForeground(new Color(39, 174, 96));
        lblPrecio.setOpaque(false);
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDificultad = new JLabel("Dificultad: " + ruta.getDificultad());
        lblDificultad.setFont(new Font("Arial", Font.PLAIN, 17));
        lblDificultad.setForeground(new Color(243, 156, 18));
        lblDificultad.setOpaque(false);
        lblDificultad.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(lblNombre);
        panelCentral.add(Box.createVerticalStrut(6));
        panelCentral.add(lblPrecio);
        panelCentral.add(Box.createVerticalStrut(6));
        panelCentral.add(lblDificultad);

        panel.add(panelCentral);

        // Efecto hover y click para mostrar detalles
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(isDark ? new Color(60, 80, 110) : new Color(200, 220, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(isDark ? new Color(44, 62, 80) : new Color(220, 230, 241));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                verDetallesRutaCuadro(ruta);
            }
        });

        return panel;
    }

    private JButton crearBotonAccion(String texto, Color color, ActionListener listener) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.addActionListener(listener);
        return btn;
    }

    // Ventana de detalles mejorada y reservar directo
    private void verDetallesRutaCuadro(Ruta ruta) {
        try {
            ThemeManager.Theme theme = ThemeManager.getCurrentTheme();
            Color bgPanel = theme == ThemeManager.Theme.DARK ? new Color(44, 62, 80) : new Color(245, 247, 250);
            Color fgPanel = theme == ThemeManager.Theme.DARK ? Color.WHITE : new Color(44, 62, 80);

            JPanel panel = new JPanel(null);
            panel.setBackground(bgPanel);
            panel.setPreferredSize(new Dimension(520, 540));

            // Imagen circular grande arriba
            JLabel lblImagen = new JLabel();
            lblImagen.setBounds(170, 20, 180, 180);
            try {
                BufferedImage img;
                if (ruta.getImagen() != null) {
                    img = ImageIO.read(new ByteArrayInputStream(ruta.getImagen()));
                } else {
                    img = new BufferedImage(180, 180, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = img.createGraphics();
                    g.setColor(new Color(220, 220, 220));
                    g.fillOval(0, 0, 180, 180);
                    g.setColor(new Color(160, 160, 160));
                    g.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    FontMetrics fm = g.getFontMetrics();
                    String texto = "Sin imagen";
                    int x = (180 - fm.stringWidth(texto)) / 2;
                    int y = (180 - fm.getHeight()) / 2 + fm.getAscent();
                    g.drawString(texto, x, y);
                    g.dispose();
                }
                if (img != null) {
                    img = resizeImage(img, 180, 180);
                }
                int size = 180;
                BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = circleBuffer.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
                g2.drawImage(img, 0, 0, size, size, null);
                g2.setClip(null);
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(52, 152, 219));
                g2.drawOval(2, 2, size - 4, size - 4);
                g2.dispose();
                lblImagen.setIcon(new ImageIcon(circleBuffer));
            } catch (Exception ex) {
            }
            panel.add(lblImagen);

            JLabel lblNombre = new JLabel(ruta.getNombre());
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblNombre.setForeground(fgPanel);
            lblNombre.setBounds(0, 210, 520, 32);
            lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lblNombre);

            JLabel lblDescripcion = new JLabel(
                    "<html><div style='text-align:center;width:440px;'><b>Descripción:</b> " + ruta.getDescripcion()
                            + "</div></html>");
            lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblDescripcion
                    .setForeground(theme == ThemeManager.Theme.DARK ? new Color(0x7ed6df) : new Color(52, 73, 94));
            lblDescripcion.setBounds(40, 250, 440, 60);
            lblDescripcion.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lblDescripcion);

            JLabel lblPrecio = new JLabel("💲 $" + ruta.getPrecio());
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblPrecio.setForeground(new Color(39, 174, 96));
            lblPrecio.setBounds(70, 320, 120, 30);

            JLabel lblDificultad = new JLabel("⚡ " + ruta.getDificultad());
            lblDificultad.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblDificultad.setForeground(new Color(243, 156, 18));
            lblDificultad.setBounds(210, 320, 120, 30);

            JLabel lblId = new JLabel("ID: " + ruta.getId());
            lblId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblId.setForeground(new Color(127, 140, 141));
            lblId.setBounds(350, 320, 120, 30);

            panel.add(lblPrecio);
            panel.add(lblDificultad);
            panel.add(lblId);

            // Botones de acción en detalles (sin panel extra)
            int btnWidth = 110, btnHeight = 40;
            JButton btnEditar = crearBotonAccion(I18n.t("boton.editar"), new Color(241, 196, 15), e -> {
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null)
                    w.dispose();
                editarRuta(ruta);
            });
            JButton btnEliminar = crearBotonAccion(I18n.t("boton.eliminar"), new Color(231, 76, 60), e -> {
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null)
                    w.dispose();
                eliminarRuta(ruta);
            });
            JButton btnReservar = crearBotonAccion(I18n.t("boton.reservar"), new Color(46, 204, 113), e -> {
                reservarRutaDirecto(ruta);
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null)
                    w.dispose();
            });
            JButton btnValorar = crearBotonAccion(I18n.t("boton.valorar"), new Color(155, 89, 182), e -> {
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null)
                    w.dispose();
                valorarRuta(ruta);
            });
            JButton btnCerrar = crearBotonAccion("Cerrar", new Color(52, 152, 219), e -> {
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null)
                    w.dispose();
            });

            btnEditar.setBounds(30, 400, btnWidth, btnHeight);
            btnEliminar.setBounds(150, 400, btnWidth, btnHeight);
            btnReservar.setBounds(270, 400, btnWidth, btnHeight);
            btnValorar.setBounds(390, 400, btnWidth, btnHeight);
            btnCerrar.setBounds(200, 460, btnWidth, btnHeight);

            panel.add(btnEditar);
            panel.add(btnEliminar);
            panel.add(btnReservar);
            panel.add(btnValorar);
            panel.add(btnCerrar);

            JDialog dialog = new JDialog(this, "Detalles de la Ruta", true);
            dialog.setContentPane(panel);
            ThemeManager.setTheme(theme, dialog);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setResizable(false);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los detalles de la ruta: " + ex.getMessage());
        }
    }

    private void editarRuta(Ruta ruta) {
        EditarRuta ventana = new EditarRuta(ruta) {
            @Override
            public void dispose() {
                super.dispose();
                cargarRutas();
            }
        };
        ThemeManager.setTheme(ThemeManager.getCurrentTheme(), ventana);
        ventana.setVisible(true);
    }

    private void eliminarRuta(Ruta ruta) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar esta ruta?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                TurismoService.getInstance().eliminarRuta(ruta.getId());
                cargarRutas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la ruta: " + ex.getMessage());
            }
        }
    }

    // Reserva directa: crea la reserva como pendiente y abre la ventana de reservas
    private void reservarRutaDirecto(Ruta ruta) {
        try {
            ReservarDAO dao = new ReservarDAO();
            Reserva reserva = new Reserva(0, usuario.getId(), ruta.getId(), null, false);
            boolean creada = dao.reservarRuta(reserva);
            if (creada) {
                JOptionPane.showMessageDialog(this, "Reserva creada. Confírmala desde la ventana de reservas.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la reserva.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al reservar: " + ex.getMessage());
        }
        VistaReservas.getInstance(usuario).setVisible(true);
    }

    private void valorarRuta(Ruta ruta) {
        VistaValoraciones.getInstance(ruta.getId(), ruta.getNombre(), usuario).setVisible(true);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    // Layout flexible para cuadrícula
    class WrapLayout extends FlowLayout {
        public WrapLayout() {
            super();
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int x = 0, y = insets.top + vgap, rowHeight = 0;
                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible())
                        continue;
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if ((x == 0) || ((x + d.width) <= maxWidth)) {
                        if (x > 0)
                            x += hgap;
                        x += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    } else {
                        x = d.width;
                        y += vgap + rowHeight;
                        rowHeight = d.height;
                    }
                }
                y += rowHeight;
                y += insets.bottom;
                return new Dimension(targetWidth, y);
            }
        }
    }
}