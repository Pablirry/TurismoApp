package views;

import model.Ruta;
import model.Usuario;
import services.TurismoService;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.io.File;

public class EditarRuta extends JDialog {
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtPrecio;
    private JComboBox<String> cmbDificultad;
    private JLabel lblImagen;
    private JButton btnGuardar, btnCancelar, btnImagen;
    private File imagenRuta;
    private Ruta rutaOriginal;
    private Usuario usuario;

    public EditarRuta(Ruta ruta) {
        super((Frame) null, "Editar Ruta", true);
        this.rutaOriginal = ruta;
        setSize(500, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // Tema y colores
        ThemeManager.Theme theme = ThemeManager.getCurrentTheme();
        Color bgPanel = theme == ThemeManager.Theme.DARK ? new Color(44, 62, 80) : new Color(245, 247, 250);
        Color fgPanel = theme == ThemeManager.Theme.DARK ? Color.WHITE : new Color(44, 62, 80);
        Color borderColor = new Color(52, 152, 219);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgPanel);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Título
        JLabel lblTitulo = new JLabel("Editar Ruta");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(fgPanel);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(18));

        // Campos
        JPanel panelCampos = new JPanel();
        panelCampos.setOpaque(false);
        panelCampos.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Nombre
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        lblNombre.setForeground(fgPanel);
        panelCampos.add(lblNombre, gbc);

        gbc.gridy++;
        txtNombre = new JTextField(ruta.getNombre());
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 15));
        txtNombre.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        txtNombre.setForeground(fgPanel);
        txtNombre.setCaretColor(fgPanel);
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        txtNombre.setPreferredSize(new Dimension(350, 36));
        panelCampos.add(txtNombre, gbc);

        // Descripción
        gbc.gridy++;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDescripcion.setForeground(fgPanel);
        panelCampos.add(lblDescripcion, gbc);

        gbc.gridy++;
        txtDescripcion = new JTextArea(ruta.getDescripcion(), 3, 20);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 15));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        txtDescripcion.setForeground(fgPanel);
        txtDescripcion.setCaretColor(fgPanel);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(350, 60));
        panelCampos.add(scrollDesc, gbc);

        // Precio
        gbc.gridy++;
        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPrecio.setForeground(fgPanel);
        panelCampos.add(lblPrecio, gbc);

        gbc.gridy++;
        txtPrecio = new JTextField(String.valueOf(ruta.getPrecio()));
        txtPrecio.setFont(new Font("Arial", Font.PLAIN, 15));
        txtPrecio.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        txtPrecio.setForeground(fgPanel);
        txtPrecio.setCaretColor(fgPanel);
        txtPrecio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        txtPrecio.setPreferredSize(new Dimension(120, 36));
        panelCampos.add(txtPrecio, gbc);

        // Dificultad
        gbc.gridy++;
        JLabel lblDificultad = new JLabel("Dificultad:");
        lblDificultad.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDificultad.setForeground(fgPanel);
        panelCampos.add(lblDificultad, gbc);

        gbc.gridy++;
        cmbDificultad = new JComboBox<>(new String[] { "Fácil", "Media", "Difícil" });
        cmbDificultad.setSelectedItem(ruta.getDificultad());
        cmbDificultad.setFont(new Font("Arial", Font.PLAIN, 15));
        cmbDificultad.setBackground(theme == ThemeManager.Theme.DARK ? new Color(52, 73, 94) : Color.WHITE);
        cmbDificultad.setForeground(fgPanel);
        cmbDificultad.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        cmbDificultad.setPreferredSize(new Dimension(180, 36));
        panelCampos.add(cmbDificultad, gbc);

        // Imagen
        gbc.gridy++;
        lblImagen = new JLabel("Sin imagen", JLabel.CENTER);
        lblImagen.setPreferredSize(new Dimension(180, 120));
        lblImagen.setFont(new Font("Arial", Font.ITALIC, 14));
        lblImagen.setForeground(theme == ThemeManager.Theme.DARK ? new Color(120, 120, 120) : new Color(160, 160, 160));
        lblImagen.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        if (ruta.getImagen() != null) {
            ImageIcon icon = new ImageIcon(ruta.getImagen());
            lblImagen.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH)));
            lblImagen.setText("");
        }
        panelCampos.add(lblImagen, gbc);

        gbc.gridy++;
        btnImagen = new JButton("Cambiar Imagen");
        btnImagen.setBackground(borderColor);
        btnImagen.setForeground(Color.WHITE);
        btnImagen.setFocusPainted(false);
        btnImagen.setFont(new Font("Arial", Font.BOLD, 14));
        btnImagen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImagen.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imagenRuta = fc.getSelectedFile();
                ImageIcon icon = new ImageIcon(imagenRuta.getAbsolutePath());
                lblImagen.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH)));
                lblImagen.setText("");
            }
        });
        panelCampos.add(btnImagen, gbc);

        panel.add(panelCampos);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 15));
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 15));
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarCambios() {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String precioStr = txtPrecio.getText().trim();
            String dificultad = (String) cmbDificultad.getSelectedItem();

            if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty() || dificultad == null) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            double precio;
            try {
                precio = Double.parseDouble(precioStr);
                if (precio < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            byte[] imagenBytes = rutaOriginal.getImagen();
            if (imagenRuta != null) {
                imagenBytes = Files.readAllBytes(imagenRuta.toPath());
            }

            Ruta rutaEditada = new Ruta(
                    rutaOriginal.getId(),
                    nombre,
                    descripcion,
                    imagenBytes,
                    precio,
                    dificultad);

            boolean actualizado = TurismoService.getInstance().actualizarRuta(rutaEditada);
            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Ruta actualizada correctamente.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la ruta.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar ruta: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        ThemeManager.setTheme(ThemeManager.getCurrentTheme(), VistaRutas.getInstance(this.usuario));
    }
}