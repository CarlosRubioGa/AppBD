package sazonpos.vista;

import sazonpos.modelo.Producto;
import sazonpos.modelo.ProductoDAO;
import sazonpos.modelo.Usuario;
import sazonpos.modelo.VentaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FrmPuntoDeVenta extends JFrame {

    // Botones de categorías
    private JButton btnRefresco;
    private JButton btnGomitas;
    private JButton btnComida;
    private JButton btnPapas;

    // Tablas
    private JTable tblProductos;
    private JTable tblTicket;

    // Controles de cantidad / ticket
    private JButton btnAgregar;
    private JTextField txtCantidad;

    // Controles de pago
    private JLabel lblTotal;
    private JTextField txtPagaCon;
    private JLabel lblCambio;

    private JRadioButton rbEfectivo;
    private JRadioButton rbTarjeta;
    private JTextField txtReferenciaTarjeta;
    private JButton btnPagar;

    // DAOs
    private ProductoDAO productoDAO;
    private VentaDAO ventaDAO;

    // Usuario que realiza la venta
    private Usuario usuario;

    // Categoría actual mostrada (1=BEbida, 2=Papas, 3=Dulces, 4=Comida)
    private int categoriaActual = 1;

    // Modelos de tabla
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloTicket;

    // ============================
    // CONSTRUCTORES
    // ============================

    // Usado desde el menú principal (con usuario logueado)
    public FrmPuntoDeVenta(Usuario usuario) {
        this.usuario = usuario;
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
        initComponents();
        cargarProductosPorCategoria(1); // por defecto: BEBIDA
    }

    // Para pruebas suelto (sin usuario)
    public FrmPuntoDeVenta() {
        this(null);
    }

    // ============================
    // INICIALIZACIÓN DE COMPONENTES
    // ============================
    private void initComponents() {
        setTitle("Punto de Venta - Sazón de Ali");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // ---------- Título ----------
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitulo = new JLabel("Punto de Venta - Sazón de Ali");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // ---------- Panel de categorías ----------
        JPanel panelCategorias = new JPanel(new GridLayout(4, 1, 5, 5));
        panelCategorias.setBorder(BorderFactory.createTitledBorder("Tipo de producto"));

        btnRefresco = crearBotonConIcono("Refrescos", "recursos/refresco.png");
        btnGomitas  = crearBotonConIcono("Gomitas",   "recursos/gomitas.png");
        btnComida   = crearBotonConIcono("Comida",    "recursos/comida.png");
        btnPapas    = crearBotonConIcono("Papas",     "recursos/papas.png");

        btnRefresco.addActionListener(e -> cargarProductosPorCategoria(1)); // BEBIDA
        btnPapas.addActionListener(e -> cargarProductosPorCategoria(2));    // PAPAS
        btnGomitas.addActionListener(e -> cargarProductosPorCategoria(3));  // DULCES
        btnComida.addActionListener(e -> cargarProductosPorCategoria(4));   // COMIDA_PREPARADA

        panelCategorias.add(btnRefresco);
        panelCategorias.add(btnGomitas);
        panelCategorias.add(btnComida);
        panelCategorias.add(btnPapas);

        add(panelCategorias, BorderLayout.WEST);

        // ---------- Panel central (productos) ----------
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBorder(BorderFactory.createTitledBorder("Productos"));

        modeloProductos = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Categoría", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProductos = new JTable(modeloProductos);
        JScrollPane scrollProductos = new JScrollPane(tblProductos);
        panelCentro.add(scrollProductos, BorderLayout.CENTER);

        // Ocultar ID, Categoría y Stock (solo se verá Nombre y Precio)
        ocultarColumnasProductos();

        // Panel inferior de productos (cantidad + botón agregar)
        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAgregar.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField("1", 5);
        panelAgregar.add(txtCantidad);

        btnAgregar = new JButton("Agregar al ticket");
        btnAgregar.addActionListener(e -> agregarProductoAlTicket());
        panelAgregar.add(btnAgregar);

        panelCentro.add(panelAgregar, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        // ---------- Panel derecho (ticket) ----------
        JPanel panelDerecha = new JPanel(new BorderLayout(5, 5));
        panelDerecha.setBorder(BorderFactory.createTitledBorder("Ticket"));

        modeloTicket = new DefaultTableModel(
                new Object[]{"ID Prod", "Producto", "Cant", "P. Unit", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTicket = new JTable(modeloTicket);
        JScrollPane scrollTicket = new JScrollPane(tblTicket);
        panelDerecha.add(scrollTicket, BorderLayout.CENTER);

        // ---------- Panel de pago ----------
        JPanel panelPago = new JPanel();
        panelPago.setLayout(new BoxLayout(panelPago, BoxLayout.Y_AXIS));
        panelPago.setBorder(BorderFactory.createTitledBorder("Pago"));

        // Total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(new JLabel("Total: $"));
        lblTotal = new JLabel("0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelTotal.add(lblTotal);
        panelPago.add(panelTotal);

        // Tipo de pago
        JPanel panelTipoPago = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbEfectivo = new JRadioButton("Efectivo");
        rbTarjeta = new JRadioButton("Tarjeta");
        ButtonGroup grupoPago = new ButtonGroup();
        grupoPago.add(rbEfectivo);
        grupoPago.add(rbTarjeta);
        rbEfectivo.setSelected(true);

        panelTipoPago.add(new JLabel("Forma de pago:"));
        panelTipoPago.add(rbEfectivo);
        panelTipoPago.add(rbTarjeta);
        panelPago.add(panelTipoPago);

        // Pago en efectivo
        JPanel panelEfectivo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEfectivo.add(new JLabel("Paga con: $"));
        txtPagaCon = new JTextField(6);
        panelEfectivo.add(txtPagaCon);
        panelEfectivo.add(new JLabel("Cambio: $"));
        lblCambio = new JLabel("0.00");
        panelEfectivo.add(lblCambio);
        panelPago.add(panelEfectivo);

        // Pago con tarjeta
        JPanel panelTarjeta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTarjeta.add(new JLabel("Referencia tarjeta:"));
        txtReferenciaTarjeta = new JTextField(10);
        panelTarjeta.add(txtReferenciaTarjeta);
        panelPago.add(panelTarjeta);

        // Botón pagar
        JPanel panelBotonPagar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPagar = new JButton("Confirmar pago");
        btnPagar.addActionListener(e -> procesarPago());
        panelBotonPagar.add(btnPagar);
        panelPago.add(panelBotonPagar);

        panelDerecha.add(panelPago, BorderLayout.SOUTH);

        add(panelDerecha, BorderLayout.EAST);

        // Comportamiento al cambiar tipo de pago
        rbEfectivo.addActionListener(e -> {
            txtPagaCon.setEnabled(true);
            txtReferenciaTarjeta.setEnabled(false);
        });
        rbTarjeta.addActionListener(e -> {
            txtPagaCon.setEnabled(false);
            txtReferenciaTarjeta.setEnabled(true);
        });
        txtReferenciaTarjeta.setEnabled(false);
    }

    // ============================
    // Ocultar columnas de la tabla de productos
    // ============================
    private void ocultarColumnasProductos() {
        // ID
        tblProductos.getColumnModel().getColumn(0).setMinWidth(0);
        tblProductos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblProductos.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Categoría
        tblProductos.getColumnModel().getColumn(2).setMinWidth(0);
        tblProductos.getColumnModel().getColumn(2).setMaxWidth(0);
        tblProductos.getColumnModel().getColumn(2).setPreferredWidth(0);

        // Stock
        tblProductos.getColumnModel().getColumn(4).setMinWidth(0);
        tblProductos.getColumnModel().getColumn(4).setMaxWidth(0);
        tblProductos.getColumnModel().getColumn(4).setPreferredWidth(0);
    }

    // ============================
    // Botón de categoría con icono
    // ============================
    private JButton crearBotonConIcono(String texto, String rutaImagen) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getClassLoader().getResource(rutaImagen));
            Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + rutaImagen);
        }

        JLabel lblIcono = new JLabel(icon);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setHorizontalAlignment(SwingConstants.CENTER);

        btn.add(lblIcono, BorderLayout.CENTER);
        btn.add(lblTexto, BorderLayout.SOUTH);

        return btn;
    }

    // ============================
    // Cargar productos por categoría
    // ============================
    private void cargarProductosPorCategoria(int idCategoria) {
        this.categoriaActual = idCategoria;  // guardamos la categoría para recargar después
        modeloProductos.setRowCount(0);
        List<Producto> productos = productoDAO.listarTodos();  // o filtrar en el SQL

        for (Producto p : productos) {
            if (p.getIdCategoria() == idCategoria) {
                modeloProductos.addRow(new Object[]{
                        p.getIdProducto(),
                        p.getNombre(),
                        p.getNombreCategoria(),
                        p.getPrecioVenta(),
                        p.getStockActual()
                });
            }
        }
    }

    // ============================
    // Agregar producto al ticket
    // ============================
    private void agregarProductoAlTicket() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idProd = Integer.parseInt(modeloProductos.getValueAt(fila, 0).toString());
            String nombre = modeloProductos.getValueAt(fila, 1).toString();
            BigDecimal precio = new BigDecimal(modeloProductos.getValueAt(fila, 3).toString());

            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));

            modeloTicket.addRow(new Object[]{
                    idProd,
                    nombre,
                    cantidad,
                    precio,
                    subtotal
            });

            recalcularTotal();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad no válida", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recalcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modeloTicket.getRowCount(); i++) {
            BigDecimal subtotal = new BigDecimal(modeloTicket.getValueAt(i, 4).toString());
            total = total.add(subtotal);
        }
        lblTotal.setText(total.toPlainString());
    }

    // ============================
    // Procesar pago (REGISTRA VENTA + DESCUENTA STOCK)
    // ============================
    private void procesarPago() {
        if (modeloTicket.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en el ticket", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal total = new BigDecimal(lblTotal.getText());

        // Armamos la lista de detalles a partir de la tabla del ticket
        List<Object[]> detalles = new ArrayList<>();
        for (int i = 0; i < modeloTicket.getRowCount(); i++) {
            int idProd = (int) modeloTicket.getValueAt(i, 0); // col 0 = ID producto
            int cant   = Integer.parseInt(modeloTicket.getValueAt(i, 2).toString());
            BigDecimal precioUnit = new BigDecimal(modeloTicket.getValueAt(i, 3).toString());
            BigDecimal subtotal   = new BigDecimal(modeloTicket.getValueAt(i, 4).toString());

            detalles.add(new Object[]{idProd, cant, precioUnit, subtotal});
        }

        // id del usuario que está logueado
        int idUsuario = (usuario != null) ? usuario.getIdUsuario() : 1;

        if (rbEfectivo.isSelected()) {
            try {
                BigDecimal pagaCon = new BigDecimal(txtPagaCon.getText().trim());
                if (pagaCon.compareTo(total) < 0) {
                    JOptionPane.showMessageDialog(this, "El pago en efectivo es menor al total", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal cambio = pagaCon.subtract(total);
                lblCambio.setText(cambio.toPlainString());

                boolean ok = ventaDAO.registrarVenta(
                        idUsuario,
                        total,
                        1,              // 1 = EFECTIVO
                        null,           // sin referencia
                        detalles
                );

                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Pago en efectivo registrado.\nCambio: $" + cambio,
                            "Venta registrada",
                            JOptionPane.INFORMATION_MESSAGE);
                    limpiarTicket();
                    cargarProductosPorCategoria(categoriaActual); // recarga stock
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al registrar la venta en la base de datos",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad de 'Paga con' no válida", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (rbTarjeta.isSelected()) {
            String ref = txtReferenciaTarjeta.getText().trim();
            if (ref.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa la referencia de la tarjeta", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = ventaDAO.registrarVenta(
                    idUsuario,
                    total,
                    2,          // 2 = TARJETA
                    ref,
                    detalles
            );

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Pago con tarjeta registrado.\nReferencia: " + ref,
                        "Venta registrada",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarTicket();
                cargarProductosPorCategoria(categoriaActual);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al registrar la venta en la base de datos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una forma de pago",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarTicket() {
        modeloTicket.setRowCount(0);
        lblTotal.setText("0.00");
        txtPagaCon.setText("");
        lblCambio.setText("0.00");
        txtReferenciaTarjeta.setText("");
    }

    // ============================
    // MAIN DE PRUEBA
    // ============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPuntoDeVenta().setVisible(true));
    }
}
