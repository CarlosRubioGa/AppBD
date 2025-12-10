package sazonpos.vista;

import sazonpos.modelo.VentaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FrmReporteVentas extends JFrame {

    private JTextField txtFecha;
    private JButton btnHoy;
    private JButton btnBuscar;
    private JTable tblVentas;
    private DefaultTableModel modeloVentas;

    private JLabel lblTotalDia;
    private JLabel lblTotalEfectivo;
    private JLabel lblTotalTarjeta;
    private JLabel lblTotalTransfer;

    private VentaDAO ventaDAO;

    public FrmReporteVentas() {
        this.ventaDAO = new VentaDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Reporte de Ventas por Día - Sazón de Ali");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // =======================
        // PANEL SUPERIOR (Fecha)
        // =======================
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelTop.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFecha = new JTextField(10);
        panelTop.add(txtFecha);

        btnHoy = new JButton("Hoy");
        btnBuscar = new JButton("Buscar");
        panelTop.add(btnHoy);
        panelTop.add(btnBuscar);

        add(panelTop, BorderLayout.NORTH);

        // =======================
        // TABLA DE VENTAS
        // =======================
        modeloVentas = new DefaultTableModel(
                new Object[]{"ID", "Folio", "Fecha/Hora", "Usuario", "Forma Pago", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVentas = new JTable(modeloVentas);
        JScrollPane scroll = new JScrollPane(tblVentas);
        add(scroll, BorderLayout.CENTER);

        // =======================
        // PANEL INFERIOR (Totales)
        // =======================
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridLayout(2, 2, 5, 5));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        lblTotalDia = new JLabel("Total día: $0.00");
        lblTotalEfectivo = new JLabel("Efectivo: $0.00");
        lblTotalTarjeta = new JLabel("Tarjeta: $0.00");
        lblTotalTransfer = new JLabel("Transferencia: $0.00");

        panelBottom.add(lblTotalDia);
        panelBottom.add(lblTotalEfectivo);
        panelBottom.add(lblTotalTarjeta);
        panelBottom.add(lblTotalTransfer);

        add(panelBottom, BorderLayout.SOUTH);

        // =======================
        // EVENTOS
        // =======================
        btnHoy.addActionListener(e -> {
            txtFecha.setText(LocalDate.now().toString()); // yyyy-MM-dd
        });

        btnBuscar.addActionListener(e -> buscarVentas());
    }

    // =======================
    // LÓGICA DE BÚSQUEDA
    // =======================
    private void buscarVentas() {
        String fecha = txtFecha.getText().trim();
        if (fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la fecha (YYYY-MM-DD)", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpiar tabla
        modeloVentas.setRowCount(0);

        // Llenar tabla desde VentaDAO
        List<Object[]> filas = ventaDAO.listarPorFechaDetallado(fecha);
        for (Object[] f : filas) {
            modeloVentas.addRow(f);
        }

        // Totales generales
        BigDecimal totalDia = ventaDAO.obtenerTotalPorFecha(fecha);
        BigDecimal totalEfe = ventaDAO.obtenerTotalPorFechaYFormaPago(fecha, 1); // 1 = EFECTIVO
        BigDecimal totalTar = ventaDAO.obtenerTotalPorFechaYFormaPago(fecha, 2); // 2 = TARJETA
        BigDecimal totalTra = ventaDAO.obtenerTotalPorFechaYFormaPago(fecha, 3); // 3 = TRANSFERENCIA

        lblTotalDia.setText("Total día: $" + totalDia.toPlainString());
        lblTotalEfectivo.setText("Efectivo: $" + totalEfe.toPlainString());
        lblTotalTarjeta.setText("Tarjeta: $" + totalTar.toPlainString());
        lblTotalTransfer.setText("Transferencia: $" + totalTra.toPlainString());
    }

    // =======================
    // MAIN DE PRUEBA (OPCIONAL)
    // =======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmReporteVentas().setVisible(true);
        });
    }
}
