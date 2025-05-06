package views;

import java.awt.*;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.border.AbstractBorder;

public class ThemeManager {
    public static final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    public static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    public static final Color COLOR_EXITO = new Color(46, 204, 113);
    public static final Color COLOR_ERROR = new Color(231, 76, 60);
    public static final Font FUENTE_TITULO = new Font("Arial", Font.BOLD, 24);
    public static final Font FUENTE_NORMAL = new Font("Arial", Font.PLAIN, 15);

    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;

    public static void setTheme(Theme theme, Window rootWindow) {
        currentTheme = theme;
        try {
            if (theme == Theme.DARK) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                UIManager.put("control", new Color(44, 62, 80));
                UIManager.put("info", new Color(44, 62, 80));
                UIManager.put("nimbusBase", new Color(44, 62, 80));
                UIManager.put("nimbusBlueGrey", new Color(44, 62, 80));
                UIManager.put("nimbusLightBackground", new Color(52, 73, 94));
                UIManager.put("text", Color.WHITE);
                UIManager.put("nimbusFocus", new Color(52, 152, 219));
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UIManager.put("control", Color.WHITE);
                UIManager.put("info", Color.WHITE);
                UIManager.put("nimbusBase", new Color(52, 152, 219));
                UIManager.put("nimbusBlueGrey", new Color(236, 240, 241));
                UIManager.put("nimbusLightBackground", Color.WHITE);
                UIManager.put("text", Color.BLACK);
                UIManager.put("nimbusFocus", new Color(52, 152, 219));
            }
        } catch (Exception ignored) {
        }

        for (Window window : Window.getWindows()) {
            setComponentTheme(window, theme);
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    public static class RoundedButton extends JButton {
        private final int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
            g2.dispose();
        }
    }

