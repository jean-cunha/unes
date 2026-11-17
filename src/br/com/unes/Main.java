package br.com.unes;

import java.sql.DriverManager;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.com.unes.util.Conexao;
import br.com.unes.view.LoginView;
import br.com.unes.view.MainView;

/** Inicia o sistema: pede usuário e senha do MySQL, conecta e abre a tela inicial. */
public class Main {

	public static void main(String[] args) {
		JTextField usuario = new JTextField("root");
		JPasswordField senha = new JPasswordField();

		while (true) {
			int r = JOptionPane.showConfirmDialog(null, new Object[] { "Usuário do MySQL", usuario, "Senha", senha },
					"Conectar ao banco", JOptionPane.OK_CANCEL_OPTION);
			if (r != JOptionPane.OK_OPTION) return;

			Conexao.usuario = usuario.getText().trim();
			Conexao.senha = new String(senha.getPassword());
			try {
				DriverManager.getConnection(Conexao.URL, Conexao.usuario, Conexao.senha).close();
				break;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Não foi possível conectar:\n" + e.getMessage());
			}
		}

		if (new LoginView().exibir()) new MainView().setVisible(true);
	}
}
