package sazonpos.vista;

import sazonpos.modelo.Usuario;
import sazonpos.modelo.UsuarioDAO;

import javax.swing.*;
import java.awt.*;

public class FrmLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;

    private UsuarioDAO usuarioDAO;

    public FrmLogin() {
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - Sazón de Ali");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 5, 5));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelCampos.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panelCampos.add(txtUsuario);

        panelCampos.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panelCampos.add(txtPassword);

        add(panelCampos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnIngresar = new JButton("Ingresar");
        btnSalir = new JButton("Salir");

        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnIngresar.addActionListener(e -> intentarLogin());
        btnSalir.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> intentarLogin());
    }

    private void intentarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa usuario y contraseña",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = usuarioDAO.validarLogin(user, pass);
        if (u != null) {
            FrmMenuPrincipal menu = new FrmMenuPrincipal(u);
            menu.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Look&Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
