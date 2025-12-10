package sazonpos.vista;

import sazonpos.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class FrmMenuPrincipal extends JFrame {

    private Usuario usuarioActual;
    private JButton btnPuntoVenta;
    private JButton btnReporteVentas;
    private JButton btnInventario;
    private JButton btnSalir;

    public FrmMenuPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initComponents();
    }

    private void initComponents() {
        setTitle("Menú Principal - Sazón de Ali");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblBienvenida = new JLabel(
                "Bienvenido: " + usuarioActual.getNombre() + " (" + usuarioActual.getRol() + ")",
                SwingConstants.CENTER
        );
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(lblBienvenida, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnPuntoVenta = new JButton("Punto de Venta");
        btnReporteVentas = new JButton("Reporte de Dinero Generado");
        btnInventario = new JButton("Inventario / Productos");
        btnSalir = new JButton("Salir");

        panelBotones.add(btnPuntoVenta);
        panelBotones.add(btnReporteVentas);
        panelBotones.add(btnInventario);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.CENTER);

        // Lógica de permisos: solo ADMIN puede ver administración
        if (!"ADMIN".equalsIgnoreCase(usuarioActual.getRol())) {
            btnReporteVentas.setEnabled(false);
            btnInventario.setEnabled(false);
        }

        // Eventos
        btnPuntoVenta.addActionListener(e -> {
    FrmPuntoDeVenta pv = new FrmPuntoDeVenta();  // 👈 sin parámetros
    pv.setVisible(true);
        });

        btnReporteVentas.addActionListener(e -> {
            FrmReporteVentas rv = new FrmReporteVentas();
            rv.setVisible(true);
        });

        btnInventario.addActionListener(e -> {
            FrmInventarioAdmin inv = new FrmInventarioAdmin();
            inv.setVisible(true);
        });

        btnSalir.addActionListener(e -> {
            dispose(); // cierra el menú
        });
    }
}