    // Borde redondeado personalizado
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2,
                    width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = thickness;
            return insets;
        }
    }

        public static void setComponentTheme(Component comp, Theme theme) {
        // COLORES CONSISTENTES EN TODAS LAS VENTANAS
        // --- PALETA CLARO ---
        final Color LIGHT_BG = new Color(236, 240, 241);
        final Color LIGHT_PANEL = Color.WHITE;
        final Color LIGHT_BORDER = new Color(44, 62, 80);
        final Color LIGHT_TEXT = new Color(44, 62, 80);
        final Color LIGHT_BUTTON = new Color(52, 152, 219);
        final Color LIGHT_BUTTON_TEXT = Color.WHITE;
        // --- PALETA OSCURO ---
        final Color DARK_BG = new Color(44, 62, 80);
        final Color DARK_PANEL = new Color(52, 73, 94);
        final Color DARK_BORDER = new Color(33, 47, 60);
        final Color DARK_TEXT = Color.WHITE;
        final Color DARK_BUTTON = new Color(52, 152, 219);
        final Color DARK_BUTTON_TEXT = Color.WHITE;
    
        if (comp instanceof JPanel) {
            if (theme == Theme.DARK) {
                comp.setBackground(DARK_PANEL);
            } else {
                comp.setBackground(LIGHT_PANEL);
            }
            // Borde recto y color consistente
            Border border = BorderFactory.createLineBorder(theme == Theme.DARK ? DARK_BORDER : LIGHT_BORDER, 2);
            ((JPanel) comp).setBorder(border);
        }
        if (comp instanceof JLabel) {
            if (theme == Theme.DARK) {
                ((JLabel) comp).setForeground(DARK_TEXT);
            } else {
                ((JLabel) comp).setForeground(LIGHT_TEXT);
            }
        }
        if (comp instanceof JSlider) {
            JSlider slider = (JSlider) comp;
            if (theme == Theme.DARK) {
                slider.setBackground(DARK_PANEL);
                slider.setForeground(DARK_TEXT);
                slider.setOpaque(true);
                UIManager.put("Slider.trackColor", DARK_BG);
                UIManager.put("Slider.thumb", new Color(189, 195, 199));
                UIManager.put("Slider.tickColor", DARK_TEXT);
            } else {
                slider.setBackground(LIGHT_PANEL);
                slider.setForeground(LIGHT_TEXT);
                slider.setOpaque(true);
                UIManager.put("Slider.trackColor", new Color(189, 195, 199));
                UIManager.put("Slider.thumb", LIGHT_BUTTON);
                UIManager.put("Slider.tickColor", LIGHT_TEXT);
            }
            SwingUtilities.updateComponentTreeUI(slider);
        }
        if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            if (theme == Theme.DARK) {
                table.setBackground(DARK_PANEL);
                table.setForeground(DARK_TEXT);
                table.setSelectionBackground(DARK_BORDER);
                table.setSelectionForeground(DARK_TEXT);
                table.setGridColor(DARK_BG);
            } else {
                table.setBackground(LIGHT_PANEL);
                table.setForeground(LIGHT_TEXT);
                table.setSelectionBackground(LIGHT_BUTTON);
                table.setSelectionForeground(LIGHT_BUTTON_TEXT);
                table.setGridColor(LIGHT_BG);
            }
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                if (theme == Theme.DARK) {
                    header.setBackground(DARK_BG);
                    header.setForeground(DARK_TEXT);
                } else {
                    header.setBackground(LIGHT_BG);
                    header.setForeground(LIGHT_TEXT);
                }
            }
        }
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            Color colorBase = theme == Theme.DARK ? DARK_BUTTON : LIGHT_BUTTON;
            Color normalBg = colorBase;
            Color hoverBg = colorBase.brighter();
            Color pressedBg = colorBase.darker();
    
            // Borde redondeado solo para botones
            Border rounded = new RoundedBorder(
                    colorBase,
                    3, 24 // grosor 3, radio 24
            );
            btn.setBorder(rounded);
    
            btn.setBackground(normalBg);
            btn.setForeground(theme == Theme.DARK ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT);
            btn.setFont(new Font("Dialog", Font.BOLD, 18));
            btn.setMargin(new Insets(12, 24, 12, 24));
            btn.setContentAreaFilled(true);
            btn.setOpaque(true);
    
            for (MouseListener ml : btn.getMouseListeners()) {
                btn.removeMouseListener(ml);
            }
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(hoverBg);
                    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btn.repaint();
                }
    
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(normalBg);
                    btn.setCursor(Cursor.getDefaultCursor());
                    btn.repaint();
                }
    
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    btn.setBackground(pressedBg);
                    btn.repaint();
                }
    
                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    Point p = e.getPoint();
                    if (p.x >= 0 && p.x < btn.getWidth() && p.y >= 0 && p.y < btn.getHeight()) {
                        btn.setBackground(hoverBg);
                    } else {
                        btn.setBackground(normalBg);
                    }
                    btn.repaint();
                }
            });
        }
        if (comp instanceof JTextField || comp instanceof JTextArea || comp instanceof JPasswordField) {
            comp.setBackground(theme == Theme.DARK ? DARK_BG : LIGHT_PANEL);
            comp.setForeground(theme == Theme.DARK ? DARK_TEXT : LIGHT_TEXT);
            if (comp instanceof JTextComponent) {
                ((JTextComponent) comp).setCaretColor(theme == Theme.DARK ? DARK_TEXT : LIGHT_TEXT);
            }
        }
        if (comp instanceof JScrollPane) {
            comp.setBackground(theme == Theme.DARK ? DARK_BG : LIGHT_PANEL);
            comp.setForeground(theme == Theme.DARK ? DARK_TEXT : LIGHT_TEXT);
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                setComponentTheme(child, theme);
            }
        }
    }

    // Método para actualizar el color y hover de todos los botones
    public static void actualizarColoresBotones(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                actualizarBotonHover((JButton) comp);
            } else if (comp instanceof Container) {
                actualizarColoresBotones((Container) comp);
            }
        }
    }

    // Método para actualizar el color base y hover de un botón según el tema
    public static void actualizarBotonHover(JButton btn) {
        Color colorBase = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK
                ? new Color(44, 62, 80)
                : new Color(52, 152, 219);
        Color colorHover = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK
                ? new Color(52, 152, 219)
                : new Color(41, 128, 185);
        btn.setBackground(colorBase);
        btn.setForeground(Color.WHITE);

        // Elimina listeners previos para evitar duplicados
        for (MouseListener ml : btn.getMouseListeners()) {
            btn.removeMouseListener(ml);
        }
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorHover);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorBase);
            }
        });
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }
}
