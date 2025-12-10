package sazonpos.vista;

import sazonpos.modelo.Usuario;
import sazonpos.modelo.UsuarioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmUsuariosAdmin extends JFrame {

    private JTable tblUsuarios;
    private DefaultTableModel modeloUsuarios;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtUsuarioLogin;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnInactivar;
    private JButton btnEliminar;
    private JButton btnRefrescar;

    private UsuarioDAO usuarioDAO;

    public FrmUsuariosAdmin() {
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        setTitle("Usuarios del Sistema - Admin");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // Tabla
        modeloUsuarios = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Usuario", "Rol", "Activo"}, 0
        ) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblUsuarios = new JTable(modeloUsuarios);
        JScrollPane scroll = new JScrollPane(tblUsuarios);
        scroll.setBorder(BorderFactory.createTitledBorder("Usuarios"));
        add(scroll, BorderLayout.CENTER);

        // Panel edición
        JPanel panelEdit = new JPanel(new GridBagLayout());
        panelEdit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("ID:"), c);
        txtId = new JTextField(5);
        txtId.setEditable(false);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtId, c);

        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Nombre:"), c);
        txtNombre = new JTextField(20);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtNombre, c);

        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Usuario login:"), c);
        txtUsuarioLogin = new JTextField(15);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtUsuarioLogin, c);

        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Contraseña:"), c);
        txtPassword = new JPasswordField(15);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(txtPassword, c);

        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Rol:"), c);
        cmbRol = new JComboBox<>(new String[]{"ADMIN", "CAJERO"});
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(cmbRol, c);

        c.gridx = 0; c.gridy = fila;
        panelEdit.add(new JLabel("Activo:"), c);
        chkActivo = new JCheckBox("Sí");
        chkActivo.setSelected(true);
        c.gridx = 1; c.gridy = fila++;
        panelEdit.add(chkActivo, c);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnInactivar = new JButton("Inactivar");
        btnEliminar = new JButton("Eliminar");
        btnRefrescar = new JButton("Refrescar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnInactivar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);

        c.gridx = 0; c.gridy = fila;
        c.gridwidth = 2;
        panelEdit.add(panelBotones, c);

        add(panelEdit, BorderLayout.SOUTH);

        // Eventos
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnInactivar.addActionListener(e -> inactivarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

    private void cargarTabla() {
        modeloUsuarios.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarTodos();
        for (Usuario u : lista) {
            modeloUsuarios.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNombre(),
                    u.getUsuarioLogin(),
                    u.getRol(),
                    u.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tblUsuarios.getSelectedRow();
        if (fila == -1) return;

        txtId.setText(modeloUsuarios.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloUsuarios.getValueAt(fila, 1).toString());
        txtUsuarioLogin.setText(modeloUsuarios.getValueAt(fila, 2).toString());
        cmbRol.setSelectedItem(modeloUsuarios.getValueAt(fila, 3).toString());
        chkActivo.setSelected("Sí".equals(modeloUsuarios.getValueAt(fila, 4).toString()));
        txtPassword.setText("");
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtUsuarioLogin.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedItem("CAJERO");
        chkActivo.setSelected(true);
        tblUsuarios.clearSelection();
    }

    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim();
        String usuarioLogin = txtUsuarioLogin.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String rol = (String) cmbRol.getSelectedItem();

        if (nombre.isEmpty() || usuarioLogin.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre y usuario son obligatorios",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtId.getText().isEmpty() && password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Contraseña obligatoria para nuevo usuario",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setUsuarioLogin(usuarioLogin);
        u.setRol(rol);
        u.setActivo(chkActivo.isSelected());
        if (!password.isEmpty()) {
            u.setPasswordHash(password);
        }

        boolean ok;
        if (txtId.getText().isEmpty()) {
            ok = usuarioDAO.insertar(u);
        } else {
            u.setIdUsuario(Integer.parseInt(txtId.getText()));
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "En este ejemplo, al editar coloca una nueva contraseña.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ok = usuarioDAO.actualizar(u);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Usuario guardado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar usuario",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inactivarUsuario() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un usuario",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        int r = JOptionPane.showConfirmDialog(this,
                "¿Inactivar usuario ID " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        boolean ok = usuarioDAO.inactivar(id);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Usuario inactivado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo inactivar",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un usuario",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar usuario ID " + id + "? (solo si no tiene ventas)",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        boolean ok = usuarioDAO.eliminar(id);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Usuario eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar (puede tener ventas ligadas)",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
