package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import model.Ruta;
import model.Usuario;
import services.TurismoService;

public class VistaRutas extends JFrame {
    private static VistaRutas instance;

    private JTable tablaRutas;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEliminar, btnVerDetalles, btnValorar, btnReservar, btnVolver;
    private Usuario usuario;

    public static VistaRutas getInstance(Usuario usuario) {
        if (instance == null || !instance.isVisible()) {
            instance = new VistaRutas(usuario);
        }
        instance.toFront();
        return instance;
    }

    public VistaRutas(Usuario usuario) {
        this.usuario = usuario;
        inicializarComponentes();
        cargarRutas();
    }

    private void inicializarComponentes() {
        setTitle("Gestión de Rutas");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(44, 62, 80));
        JLabel lblTitulo = new JLabel("Gestión de Rutas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        ImageIcon iconoAct = new ImageIcon("assets/carga.png");

        JButton btnActualizar = new JButton(iconoAct);
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setPreferredSize(new Dimension(50,30));
        btnActualizar.addActionListener(e -> cargarRutas());
        panelTitulo.add(btnActualizar, BorderLayout.EAST);

        add(panelTitulo, BorderLayout.NORTH);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Descripción", "Precio", "Dificultad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRutas = new JTable(modeloTabla);
        tablaRutas.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollTabla = new JScrollPane(tablaRutas);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        btnAgregar = crearBoton("Agregar Ruta", new Color(52, 152, 219));
        btnEliminar = crearBoton("Eliminar Ruta", new Color(231, 76, 60));
        btnVerDetalles = crearBoton("Ver Detalles", new Color(46, 204, 113));
        btnValorar = crearBoton("Valorar Ruta", new Color(46, 204, 113));
        btnReservar = crearBoton("Reservar Ruta", new Color(46, 204, 113));
        btnVolver = crearBoton("Volver al Menú", new Color(52, 152, 219));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVerDetalles);
        panelBotones.add(btnValorar);
        panelBotones.add(btnReservar);
        panelBotones.add(btnVolver);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones de botones
        btnAgregar.addActionListener(e -> new AgregarRuta().setVisible(true));
        btnEliminar.addActionListener(e -> eliminarRuta());
        btnVerDetalles.addActionListener(e -> verDetallesRuta());
        btnValorar.addActionListener(e -> valorarRuta());
        btnReservar.addActionListener(e -> reservarRuta());
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
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        return btn;
    }

    private void cargarRutas() {
        modeloTabla.setRowCount(0);
        try {
            List<Ruta> rutas = TurismoService.getInstance().obtenerRutas();
            for (Ruta r : rutas) {
                modeloTabla.addRow(new Object[]{
                        r.getId(), r.getNombre(), r.getDescripcion(), r.getPrecio(), r.getDificultad()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar rutas: " + ex.getMessage());
        }
    }

    private void eliminarRuta() {
        int fila = tablaRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una ruta para eliminar.");
            return;
        }
        int idRuta = (int) modeloTabla.getValueAt(fila, 0);
        try {
            boolean eliminado = TurismoService.getInstance().eliminarRuta(idRuta);
            if (eliminado) {
                modeloTabla.removeRow(fila);
                JOptionPane.showMessageDialog(this, "Ruta eliminada.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la ruta.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void verDetallesRuta() {
        int fila = tablaRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una ruta.");
            return;
        }

        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        String descripcion = (String) modeloTabla.getValueAt(fila, 2);
        double precio = (double) modeloTabla.getValueAt(fila, 3);
        String dificultad = (String) modeloTabla.getValueAt(fila, 4);

        try {
            int idRuta = (int) modeloTabla.getValueAt(fila, 0);
            Ruta ruta = TurismoService.getInstance().obtenerRutaPorId(idRuta);

            String detalles = "Nombre: " + nombre +
                    "\nDescripción: " + descripcion +
                    "\nPrecio: " + precio +
                    "\nDificultad: " + dificultad;

            ImageIcon icono = null;

            if(ruta.getImagen() != null) {
                Image img = Toolkit.getDefaultToolkit().createImage(ruta.getImagen());
                icono = new ImageIcon(img.getScaledInstance(200,200,Image.SCALE_SMOOTH));
            }
            JLabel lblImagen = new JLabel(icono);
            lblImagen.setHorizontalAlignment(JLabel.CENTER);

            JOptionPane.showMessageDialog(this, detalles, "Detalles de la Ruta", JOptionPane.INFORMATION_MESSAGE, icono);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los detalles de la ruta: " + ex.getMessage());
        }
    }

    private void valorarRuta() {
        int fila = tablaRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una ruta para valorar.");
            return;
        }
        int idRuta = (int) modeloTabla.getValueAt(fila, 0);
        String nombreRuta = (String) modeloTabla.getValueAt(fila, 1);
        VistaValoraciones.getInstance(idRuta, nombreRuta).setVisible(true);
    }

    private void reservarRuta() {
        try {
            if (rutaSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "No hay ruta seleccionada.");
                return;
            }
    
            RutaDAO rutaDAO = new RutaDAO();
            Ruta ruta = rutaDAO.obtenerRutaPorNombre(rutaSeleccionada);
    
            if (ruta == null) {
                JOptionPane.showMessageDialog(this, "Ruta no encontrada.");
                return;
            }
    
            ReservarDAO dao = new ReservarDAO();
            boolean reservado = dao.reservarRuta(usuario.getId(), ruta.getId(), new Date());
    
            if (reservado) {
                TurismoService.getInstance().registrarActividad(usuario.getId(), "Reservó la ruta: " + ruta.getNombre());
                JOptionPane.showMessageDialog(this, "Reserva realizada con éxito.");
                cargarReservas();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo realizar la reserva.");
            }
    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reservar ruta: " + e.getMessage());
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }
}