package br.com.unes.view;

import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.unes.controller.MatriculaController;
import br.com.unes.model.Matricula;
import br.com.unes.util.Formato;

public class MatriculaView extends CrudView<Matricula> {

	private final MatriculaController controller = new MatriculaController();
	private Map<Integer, String> alunos = Map.of(), turmas = Map.of();
	private final JComboBox<Opcao> aluno = comboOpcao(), turma = comboOpcao();
	private final JTextField data = txt(), media = txt(), frequencia = txt();
	private final JComboBox<String> situacao = comboTexto(false, "ATIVA", "TRANCADA", "CANCELADA", "APROVADO",
			"REPROVADO");

	public MatriculaView() {
		media.setEditable(false);
		frequencia.setEditable(false);
		campo("Aluno*", aluno);
		campo("Turma*", turma);
		campo("Data* (dd/mm/aaaa)", data);
		campo("Situação*", situacao);
		campo("Média final (calculada)", media);
		campo("Frequência % (calculada)", frequencia);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Aluno", "Turma", "Data", "Situação", "Média", "Freq. %" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		List<Opcao> a = Opcoes.alunos(), t = Opcoes.turmas();
		alunos = Opcoes.mapa(a);
		turmas = Opcoes.mapa(t);
		carregarCombo(aluno, a, true);
		carregarCombo(turma, t, true);
	}

	@Override protected List<Matricula> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Matricula m) {
		return new Object[] { alunos.get(m.getAluno_id()), turmas.get(m.getTurma_id()),
				Formato.data(m.getData_matricula()), m.getSituacao(), Formato.decimal(m.getMedia_final()),
				Formato.decimal(m.getFrequencia_percentual()) };
	}

	@Override
	protected void preencher(Matricula m) {
		if (m == null) {
			selecionarId(aluno, null);
			selecionarId(turma, null);
			data.setText(Formato.data(new java.sql.Date(System.currentTimeMillis())));
			selecionar(situacao, "ATIVA");
			limpar(media, frequencia);
			return;
		}
		selecionarId(aluno, m.getAluno_id());
		selecionarId(turma, m.getTurma_id());
		data.setText(Formato.data(m.getData_matricula()));
		selecionar(situacao, m.getSituacao());
		media.setText(Formato.decimal(m.getMedia_final()));
		frequencia.setText(Formato.decimal(m.getFrequencia_percentual()));
	}

	@Override
	protected Matricula ler() {
		Matricula m = atual != null ? atual : new Matricula();
		m.setAluno_id(idObrigatorio(aluno));
		m.setTurma_id(idObrigatorio(turma));
		m.setData_matricula(Formato.paraData(data.getText(), "Data da matrícula"));
		m.setSituacao(selecionado(situacao));
		return m;
	}

	@Override protected void gravar(Matricula m) throws Exception { controller.salvar(m); }
	@Override protected String rotuloRemover() { return "Cancelar matrícula"; }
	@Override protected void remover(Matricula m) throws Exception { controller.cancelar(m); }
}
