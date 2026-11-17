package br.com.unes.view;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.com.unes.controller.UsuarioController;
import br.com.unes.model.Usuario;

public class UsuarioView extends CrudView<Usuario> {

	private final UsuarioController controller = new UsuarioController();
	private final JTextField login = txt();
	private final JPasswordField senha = new JPasswordField(18);
	private final JComboBox<String> perfil = comboTexto(false, "SECRETARIA", "PEDAGOGIA", "PROFESSOR", "ALUNO");
	private final JCheckBox ativo = new JCheckBox("Ativo", true);
	private final JComboBox<Opcao> aluno = comboOpcao(), professor = comboOpcao();

	public UsuarioView() {
		campo("Login*", login);
		campo("Senha (vazio mantém)", senha);
		campo("Perfil*", perfil);
		campo("", ativo);
		campo("Aluno (perfil ALUNO)", aluno);
		campo("Professor (perfil PROFESSOR)", professor);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Login", "Perfil", "Ativo", "Último acesso" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		carregarCombo(aluno, Opcoes.alunos(), true);
		carregarCombo(professor, Opcoes.professores(), true);
	}

	@Override protected List<Usuario> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Usuario u) {
		return new Object[] { u.getLogin(), u.getPerfil(), u.isAtivo() ? "Sim" : "Não", u.getUltimo_acesso() };
	}

	@Override
	protected void preencher(Usuario u) {
		senha.setText("");
		if (u == null) {
			login.setText("");
			selecionar(perfil, "SECRETARIA");
			ativo.setSelected(true);
			selecionarId(aluno, null);
			selecionarId(professor, null);
			return;
		}
		login.setText(u.getLogin());
		selecionar(perfil, u.getPerfil());
		ativo.setSelected(u.isAtivo());
		selecionarId(aluno, u.getAluno_id());
		selecionarId(professor, u.getProfessor_id());
	}

	@Override
	protected Usuario ler() {
		Usuario u = atual != null ? atual : new Usuario();
		u.setLogin(valor(login));
		u.setPerfil(selecionado(perfil));
		u.setAtivo(ativo.isSelected());
		String p = selecionado(perfil);
		u.setAluno_id("ALUNO".equals(p) ? idDe(aluno) : null);
		u.setProfessor_id("PROFESSOR".equals(p) ? idDe(professor) : null);
		return u;
	}

	@Override
	protected void gravar(Usuario u) throws Exception {
		controller.salvar(u, new String(senha.getPassword()));
	}

	@Override protected String rotuloRemover() { return "Desativar"; }
	@Override protected void remover(Usuario u) throws Exception { controller.definirAtivo(u, false); }
}
