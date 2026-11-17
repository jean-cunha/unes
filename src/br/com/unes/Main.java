package br.com.unes;

import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.com.unes.util.Conexao;
import br.com.unes.view.LoginView;
import br.com.unes.view.MainView;

/** Ponto de entrada: conecta ao MySQL local (usuário/senha digitados), depois abre login e janela principal. */
public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignorada) {
			}
			if (!conectarBanco()) System.exit(0);
			if (new LoginView().exibir()) new MainView().setVisible(true);
			else System.exit(0);
		});
	}

	/** Pede usuário e senha do MySQL até conectar; retorna false se o usuário cancelar. */
	private static boolean conectarBanco() {
		JTextField usuario = new JTextField("root");
		JPasswordField senha = new JPasswordField();
		Object[] campos = { "Banco de dados MySQL local (localhost:3306, schema 'unes')", "Usuário", usuario,
				"Senha", senha };

		while (true) {
			int r = JOptionPane.showConfirmDialog(null, campos, "Conectar ao banco", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (r != JOptionPane.OK_OPTION) return false;

			char[] digitada = senha.getPassword();
			try {
				Conexao.configurar(usuario.getText().trim(), digitada);
				return true;
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Não foi possível conectar:\n" + e.getMessage(),
						"Erro de conexão", JOptionPane.ERROR_MESSAGE);
				senha.setText("");
			} finally {
				Arrays.fill(digitada, '\0');
			}
		}
	}
}
