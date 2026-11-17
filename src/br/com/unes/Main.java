package br.com.unes;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.com.unes.view.LoginView;
import br.com.unes.view.MainView;

/** Ponto de entrada da aplicação. */
public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignorada) {
			}
			if (new LoginView().exibir()) new MainView().setVisible(true);
			else System.exit(0);
		});
	}
}
