package views;

import model.Restaurante;
import services.TurismoService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Files;
import java.io.File;

public class AgregarRestaurante extends JFrame {
    private JTextField txtNombre, txtUbicacion;
    private JLabel lblImagen;
    private JButton btnGuardar, btnCancelar, btnImagen;
    private File imagenPerfil;
    private int valoracionSeleccionada = 0;
    private JLabel[] estrellas = new JLabel[5];

    public AgregarRestaurante() {
        setTitle("Nuevo Restaurante");
        setSize(540, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        ThemeManager.Theme theme = ThemeManager.getCurrentTheme();
        Color bgPanel = theme == ThemeManager.Theme.DARK ? new Color(44, 62, 80) : new Color(245, 247, 250);
        Color fgPanel = theme == ThemeManager.Theme.DARK ? Color.WHITE : new Color(44, 62, 80);
        Color borderColor = new Color(52, 152, 219);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(bgPanel);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Título
        JLabel lblTitulo = new JLabel("Nuevo Restaurante");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(fgPanel);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel central con GridBagLayout para mejor organización
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(bgPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Nombre
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        lblNombre.setForeground(fgPanel);
        panelCentral.add(lblNombre, gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 15));
        txtNombre.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        txtNombre.setForeground(fgPanel);
        txtNombre.setCaretColor(fgPanel);
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        txtNombre.setPreferredSize(new Dimension(220, 36));
        panelCentral.add(txtNombre, gbc);

        // Ubicación
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblUbicacion = new JLabel("Ubicación:");
        lblUbicacion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblUbicacion.setForeground(fgPanel);
        panelCentral.add(lblUbicacion, gbc);

        gbc.gridx = 1;
        txtUbicacion = new JTextField();
        txtUbicacion.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUbicacion.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        txtUbicacion.setForeground(fgPanel);
        txtUbicacion.setCaretColor(fgPanel);
        txtUbicacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        txtUbicacion.setPreferredSize(new Dimension(220, 36));
        panelCentral.add(txtUbicacion, gbc);

        // Imagen
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        lblImagen = new JLabel("Sin imagen", JLabel.CENTER);
        lblImagen.setPreferredSize(new Dimension(180, 120));
        lblImagen.setFont(new Font("Arial", Font.ITALIC, 14));
        lblImagen.setForeground(theme == ThemeManager.Theme.DARK ? new Color(120, 120, 120) : new Color(160, 160, 160));
        lblImagen.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        lblImagen.setOpaque(true);
        lblImagen.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        panelCentral.add(lblImagen, gbc);

        // Botón seleccionar imagen
        gbc.gridy++;
        btnImagen = new JButton("Seleccionar Imagen");
        btnImagen.setFont(new Font("Arial", Font.BOLD, 15));
        btnImagen.setPreferredSize(new Dimension(220, 44));
        btnImagen.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2, true));
        setButtonEffects(btnImagen, new Color(52, 152, 219), new Color(41, 128, 185), new Color(31, 97, 141));
        btnImagen.addActionListener(e -> seleccionarImagen());
        panelCentral.add(btnImagen, gbc);

        // Valoración (estrellas)
        gbc.gridy++;
        JPanel panelEstrellas = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelEstrellas.setOpaque(false);
        JLabel lblValoracion = new JLabel("Valoración inicial:");
        lblValoracion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblValoracion.setForeground(fgPanel);
        panelEstrellas.add(lblValoracion);

        for (int i = 0; i < 5; i++) {
            estrellas[i] = new JLabel("☆");
            estrellas[i].setFont(new Font("Segoe UI Symbol", Font.BOLD, 28));
            estrellas[i].setForeground(new Color(241, 196, 15));
            final int estrellaIndex = i;
            estrellas[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            estrellas[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    valoracionSeleccionada = estrellaIndex + 1;
                    actualizarEstrellas();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    resaltarEstrellas(estrellaIndex + 1);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    actualizarEstrellas();
                }
            });
            panelEstrellas.add(estrellas[i]);
        }
        panelCentral.add(panelEstrellas, gbc);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // Panel de botones abajo
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        panelBotones.setOpaque(false);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 18));
        btnGuardar.setPreferredSize(new Dimension(170, 48));
        btnGuardar.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 2, true));
        setButtonEffects(btnGuardar, new Color(46, 204, 113), new Color(39, 174, 96), new Color(30, 132, 73));
        btnGuardar.addActionListener(e -> agregarRestaurante());
        panelBotones.add(btnGuardar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancelar.setPreferredSize(new Dimension(170, 48));
        btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2, true));
        setButtonEffects(btnCancelar, new Color(231, 76, 60), new Color(192, 57, 43), new Color(155, 34, 38));
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);

        setVisible(true);
    }

    // Efectos visuales para los botones CRUD
    private void setButtonEffects(JButton btn, Color normal, Color hover, Color pressed) {
        btn.setBackground(normal);
        // Texto: blanco en oscuro, oscuro y negrita en claro
        boolean dark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        btn.setForeground(dark ? Color.WHITE : new Color(44, 62, 80));
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);

        // Elimina listeners previos para evitar duplicados
        for (MouseListener ml : btn.getMouseListeners()) {
            if (ml.getClass().getName().contains("MouseAdapter")) {
                btn.removeMouseListener(ml);
            }
        }

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(pressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                if (p.x >= 0 && p.x < btn.getWidth() && p.y >= 0 && p.y < btn.getHeight()) {
                    btn.setBackground(hover);
                } else {
                    btn.setBackground(normal);
                }
            }
        });
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            imagenPerfil = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(imagenPerfil.getAbsolutePath());
            lblImagen.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH)));
            lblImagen.setText("");
        }
    }

    private void actualizarEstrellas() {
        for (int i = 0; i < 5; i++) {
            estrellas[i].setText(i < valoracionSeleccionada ? "★" : "☆");
        }
    }

    private void resaltarEstrellas(int hasta) {
        for (int i = 0; i < 5; i++) {
            estrellas[i].setText(i < hasta ? "★" : "☆");
        }
    }

    private void agregarRestaurante() {
        try {
            String nombre = txtNombre.getText().trim();
            String ubicacion = txtUbicacion.getText().trim();

            if (nombre.isEmpty() || ubicacion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (valoracionSeleccionada == 0) {
                JOptionPane.showMessageDialog(this, "Selecciona una valoración inicial con estrellas.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (new dao.RestauranteDAO().obtenerRestaurantePorNombre(nombre) != null) {
                JOptionPane.showMessageDialog(this, "Ya existe un restaurante con ese nombre.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            byte[] imagenBytes = null;
            if (imagenPerfil != null) {
                imagenBytes = Files.readAllBytes(imagenPerfil.toPath());
            }

            Restaurante restaurante = new Restaurante(0, nombre, ubicacion, (float) valoracionSeleccionada,
                    imagenBytes);
            boolean guardado = TurismoService.getInstance().agregarRestaurante(restaurante);

            if (guardado) {
                Restaurante restInsertado = new dao.RestauranteDAO().obtenerRestaurantePorNombre(nombre);
                if (restInsertado != null) {
                    int idUsuario = 1;
                    dao.ValoracionDAO valoracionDAO = new dao.ValoracionDAO();
                    valoracionDAO.registrarValoracionRestaurante(
                            new model.ValoracionRestaurante(0, idUsuario, restInsertado.getId(), valoracionSeleccionada,
                                    "Valoración inicial"));
                }
                JOptionPane.showMessageDialog(this, "Restaurante agregado correctamente.");
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el restaurante.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar restaurante: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}