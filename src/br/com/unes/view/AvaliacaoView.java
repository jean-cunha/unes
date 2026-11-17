package br.com.unes.view;

import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.unes.controller.AvaliacaoController;
import br.com.unes.model.Avaliacao;
import br.com.unes.util.Formato;

public class AvaliacaoView extends CrudView<Avaliacao> {

	private final AvaliacaoController controller = new AvaliacaoController();
	private Map<Integer, String> turmas = Map.of();
	private final JComboBox<Opcao> turma = comboOpcao();
	private final JTextField descricao = txt(), valorMax = txt(), data = txt(), ordem = txt();
	private final JComboBox<String> tipo = comboTexto(false, "PROVA", "TRABALHO", "SEMINARIO", "OUTRO");

	public AvaliacaoView() {
		campo("Turma*", turma);
		campo("Descrição*", descricao);
		campo("Tipo*", tipo);
		campo("Valor máximo*", valorMax);
		campo("Data (dd/mm/aaaa)", data);
		campo("Ordem*", ordem);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Turma", "Ordem", "Descrição", "Tipo", "Valor máx.", "Data" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		List<Opcao> t = Opcoes.turmas();
		turmas = Opcoes.mapa(t);
		carregarCombo(turma, t, true);
	}

	@Override protected List<Avaliacao> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Avaliacao a) {
		return new Object[] { turmas.get(a.getTurma_id()), a.getOrdem(), a.getDescricao(), a.getTipo(),
				Formato.decimal(a.getValor_maximo()), Formato.data(a.getData_avaliacao()) };
	}

	@Override
	protected void preencher(Avaliacao a) {
		if (a == null) {
			limpar(descricao, valorMax, data, ordem);
			selecionarId(turma, null);
			selecionar(tipo, "PROVA");
			return;
		}
		selecionarId(turma, a.getTurma_id());
		descricao.setText(a.getDescricao());
		selecionar(tipo, a.getTipo());
		valorMax.setText(Formato.decimal(a.getValor_maximo()));
		data.setText(Formato.data(a.getData_avaliacao()));
		ordem.setText(String.valueOf(a.getOrdem()));
	}

	@Override
	protected Avaliacao ler() {
		Avaliacao a = atual != null ? atual : new Avaliacao();
		a.setTurma_id(idObrigatorio(turma));
		a.setDescricao(valor(descricao));
		a.setTipo(selecionado(tipo));
		a.setValor_maximo(Formato.paraDecimal(valorMax.getText(), "Valor máximo"));
		a.setData_avaliacao(Formato.paraData(data.getText(), "Data da avaliação"));
		a.setOrdem(Formato.paraInt(ordem.getText(), "Ordem"));
		return a;
	}

	@Override protected void gravar(Avaliacao a) throws Exception { controller.salvar(a); }
	@Override protected String rotuloRemover() { return "Excluir"; }
	@Override protected void remover(Avaliacao a) throws Exception { controller.excluir(a); }
}
