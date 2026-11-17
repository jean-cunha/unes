package br.com.unes.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.com.unes.controller.UsuarioController;
import br.com.unes.model.Usuario;

/** Diálogo de login. Na primeira execução (sem usuários) cria o administrador inicial. */
public class LoginView extends JDialog {

	private final UsuarioController controller = new UsuarioController();
	private final JTextField login = new JTextField(16);
	private final JPasswordField senha = new JPasswordField(16);
	private boolean autenticado;

	public LoginView() {
		setTitle("UNES - Acesso");
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(6, 8, 6, 8);
		g.gridx = 0; g.gridy = 0; add(new JLabel("Login"), g);
		g.gridx = 1; add(login, g);
		g.gridx = 0; g.gridy = 1; add(new JLabel("Senha"), g);
		g.gridx = 1; add(senha, g);
		JButton entrar = new JButton("Entrar");
		entrar.addActionListener(e -> entrar());
		g.gridx = 1; g.gridy = 2; g.anchor = GridBagConstraints.EAST; add(entrar, g);
		getRootPane().setDefaultButton(entrar);
		pack();
		setLocationRelativeTo(null);
	}

	/** Mostra o diálogo e retorna true se o usuário autenticou. */
	public boolean exibir() {
		try {
			if (!controller.existeAlgumUsuario()) criarAdministradorInicial();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro ao acessar o banco: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		setVisible(true);
		return autenticado;
	}

	private void criarAdministradorInicial() throws Exception {
		JTextField l = new JTextField("admin");
		JPasswordField s = new JPasswordField();
		Object[] campos = { "Nenhum usuário cadastrado. Crie o administrador (perfil SECRETARIA):", "Login", l,
				"Senha (mín. 6 caracteres)", s };
		while (true) {
			int r = JOptionPane.showConfirmDialog(null, campos, "Primeiro acesso", JOptionPane.OK_CANCEL_OPTION);
			if (r != JOptionPane.OK_OPTION) return;
			Usuario u = new Usuario();
			u.setLogin(l.getText().trim());
			u.setPerfil("SECRETARIA");
			u.setAtivo(true);
			try {
				controller.salvar(u, new String(s.getPassword()));
				return;
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void entrar() {
		try {
			Usuario u = controller.autenticar(login.getText(), new String(senha.getPassword()));
			if ("ALUNO".equals(u.getPerfil())) {
				controller.sair();
				throw new IllegalArgumentException("O perfil ALUNO não tem acesso ao painel administrativo.");
			}
			autenticado = true;
			dispose();
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
			senha.setText("");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
}
