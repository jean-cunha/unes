package br.com.unes.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import br.com.unes.controller.DisciplinaController;
import br.com.unes.model.Disciplina;
import br.com.unes.util.Formato;

public class DisciplinaView extends CrudView<Disciplina> {

	private final DisciplinaController controller = new DisciplinaController();
	private Map<Integer, String> cursos = Map.of();
	private final JTextField codigo = txt(), nome = txt(), carga = txt(), semestre = txt();
	private final JComboBox<Opcao> curso = comboOpcao();
	private final JComboBox<String> status = comboTexto(false, "ATIVO", "INATIVO");

	private final DefaultListModel<Opcao> prereqModelo = new DefaultListModel<>();
	private final JList<Opcao> prereqLista = new JList<>(prereqModelo);
	private final JComboBox<Opcao> prereqNovo = comboOpcao();

	public DisciplinaView() {
		campo("Código*", codigo);
		campo("Nome*", nome);
		campo("Carga horária (h)*", carga);
		campo("Semestre sugerido*", semestre);
		campo("Curso*", curso);
		campo("Status*", status);

		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setBorder(javax.swing.BorderFactory.createTitledBorder("Pré-requisitos (salve a disciplina antes de incluir)"));
		prereqLista.setVisibleRowCount(4);
		p.add(new JScrollPane(prereqLista), BorderLayout.CENTER);
		JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton add = new JButton("Adicionar");
		JButton rem = new JButton("Remover selecionado");
		add.addActionListener(e -> adicionarPrerequisito());
		rem.addActionListener(e -> removerPrerequisito());
		acoes.add(new JLabel("Disciplina:"));
		acoes.add(prereqNovo);
		acoes.add(add);
		acoes.add(rem);
		p.add(acoes, BorderLayout.SOUTH);
		largo(null, p);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Código", "Nome", "Carga", "Semestre", "Curso", "Status" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		List<Opcao> l = Opcoes.cursos();
		cursos = Opcoes.mapa(l);
		carregarCombo(curso, l, true);
		carregarCombo(prereqNovo, Opcoes.disciplinas(), false);
	}

	@Override protected List<Disciplina> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Disciplina d) {
		return new Object[] { d.getCodigo(), d.getNome(), d.getCarga_horaria(), d.getSemestre_sugerido(),
				cursos.get(d.getCurso_id()), d.getStatus_disciplina() };
	}

	@Override
	protected void preencher(Disciplina d) {
		prereqModelo.clear();
		if (d == null) {
			limpar(codigo, nome, carga, semestre);
			selecionarId(curso, null);
			selecionar(status, "ATIVO");
			return;
		}
		codigo.setText(d.getCodigo());
		nome.setText(d.getNome());
		carga.setText(String.valueOf(d.getCarga_horaria()));
		semestre.setText(String.valueOf(d.getSemestre_sugerido()));
		selecionarId(curso, d.getCurso_id());
		selecionar(status, d.getStatus_disciplina());
		try {
			for (Disciplina p : controller.listarPrerequisitos(d.getId()))
				prereqModelo.addElement(new Opcao(p.getId(), p.getCodigo() + " - " + p.getNome()));
		} catch (Exception e) {
			erro(e);
		}
	}

	@Override
	protected Disciplina ler() {
		Disciplina d = atual != null ? atual : new Disciplina();
		d.setCodigo(valor(codigo));
		d.setNome(valor(nome));
		d.setCarga_horaria(Formato.paraInt(carga.getText(), "Carga horária"));
		d.setSemestre_sugerido(Formato.paraInt(semestre.getText(), "Semestre sugerido"));
		d.setCurso_id(idObrigatorio(curso));
		d.setStatus_disciplina(selecionado(status));
		return d;
	}

	@Override protected void gravar(Disciplina d) throws Exception { controller.salvar(d); }
	@Override protected String rotuloRemover() { return "Inativar"; }
	@Override protected void remover(Disciplina d) throws Exception { controller.inativar(d); }

	private void adicionarPrerequisito() {
		if (atual == null) {
			JOptionPane.showMessageDialog(this, "Selecione uma disciplina na tabela primeiro.");
			return;
		}
		Integer id = idDe(prereqNovo);
		if (id == null) return;
		try {
			controller.adicionarPrerequisito(atual.getId(), id);
			preencher(atual);
		} catch (Exception e) {
			erro(e);
		}
	}

	private void removerPrerequisito() {
		Opcao o = prereqLista.getSelectedValue();
		if (atual == null || o == null) return;
		try {
			controller.removerPrerequisito(atual.getId(), o.id());
			preencher(atual);
		} catch (Exception e) {
			erro(e);
		}
	}
}
