package sazonpos.vista;

import sazonpos.modelo.Producto;
import sazonpos.modelo.ProductoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class FrmInventarioAdmin extends JFrame {

    private JTable tblProductos;
    private DefaultTableModel modeloProductos;

    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JCheckBox chkActivo;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnRefrescar;

    private ProductoDAO productoDAO;

    public FrmInventarioAdmin() {
        this.productoDAO = new ProductoDAO();
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        setTitle("Inventario / Productos - Admin");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // =======================
        // TABLA
        // =======================
        modeloProductos = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Categoría", "Precio", "Stock", "Activo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProductos = new JTable(modeloProductos);
        JScrollPane scroll = new JScrollPane(tblProductos);
        add(scroll, BorderLayout.CENTER);

        // =======================
        // PANEL EDICIÓN
        // =======================
        JPanel panelEdit = new JPanel();
        panelEdit.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        // ID (solo lectura)
        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("ID:"), c);
        txtId = new JTextField(5);
        txtId.setEditable(false);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtId, c);

        // Nombre
        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Nombre:"), c);
        txtNombre = new JTextField(25);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtNombre, c);

        // Categoría
        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Categoría:"), c);
        cmbCategoria = new JComboBox<>(new String[]{
                "1 - BEBIDA",
                "2 - PAPAS",
                "3 - DULCES",
                "4 - COMIDA_PREPARADA"
        });
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(cmbCategoria, c);

        // Precio
        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Precio venta:"), c);
        txtPrecio = new JTextField(10);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtPrecio, c);

        // Stock
        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Stock:"), c);
        txtStock = new JTextField(10);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtStock, c);

        // Activo
        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Activo:"), c);
        chkActivo = new JCheckBox("Sí");
        chkActivo.setSelected(true);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(chkActivo, c);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnRefrescar = new JButton("Refrescar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);

        c.gridx = 0; c.gridy = fila;
        c.gridwidth = 2;
        panelEdit.add(panelBotones, c);

        add(panelEdit, BorderLayout.SOUTH);

        // =======================
        // EVENTOS
        // =======================
        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

    // =======================
    // CARGAR TABLA
    // =======================
    private void cargarTabla() {
        modeloProductos.setRowCount(0);
        List<Producto> lista = productoDAO.listarTodos();
        for (Producto p : lista) {
            modeloProductos.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getNombreCategoria(),
                    p.getPrecioVenta(),
                    p.getStockActual(),
                    p.isActivo() ? "Sí" : "No"
            });
        }
    }

    // =======================
    // CARGAR SELECCIÓN EN FORM
    // =======================
    private void cargarSeleccionEnFormulario() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) return;

        txtId.setText(modeloProductos.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloProductos.getValueAt(fila, 1).toString());

        String catNombre = modeloProductos.getValueAt(fila, 2).toString();
        if (catNombre.contains("BEBIDA")) cmbCategoria.setSelectedIndex(0);
        else if (catNombre.contains("PAPAS")) cmbCategoria.setSelectedIndex(1);
        else if (catNombre.contains("DULCES")) cmbCategoria.setSelectedIndex(2);
        else cmbCategoria.setSelectedIndex(3); // COMIDA_PREPARADA u otra

        txtPrecio.setText(modeloProductos.getValueAt(fila, 3).toString());
        txtStock.setText(modeloProductos.getValueAt(fila, 4).toString());
        chkActivo.setSelected("Sí".equals(modeloProductos.getValueAt(fila, 5).toString()));
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPrecio.setText("");
        txtStock.setText("0");
        chkActivo.setSelected(true);
        tblProductos.clearSelection();
    }

    // =======================
    // GUARDAR (INSERT/UPDATE)
    // =======================
    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa nombre, precio y stock", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCategoria;
        switch (cmbCategoria.getSelectedIndex()) {
            case 0: idCategoria = 1; break; // BEBIDA
            case 1: idCategoria = 2; break; // PAPAS
            case 2: idCategoria = 3; break; // DULCES
            default: idCategoria = 4; break; // COMIDA_PREPARADA
        }

        BigDecimal precio;
        int stock;
        try {
            precio = new BigDecimal(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Precio o stock no válidos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setIdCategoria(idCategoria);
        p.setIdProveedor(0); // sin proveedor específico (si en tu BD es NOT NULL, pon uno por defecto)
        p.setPrecioVenta(precio);
        p.setCostoUnitario(null);    // si tu modelo no usa costo, lo dejamos null
        p.setStockActual(stock);
        p.setActivo(chkActivo.isSelected());

        boolean ok;
        if (txtId.getText().isEmpty()) {
            // NUEVO
            ok = productoDAO.insertar(p);
        } else {
            // EDITAR
            p.setIdProducto(Integer.parseInt(txtId.getText()));
            ok = productoDAO.actualizar(p);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Producto guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =======================
    // ELIMINAR
    // =======================
    private void eliminarProducto() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar producto ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        boolean ok = productoDAO.eliminar(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede estar en alguna venta o es baja lógica)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =======================
    // MAIN DE PRUEBA (opcional)
    // =======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmInventarioAdmin().setVisible(true);
        });
    }
}