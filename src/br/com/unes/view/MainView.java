package br.com.unes.view;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import br.com.unes.controller.UsuarioController;
import br.com.unes.util.Sessao;

/** Janela principal: uma aba por cadastro, conforme o perfil do usuário logado. */
public class MainView extends JFrame {

	private final JTabbedPane abas = new JTabbedPane();

	public MainView() {
		setTitle("UNES - Sistema Acadêmico  |  " + Sessao.getUsuario().getLogin() + " (" + Sessao.getUsuario().getPerfil() + ")");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1100, 720);
		setLocationRelativeTo(null);

		boolean gestao = Sessao.temPerfil("SECRETARIA", "PEDAGOGIA");
		boolean docente = Sessao.temPerfil("PROFESSOR");

		if (gestao) {
			aba("Alunos", new AlunoView());
			aba("Professores", new ProfessorView());
			aba("Cursos", new CursoView());
			aba("Disciplinas", new DisciplinaView());
			aba("Turmas", new TurmaView());
			aba("Matrículas", new MatriculaView());
			aba("Documentos", new DocumentoView());
		}
		if (gestao || docente) {
			aba("Avaliações", new AvaliacaoView());
			aba("Notas", new NotaView());
			aba("Frequência", new FrequenciaView());
		}
		if (Sessao.temPerfil("SECRETARIA")) {
			aba("Usuários", new UsuarioView());
			aba("Auditoria", new AuditoriaView());
		}

		abas.addChangeListener(e -> {
			if (abas.getSelectedComponent() instanceof CrudView<?> v) v.atualizar();
		});
		setContentPane(abas);

		JMenuBar barra = new JMenuBar();
		JMenu sistema = new JMenu("Sistema");
		JMenuItem sair = new JMenuItem("Sair");
		sair.addActionListener(e -> {
			new UsuarioController().sair();
			dispose();
			System.exit(0);
		});
		sistema.add(sair);
		barra.add(sistema);
		setJMenuBar(barra);

		if (abas.getTabCount() > 0 && abas.getComponentAt(0) instanceof CrudView<?> v) v.atualizar();
	}

	private void aba(String titulo, CrudView<?> view) {
		abas.addTab(titulo, view);
	}
}
