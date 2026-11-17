package br.com.unes.view;

import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import br.com.unes.controller.FrequenciaController;
import br.com.unes.model.Frequencia;
import br.com.unes.util.Formato;

/** Registro de presença por aula de uma matrícula (selecionada no filtro). */
public class FrequenciaView extends CrudView<Frequencia> {

	private final FrequenciaController controller = new FrequenciaController();
	private final JComboBox<Opcao> matricula = comboOpcao();
	private final JTextField data = txt();
	private final JCheckBox presente = new JCheckBox("Presente", true);

	public FrequenciaView() {
		filtro.add(new JLabel("Matrícula:"));
		filtro.add(matricula);
		matricula.addActionListener(e -> aoFiltrar());
		campo("Data da aula* (dd/mm/aaaa)", data);
		campo("", presente);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Data da aula", "Presente" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		carregarCombo(matricula, Opcoes.matriculas(), true);
	}

	@Override
	protected List<Frequencia> carregar() throws Exception {
		Integer mid = idDe(matricula);
		return mid == null ? Collections.emptyList() : controller.listarPorMatricula(mid);
	}

	@Override
	protected Object[] linha(Frequencia f) {
		return new Object[] { Formato.data(f.getData_aula()), f.isPresente() ? "Sim" : "Não" };
	}

	@Override
	protected void preencher(Frequencia f) {
		if (f == null) {
			data.setText(Formato.data(new java.sql.Date(System.currentTimeMillis())));
			presente.setSelected(true);
			return;
		}
		data.setText(Formato.data(f.getData_aula()));
		presente.setSelected(f.isPresente());
	}

	@Override
	protected Frequencia ler() {
		Formato.exigir(idDe(matricula) != null, "Selecione a matrícula no filtro.");
		Frequencia f = atual != null ? atual : new Frequencia();
		f.setMatricula_id(idObrigatorio(matricula));
		f.setData_aula(Formato.paraData(data.getText(), "Data da aula"));
		f.setPresente(presente.isSelected());
		return f;
	}

	@Override protected void gravar(Frequencia f) throws Exception { controller.salvar(f); }
	@Override protected String rotuloRemover() { return "Excluir"; }
	@Override protected void remover(Frequencia f) throws Exception { controller.excluir(f); }
}
