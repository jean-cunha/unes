package br.com.unes.view;

import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.unes.controller.TurmaController;
import br.com.unes.model.Turma;
import br.com.unes.util.Formato;

public class TurmaView extends CrudView<Turma> {

	private final TurmaController controller = new TurmaController();
	private Map<Integer, String> disciplinas = Map.of(), professores = Map.of();
	private final JTextField codigo = txt(), periodo = txt(), inicio = txt(), fim = txt(), sala = txt(),
			vagas = txt(), media = txt(), freq = txt();
	private final JComboBox<Opcao> disciplina = comboOpcao(), professor = comboOpcao();
	private final JComboBox<String> turno = comboTexto(true, "MATUTINO", "VESPERTINO", "NOTURNO", "INTEGRAL"),
			status = comboTexto(false, "ABERTA", "EM_ANDAMENTO", "ENCERRADA");

	public TurmaView() {
		campo("Código*", codigo);
		campo("Período letivo* (AAAA.S)", periodo);
		campo("Disciplina*", disciplina);
		campo("Professor*", professor);
		campo("Início* (dd/mm/aaaa)", inicio);
		campo("Fim* (dd/mm/aaaa)", fim);
		campo("Turno", turno);
		campo("Sala", sala);
		campo("Vagas*", vagas);
		campo("Média de aprovação*", media);
		campo("Frequência mínima (%)*", freq);
		campo("Status*", status);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Código", "Período", "Disciplina", "Professor", "Vagas", "Status" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		List<Opcao> d = Opcoes.disciplinas(), p = Opcoes.professores();
		disciplinas = Opcoes.mapa(d);
		professores = Opcoes.mapa(p);
		carregarCombo(disciplina, d, true);
		carregarCombo(professor, p, true);
	}

	@Override protected List<Turma> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Turma t) {
		return new Object[] { t.getCodigo(), t.getPeriodo_letivo(), disciplinas.get(t.getDisciplina_id()),
				professores.get(t.getProfessor_id()), t.getVagas(), t.getStatus() };
	}

	@Override
	protected void preencher(Turma t) {
		if (t == null) {
			limpar(codigo, periodo, inicio, fim, sala, vagas);
			media.setText("6.00");
			freq.setText("75.00");
			selecionarId(disciplina, null);
			selecionarId(professor, null);
			selecionar(turno, null);
			selecionar(status, "ABERTA");
			return;
		}
		codigo.setText(t.getCodigo());
		periodo.setText(t.getPeriodo_letivo());
		selecionarId(disciplina, t.getDisciplina_id());
		selecionarId(professor, t.getProfessor_id());
		inicio.setText(Formato.data(t.getData_inicio()));
		fim.setText(Formato.data(t.getData_fim()));
		selecionar(turno, t.getTurno());
		sala.setText(t.getSala());
		vagas.setText(String.valueOf(t.getVagas()));
		media.setText(Formato.decimal(t.getMedia_aprovacao()));
		freq.setText(Formato.decimal(t.getFrequencia_minima()));
		selecionar(status, t.getStatus());
	}

	@Override
	protected Turma ler() {
		Turma t = atual != null ? atual : new Turma();
		t.setCodigo(valor(codigo));
		t.setPeriodo_letivo(valor(periodo));
		t.setDisciplina_id(idObrigatorio(disciplina));
		t.setProfessor_id(idObrigatorio(professor));
		t.setData_inicio(Formato.paraData(inicio.getText(), "Data de início"));
		t.setData_fim(Formato.paraData(fim.getText(), "Data de fim"));
		t.setTurno(selecionado(turno));
		t.setSala(valor(sala));
		t.setVagas(Formato.paraInt(vagas.getText(), "Vagas"));
		t.setMedia_aprovacao(Formato.paraDecimal(media.getText(), "Média de aprovação"));
		t.setFrequencia_minima(Formato.paraDecimal(freq.getText(), "Frequência mínima"));
		t.setStatus(selecionado(status));
		return t;
	}

	@Override protected void gravar(Turma t) throws Exception { controller.salvar(t); }
	@Override protected String rotuloRemover() { return "Excluir"; }
	@Override protected void remover(Turma t) throws Exception { controller.excluir(t); }
}
